package com.caputo.boxshare.service.engines;

import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;

/**
 * A class to be extended by search engine classes that scrape search results from HTML pages. This
 * class provides multiple methods for common scraping actions shared by all engines, but expects
 * its children to implement specific ways to scrape individual search results. For an example of a
 * class that extends this abstract class, see {@link CorsaroNero} and {@link LeetX}.
 */
public abstract class HtmlResultsReader implements SearchEngine {

  Elements tableRows;

  @Value("${search.smart.minSeeders}")
  private int SMART_SEARCH_MIN_SEEDS;

  @Value("${search.smart.maxResults}")
  private int SMART_SEARCH_MAX_RESULTS;

  /**
   * @param url the URL of the search page.
   * @param rowSelector the CSS selector of the individual table rows that hold search results.
   * @param method the searching method to apply while parsing the results.
   * @return the deserialised search results.
   */
  protected Optional<List<SearchResult>> getResults(
      String url, String rowSelector, SearchMethod method) {
    getSearchPage(url).ifPresent(page -> tableRows = page.select(rowSelector));
    return parseTable(tableRows, method);
  }

  /**
   * Fetches the HTML document of the search page for a given query.
   *
   * @param url the URL of the search page.
   * @return the HTML document of the search page.
   */
  protected Optional<Document> getSearchPage(String url) {
    logger.info("Getting results page: {}", url);
    try {
      return Optional.of(Jsoup.connect(url).headers(headers).get());
    } catch (Exception e) {
      logger.error("Failed to get search page. URL=\"{}\", Error=\"{}\"", url, e.getMessage());
      return Optional.empty();
    }
  }

  /**
   * Parses a list of SearchResult objects from the HTML table rows of the search page.
   *
   * @param rows the HTML rows of the search page.
   * @param method the searching method to apply while parsing the results.
   * @return the populated SearchResult objects.
   */
  protected Optional<List<SearchResult>> parseTable(Elements rows, SearchMethod method) {
    logger.info("Parsing table..");
    List<SearchResult> results = new ArrayList<>();
    if (rows.size() < 1) {
      logger.error("No parsable row found.");
      return Optional.empty();
    }
    switch (method) {
      case QUICK:
        results.add(parseRow(rows.get(0)));
        break;
      case COMPLETE:
        rows.forEach(row -> results.add(parseRow(row)));
        break;
      case SMART:
        int totalParsed = 0;
        for (int i = 0; i < rows.size() && totalParsed < SMART_SEARCH_MAX_RESULTS; i++) {
          SearchResult result = parseRow(rows.get(i));
          if (result.getSeeders() >= SMART_SEARCH_MIN_SEEDS) {
            results.add(result);
            totalParsed++;
          }
        }
    }
    logger.info("{} results found.", results.size());
    return Optional.of(results);
  }
  /**
   * Parses a SearchResult object from an HTML table row.
   *
   * @param row an HTML row of the search page.
   * @return the populated SearchResult object.
   */
  protected abstract SearchResult parseRow(Element row);
}
