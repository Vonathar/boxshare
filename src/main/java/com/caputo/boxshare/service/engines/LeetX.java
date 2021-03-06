package com.caputo.boxshare.service.engines;

import com.caputo.boxshare.builder.SearchResultBuilder;
import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** A class responsible for querying the torrent search engine 1337X. */
@Service
public class LeetX extends HtmlResultsReader implements SearchEngine {

  private final SearchResultBuilder srBuilder;

  @Value("${search.engine.leetX.baseUrl}")
  private String BASE_URL;

  public LeetX(SearchResultBuilder srBuilder) {
    this.srBuilder = srBuilder;
  }

  /**
   * Queries 1337X and returns the serialised search results.
   *
   * @param query the term to search on 1337X.
   * @param method the searching method to apply while parsing the results.
   * @return the serialised search results.
   */
  public Optional<List<SearchResult>> search(String query, SearchMethod method) {
    logger.info("Searching for \"{}\" on {} ({})..", query, className, method);
    String rowSelector = ".table-list tbody tr";
    String url = BASE_URL + "/srch?search=" + query;
    return getResults(url, rowSelector, method);
  }

  /**
   * Parses a SearchResult object from an HTML table row.
   *
   * @param htmlResult an HTML row of the search page.
   * @return the populated SearchResult object, or null for non-parsable inputs.
   */
  protected SearchResult parseResult(Element htmlResult) {
    logger.info("Parsing row..");
    String NAME_SELECTOR = ".coll-1";
    String SEEDERS_SELECTOR = ".coll-2";
    String SIZE_SELECTOR = ".coll-4";
    String ANCHOR_SELECTOR = ".coll-1 a:nth-child(2)";
    String name = htmlResult.select(NAME_SELECTOR).text();
    String seeders = htmlResult.select(SEEDERS_SELECTOR).text();
    String size = htmlResult.select(SIZE_SELECTOR).text();
    String resultPageUrl = BASE_URL + htmlResult.select(ANCHOR_SELECTOR).attr("href");
    if (name.isEmpty() | seeders.isEmpty() | resultPageUrl.isEmpty()) {
      logger.error("Failed to parse required information from row: {}", htmlResult);
      return null;
    } else {
      srBuilder
          .setOrigin(this.getClass().getSimpleName())
          .setName(name)
          .setSeeders(Integer.parseInt(seeders))
          .setSize(size);
    }
    try {
      Document resultPageHtml = Jsoup.connect(resultPageUrl).get();
      String HASH_SELECTOR = ".infohash-box span";
      String infoHash = resultPageHtml.select(HASH_SELECTOR).text();
      if (infoHash.isEmpty()) {
        logger.error("Failed to parse hash from URL: {}", resultPageUrl);
        return null;
      } else {
        srBuilder.setInfoHash(infoHash);
      }
    } catch (IOException e) {
      logger.error("Failed to get HTML. URL=\"{}\", Error=\"{}\"", resultPageUrl, e.getMessage());
    }
    return srBuilder.build();
  }
}
