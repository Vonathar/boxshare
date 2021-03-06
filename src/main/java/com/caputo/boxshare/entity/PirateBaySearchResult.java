package com.caputo.boxshare.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
  "id",
  "name",
  "seeders",
  "size",
  "username",
  "status",
})
public class PirateBaySearchResult {
  @JsonProperty("id")
  private String id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("seeders")
  private String seeders;

  @JsonProperty("size")
  private String size;

  @JsonProperty("username")
  private String username;

  @JsonProperty("status")
  private String status;

  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("seeders")
  public String getSeeders() {
    return seeders;
  }

  @JsonProperty("size")
  public String getSize() {
    return size;
  }

  @JsonProperty("username")
  public String getUsername() {
    return username;
  }

  @JsonProperty("status")
  public String getStatus() {
    return status;
  }
}
