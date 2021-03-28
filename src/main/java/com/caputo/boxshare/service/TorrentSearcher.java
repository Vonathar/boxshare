package com.caputo.boxshare.service;

import com.caputo.boxshare.builder.ResultListBuilder;
import com.caputo.boxshare.entity.ResultList;
import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import com.caputo.boxshare.service.engines.CorsaroBlu;
import com.caputo.boxshare.service.engines.CorsaroNero;
import com.caputo.boxshare.service.engines.LeetX;
import com.caputo.boxshare.service.engines.PirateBay;
import com.caputo.boxshare.service.engines.SearchEngine;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * A class responsible for querying multiple search engines and aggregating their results. Any new
 * search engine must be added to the 'engines' list inside of the constructor.
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
   * Queries all available search engines and returns their search results.
   *
   * @param query the term to search.
   * @param method the searching method to apply while parsing the results.
   * @return the list of serialised search results.
   */
  public ResultList get(String query, SearchMethod method) {
    for (SearchEngine engine : engines) {
      Optional<List<SearchResult>> results = engine.search(query, method);
      results.ifPresent(resultListBuilder::add);
    }
    return resultListBuilder.build();
  }
}
