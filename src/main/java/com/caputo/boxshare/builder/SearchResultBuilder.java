package com.caputo.boxshare.builder;

import com.caputo.boxshare.entity.SearchResult;
import org.springframework.stereotype.Service;

@Service
public class SearchResultBuilder {

  private String name;
  private String infoHash;
  private int seeders;
  private String size;
  private String origin;

  public SearchResultBuilder setName(String name) {
    this.name = name;
    return this;
  }

  public SearchResultBuilder setInfoHash(String infoHash) {
    this.infoHash = infoHash;
    return this;
  }

  public SearchResultBuilder setSeeders(int seeders) {
    this.seeders = seeders;
    return this;
  }

  public SearchResultBuilder setSize(String size) {
    this.size = size;
    return this;
  }

  public SearchResultBuilder setOrigin(String origin) {
    this.origin = origin;
    return this;
  }

  public SearchResult build() {
    return new SearchResult(name, infoHash, seeders, size, user, origin);
  }
}
