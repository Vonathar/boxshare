package com.caputo.boxshare.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
  "name",
  "info_hash",
  "seeders",
  "size",
  "username",
  "status",
})
public class PirateBaySearchResults implements JsonSearchResult {

  @JsonProperty("name")
  private String name;

  @JsonProperty("info_hash")
  private String infoHash;

  @JsonProperty("seeders")
  private int seeders;

  @JsonProperty("size")
  private String size;

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("info_hash")
  public String getInfoHash() {
    return infoHash;
  }

  @JsonProperty("seeders")
  public int getSeeders() {
    return seeders;
  }

  @JsonProperty("size")
  public String getSize() {
    return size;
  }
}
