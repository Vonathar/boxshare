package com.caputo.boxshare.service.engines;

import com.caputo.boxshare.builder.SearchResultBuilder;
import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CorsaroNero implements SearchEngine {

  private final SearchResultBuilder srBuilder;
  private final String BASE_URL = "https://ilcorsaronero.link/argh.php";
  Logger logger = LoggerFactory.getLogger(CorsaroNero.class);

  @Value("${search.smart.minSeeders}")
  private int SMART_SEARCH_MIN_SEEDS;

  @Value("${search.smart.maxResults}")
  private int SMART_SEARCH_MAX_RESULTS;

  public CorsaroNero(SearchResultBuilder srBuilder) {
    this.srBuilder = srBuilder;
  }

  /**
   * Queries CorsaroNero and fetches the deserialised search results.
   *
   * @param query the term to search on CorsaroNero.
   * @param method the searching method to apply while parsing the search results.
   * @return the deserialised search results.
   */
  public List<SearchResult> search(String query, SearchMethod method) {
    logger.info("Searching for \"{}\" on CorsaroNero ({})..", query, method);
    Document searchPage = getSearchPage(query);
    String ROWS_SELECTOR =
        "#left > table > tbody > tr > td:nth-child(2) > table .odd, "
            + "#left > table > tbody > tr > td:nth-child(2) > table .odd2";
    Elements tableRows = searchPage.select(ROWS_SELECTOR);
    return parseTable(tableRows, method);
  }

  /**
   * Fetches the HTML document of the search page for a given query.
   *
   * @param query the search term.
   * @return the HTML document of the search page.
   */
  private Document getSearchPage(String query) {
    logger.info("Getting search results..");
    Document searchPageHtml = null;
    try {
      String searchPageUrl = BASE_URL + "?search=" + query;
      searchPageHtml = Jsoup.connect(searchPageUrl).get();
    } catch (Exception e) {
      logger.error("Failed to get search page for query: \"{}\". Error: {}", query, e);
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
  private List<SearchResult> parseTable(Elements rows, SearchMethod method) {
    logger.info("Parsing table..");
    List<SearchResult> results = new ArrayList<>();
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
    logger.info("Successfully found {} search results.", results.size());
    return results;
  }

  /**
   * Parses a SearchResult object from an HTML table row.
   *
   * @param row an HTML row of the search page.
   * @return the populated SearchResult object.
   */
  private SearchResult parseRow(Element row) {
    logger.info("Parsing row..");
    String NAME_SELECTOR = "td:nth-child(2) a.tab";
    String SEEDERS_SELECTOR = "td:nth-child(6) > font";
    String SIZE_SELECTOR = "td:nth-child(3) > font";
    String HASH_SELECTOR = "td:nth-child(4) > form > input";
    int seeders = 0;
    try {
      seeders = Integer.parseInt(row.select(SEEDERS_SELECTOR).text());
    } catch (NumberFormatException e) {
      logger.info("Failed to get number of seeders, defaulting to 0.");
    }
    srBuilder
        .setName(row.select(NAME_SELECTOR).text())
        .setSeeders(seeders)
        .setSize(row.select(SIZE_SELECTOR).text())
        .setUser(null)
        .setInfoHash(row.select(HASH_SELECTOR).attr("value"))
        .setOrigin(this.getClass().getSimpleName());
    // TODO: Parse all available pages.
    return srBuilder.build();
  }
}
