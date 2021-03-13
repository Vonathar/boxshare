package com.caputo.boxshare.service.engines;

import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;

import java.util.List;

/**
 * This interface represents a generic search engine, responsible for fetching torrent results from
 * a given source. The implementation details are entirely irrelevant, but all results must be
 * parsed as an object of type SearchResult in order to be processed later on.
 */
public interface SearchEngine {

  /**
   * Queries the search engine and fetches the deserialised search results.
   *
   * @param query the term to search.
   * @param method the searching method to apply while parsing the search results.
   * @return the deserialised search results.
   */
  List<SearchResult> search(String query, SearchMethod method);
}
