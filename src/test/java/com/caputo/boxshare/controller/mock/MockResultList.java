package com.caputo.boxshare.controller.mock;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

/**
 * This class represents part of the entity that the client uses to deserialise responses received
 * from the 'search' endpoint. It is intended to be exclusively used in tests.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"all"})
public class MockResultList {

  @JsonProperty("all")
  private final List<MockSearchResult> all = null;

  @JsonProperty("all")
  public List<MockSearchResult> getAll() {
    return all;
  }
}
