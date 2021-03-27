package com.caputo.boxshare.service.engines;

import com.caputo.boxshare.builder.SearchResultBuilder;
import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CorsaroNero extends HtmlResultsReader implements SearchEngine {

  private final SearchResultBuilder srBuilder;

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
  public Optional<List<SearchResult>> search(String query, SearchMethod method) {
    logger.info("Searching for \"{}\" on {} ({})..", query, className, method);
    String url = BASE_URL + query;
    String rowSelector =
        "#left > table > tbody > tr > td:nth-child(2) > table .odd, "
            + "#left > table > tbody > tr > td:nth-child(2) > table .odd2";
    return getResults(url, rowSelector, method);
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
    String name = row.select(NAME_SELECTOR).text();
    String hash = row.select(HASH_SELECTOR).attr("value");
    String seeders = row.select(SEEDERS_SELECTOR).text();
    if (seeders.equals("n/a")) {
      seeders = "-1";
    }
    String size = row.select(SIZE_SELECTOR).text();
    if (name.isEmpty() | hash.isEmpty()) {
      logger.error("Failed to parse required information from row: {}", row);
      return null;
    } else {
      return srBuilder
          .setName(name)
          .setSeeders(Integer.parseInt(seeders))
          .setSize(size)
          .setInfoHash(hash)
          .setOrigin(this.getClass().getSimpleName())
          .build();
    }
    // TODO: Parse all available pages.
  }
}
