package com.caputo.boxshare.service.engines;

import com.caputo.boxshare.builder.SearchResultBuilder;
import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class LeetX extends HtmlResultsReader implements SearchEngine {

  private final SearchResultBuilder srBuilder;

  @Value("${search.engine.leetX.baseUrl}")
  private String BASE_URL;

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
    String rowSelector = ".table-list tbody tr";
    String url = BASE_URL + query;
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
    String NAME_SELECTOR = ".coll-1";
    String SEEDERS_SELECTOR = ".coll-2";
    String SIZE_SELECTOR = ".coll-4";
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
          .setInfoHash(infoHash)
          .setOrigin(this.getClass().getSimpleName());
    } catch (IOException e) {
      logger.error("Failed to parse HTML row: \"{}\". Error: {}", row, e);
    }
    return srBuilder.build();
  }
}
