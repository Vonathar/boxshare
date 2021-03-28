package com.caputo.boxshare.enumerable;

/** An enumerable representing the available methods for searching torrents. */
public enum SearchMethod {
  /** Retrieves the search result with the most seeders. */
  QUICK,
  /** Retrieves a limited number of search results that have a minimum number of seeders. */
  SMART,
  /** Retrieves all available search results. */
  COMPLETE
}
