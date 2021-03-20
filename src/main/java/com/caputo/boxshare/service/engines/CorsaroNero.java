package com.caputo.boxshare.service.engines;

import com.caputo.boxshare.builder.SearchResultBuilder;
import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CorsaroNero extends HtmlResultsReader implements SearchEngine {

  private final SearchResultBuilder srBuilder;
  Logger logger = LoggerFactory.getLogger(CorsaroNero.class);

  @Value("${search.engine.corsaroNero.baseUrl}")
  private String BASE_URL;

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
    String url = BASE_URL + query;
    String rowSelector =
        "#left > table > tbody > tr > td:nth-child(2) > table .odd, "
            + "#left > table > tbody > tr > td:nth-child(2) > table .odd2";
    return getResults(query, url, rowSelector, method);
  }

  /**
   * Parses a SearchResult object from an HTML table row.
   *
   * @param row an HTML row of the search page.
   * @return the populated SearchResult object.
   */
  protected SearchResult parseRow(Element row) {
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
        .setInfoHash(row.select(HASH_SELECTOR).attr("value"))
        .setOrigin(this.getClass().getSimpleName());
    // TODO: Parse all available pages.
    return srBuilder.build();
  }
}
