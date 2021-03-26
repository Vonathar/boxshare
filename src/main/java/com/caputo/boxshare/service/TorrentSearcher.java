package com.caputo.boxshare.service;

import com.caputo.boxshare.builder.ResultListBuilder;
import com.caputo.boxshare.entity.ResultList;
import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import com.caputo.boxshare.service.engines.CorsaroNero;
import com.caputo.boxshare.service.engines.LeetX;
import com.caputo.boxshare.service.engines.PirateBay;
import com.caputo.boxshare.service.engines.SearchEngine;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * This class is responsible for querying all available search engines. The result of searches are
 * aggregated and returned as a single collection.
 */
@Service
public class TorrentSearcher {
  final ResultListBuilder resultListBuilder;
  final List<SearchEngine> engines;

  public TorrentSearcher(
      ResultListBuilder resultListBuilder, PirateBay pb, LeetX leetX, CorsaroNero cn) {
    this.resultListBuilder = resultListBuilder;
    this.engines = Arrays.asList(pb, leetX, cn);
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
      List<SearchResult> results = engine.search(query, method);
      if (results != null) {
        resultListBuilder.add(results);
      }
    }
    return resultListBuilder.build();
  }
}
