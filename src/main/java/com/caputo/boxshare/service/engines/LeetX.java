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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LeetX implements SearchEngine {

  private final SearchResultBuilder srBuilder;
  private final String BASE_URL = "https://1337x.torrentbay.to";
  Logger logger = LoggerFactory.getLogger(LeetX.class);

  @Value("${search.smart.minSeeders}")
  private int SMART_SEARCH_MIN_SEEDS;

  @Value("${search.smart.maxResults}")
  private int SMART_SEARCH_MAX_RESULTS;

  public LeetX(SearchResultBuilder srBuilder) {
    this.srBuilder = srBuilder;
  }

  /**
   * Queries 1337X and fetches the deserialised search results.
   *
   * @param query the term to search on TPB.
   * @param method the searching method to apply while parsing the search results.
   * @return the deserialised search results.
   */
  public List<SearchResult> search(String query, SearchMethod method) {
    logger.info("Searching for \"{}\" on 1337X..", query);
    Document searchPage = getSearchPage(query);
    String ROWS_SELECTOR = ".table-list tbody tr";
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
    logger.info("Getting HTML of search results..");
    Document searchPageHtml = null;
    try {
      String searchPageUrl = BASE_URL + "/srch?search=" + query;
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
    logger.info("Parsing HTML table with \"{}\" method..", method);
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
    logger.info("Parsing HTML row..");
    String NAME_SELECTOR = ".coll-1";
    String SEEDERS_SELECTOR = ".coll-2";
    String SIZE_SELECTOR = ".coll-4";
    String USER_SELECTOR = ".coll-5";
    String ANCHOR_SELECTOR = ".coll-1 a:nth-child(2)";
    String HASH_SELECTOR = ".infohash-box span";
    try {
      String resultPageUrl = BASE_URL + row.select(ANCHOR_SELECTOR).attr("href");
      Document resultPageHtml = Jsoup.connect(resultPageUrl).get();
      String infoHash = resultPageHtml.select(HASH_SELECTOR).text();
      srBuilder
          .setName(row.select(NAME_SELECTOR).text())
          .setSeeders(Integer.parseInt(row.select(SEEDERS_SELECTOR).text()))
          .setSize(row.select(SIZE_SELECTOR).text())
          .setUser(row.select(USER_SELECTOR).text())
          .setInfoHash(infoHash)
          .setOrigin(this.getClass().getSimpleName());
    } catch (IOException e) {
      logger.error("Failed to parse HTML row: \"{}\". Error: {}", row, e);
    }
    return srBuilder.build();
  }
}
