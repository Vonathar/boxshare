package com.caputo.boxshare.entity;

public class SearchResult {
  private final String name;
  private final String infoHash;
  private final int seeders;
  private final String size;
  private final String user;
  private final String origin;

  public SearchResult(
      String name, String infoHash, int seeders, String size, String user, String origin) {
    this.name = name;
    this.infoHash = infoHash;
    this.seeders = seeders;
    this.size = size;
    this.user = user;
    this.origin = origin;
  }

  public String getName() {
    return name;
  }

  public String getInfoHash() {
    return infoHash;
  }

  public int getSeeders() {
    return seeders;
  }

  public String getSize() {
    return size;
  }

  public String getUser() {
    return user;
  }

  public String getOrigin() {
    return origin;
  }
}
