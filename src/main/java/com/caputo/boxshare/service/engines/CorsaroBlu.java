package com.caputo.boxshare.service.engines;

import com.caputo.boxshare.builder.SearchResultBuilder;
import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CorsaroBlu extends HtmlResultsReader implements SearchEngine {

  private final SearchResultBuilder srBuilder;

  @Value("${search.engine.corsaroBlu.baseUrl}")
  private String BASE_URL;

  public CorsaroBlu(SearchResultBuilder srBuilder) {
    this.srBuilder = srBuilder;
  }

  /**
   * Queries CorsaroBlu and fetches the deserialised search results.
   *
   * @param query the term to search on CorsaroNero.
   * @param method the searching method to apply while parsing the search results.
   * @return the deserialised search results.
   */
  public List<SearchResult> search(String query, SearchMethod method) {
    logger.info("Searching for \"{}\" on {} ({})..", query, className, method);
    String url = BASE_URL + query + "&&order=5&by=2";
    String rowSelector =
        "#Mcol .block .block-content div div div table tbody table tbody tr:nth-child(2n+3)";
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
    String NAME_SELECTOR = "td:nth-child(2) > a";
    String SEEDERS_SELECTOR = "td:nth-child(6)";
    String SIZE_SELECTOR = "td:nth-child(10)";
    String HASH_SELECTOR = "td:nth-child(4) > a";
    String name = row.select(NAME_SELECTOR).text();
    String hash =
        row.select(HASH_SELECTOR)
            .attr("href")
            .replace("download.php?id=", "")
            .replace("magnet:?xt=urn:btih:", "");
    hash = hash.substring(0, Math.min(hash.length(), 40));
    ;
    String seeders = row.select(SEEDERS_SELECTOR).text();
    String size = row.select(SIZE_SELECTOR).text();
    if (name.isEmpty() | seeders.isEmpty() | hash.isEmpty()) {
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
