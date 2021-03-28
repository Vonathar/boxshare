package com.caputo.boxshare.service.engines;

import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An interface representing a generic search engine, responsible for fetching torrent search
 * results from a given source.
 */
public interface SearchEngine {
  Logger logger = LoggerFactory.getLogger(Runnable.class.getSimpleName());
  String className = Runnable.class.getSimpleName();
  Map<String, String> headers =
      new HashMap<>(
          Map.of(
              "User-Agent",
              "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                  + "(KHTML, like Gecko) Chrome/88.0.4324.186 Safari/537.36"));

  /**
   * Queries the search engine for the serialised search results.
   *
   * @param query the term to search.
   * @param method the searching method to apply while parsing the results.
   * @return the serialised search results, or an empty Optional for non-serialisable JSON .
   */
  Optional<List<SearchResult>> search(String query, SearchMethod method);
}
