package com.caputo.boxshare.entity;

import java.util.List;

/**
 * A class representing the unified collection of SearchResult objects that are returned by each
 * search engine. Their instantiation is directly handled by a builder class.
 */
public class ResultList {
  private final List<SearchResult> results;

  public ResultList(List<SearchResult> results) {
    this.results = results;
  }

  public List<SearchResult> getAll() {
    return results;
  }
}
