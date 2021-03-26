package com.caputo.boxshare.service.engines;

import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for parsing and deserialising the HTML search results of any engine.
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
   * @param method the searching method to apply while parsing the search results.
   * @return the deserialised search results.
   */
  protected List<SearchResult> getResults(String url, String rowSelector, SearchMethod method) {
    Document searchPage = getSearchPage(url);
    tableRows = searchPage.select(rowSelector);
    return parseTable(tableRows, method);
  }

  /**
   * Fetches the HTML document of the search page for a given query.
   *
   * @param url the URL of the search page.
   * @return the HTML document of the search page.
   */
  protected Document getSearchPage(String url) {
    logger.info("Getting results page: {}", url);
    Document searchPageHtml = null;
    try {
      searchPageHtml = Jsoup.connect(url).userAgent("Mozilla").get();
    } catch (Exception e) {
      logger.error("Failed to get search page: \"{}\". Error: {}", url, e);
    }
    return searchPageHtml;
  }

  /**
   * Parses a list of SearchResult objects from the HTML table rows of the search page.
   *
   * @param rows the HTML rows of the search page.
   * @param method the searching method to apply while parsing the search results.
   * @return the populated SearchResult objects.
   */
  protected List<SearchResult> parseTable(Elements rows, SearchMethod method) {
    logger.info("Parsing table..");
    List<SearchResult> results = new ArrayList<>();
    if (rows.size() > 0) {
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
    }
    logger.info("{} results found.", results.size());
    return results;
  }
  /**
   * Parses a SearchResult object from an HTML table row.
   *
   * @param row an HTML row of the search page.
   * @return the populated SearchResult object.
   */
  protected abstract SearchResult parseRow(Element row);
}
