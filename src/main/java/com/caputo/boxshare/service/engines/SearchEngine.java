package com.caputo.boxshare.service.engines;

import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This interface represents a generic search engine, responsible for fetching torrent results from
 * a given source. The implementation details are entirely irrelevant, but all results must be
 * parsed as an object of type SearchResult in order to be processed later on.
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
   * Queries the search engine and fetches the deserialised search results.
   *
   * @param query the term to search.
   * @param method the searching method to apply while parsing the search results.
   * @return the deserialised search results.
   */
  Optional<List<SearchResult>> search(String query, SearchMethod method);
}
