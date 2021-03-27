package com.caputo.boxshare.service;

import com.caputo.boxshare.builder.ResultListBuilder;
import com.caputo.boxshare.entity.ResultList;
import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import com.caputo.boxshare.service.engines.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * This class is responsible for querying all available search engines. The result of searches are
 * aggregated and returned as a single collection.
 */
@Service
public class TorrentSearcher {
  final ResultListBuilder resultListBuilder;
  final List<SearchEngine> engines;

  public TorrentSearcher(
      ResultListBuilder resultListBuilder,
      PirateBay pb,
      LeetX leetX,
      CorsaroNero cn,
      CorsaroBlu cb) {
    this.resultListBuilder = resultListBuilder;
    this.engines = Arrays.asList(pb, leetX, cn, cb);
  }

  /**
   * Queries all search engines and fetches their search results.
   *
   * @param query the term to search.
   * @param method the searching method to apply while parsing the search results.
   * @return the list of deserialised search results.
   */
  public ResultList get(String query, SearchMethod method) {
    for (SearchEngine engine : engines) {
      Optional<List<SearchResult>> results = engine.search(query, method);
      results.ifPresent(resultListBuilder::add);
    }
    return resultListBuilder.build();
  }
}
