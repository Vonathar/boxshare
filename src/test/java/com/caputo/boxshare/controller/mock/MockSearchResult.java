package com.caputo.boxshare.controller.mock;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * This class represents part of the entity that the client uses to deserialise responses received
 * from the 'search' endpoint. It is intended to be exclusively used in tests.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"name", "infoHash", "seeders", "size", "user", "origin"})
public class MockSearchResult {

  @JsonProperty("name")
  private String name;

  @JsonProperty("infoHash")
  private String infoHash;

  @JsonProperty("seeders")
  private String seeders;

  @JsonProperty("size")
  private String size;

  @JsonProperty("user")
  private String user;

  @JsonProperty("origin")
  private String origin;

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("infoHash")
  public String getInfoHash() {
    return infoHash;
  }

  @JsonProperty("seeders")
  public String getSeeders() {
    return seeders;
  }

  @JsonProperty("size")
  public String getSize() {
    return size;
  }

  @JsonProperty("user")
  public String getUser() {
    return user;
  }

  @JsonProperty("origin")
  public String getOrigin() {
    return origin;
  }
}
