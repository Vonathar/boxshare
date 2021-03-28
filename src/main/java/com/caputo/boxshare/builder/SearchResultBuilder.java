package com.caputo.boxshare.builder;

import com.caputo.boxshare.entity.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * A class responsible for creating new instances of SearchResult objects. For more information
 * about the instanced class, see {@link SearchResult}.
 */
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
    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    logger.info(
        "Successfully created: {} - {}. Seeders: {} Size: {}", infoHash, name, seeders, size);
    return new SearchResult(name, infoHash, seeders, size, origin);
  }
}
