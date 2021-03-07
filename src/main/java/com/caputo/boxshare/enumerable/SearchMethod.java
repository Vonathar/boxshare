package com.caputo.boxshare.enumerable;

/**
 * This enumerable represents the available methods for searching torrents. These methods apply to a
 * search on a single search engine, not the global results.
 */
public enum SearchMethod {
  /** Retrieves the search result with the most seeders. */
  QUICK,
  /** Retrieves a limited number of search results that have a minimum number of seeders. */
  SMART,
  /** Retrieves all available search results. */
  COMPLETE
}
