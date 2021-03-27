package com.caputo.boxshare.service;

import com.caputo.boxshare.enumerable.SearchMethod;
import org.springframework.stereotype.Service;

@Service
public class RequestValidator {

  /**
   * Validates a search query.
   *
   * @param query the query to validate.
   * @throws IllegalArgumentException when the input is not a valid query.
   */
  public void validateQuery(String query) {
    if (query.length() <= 1) {
      throw new IllegalArgumentException("Query must be at least 2 characters long.");
    }
    if (query.matches("^\\W+$")) {
      throw new IllegalArgumentException("Query cannot contain only non-word characters.");
    }
  }

  /**
   * Validates a search method.
   *
   * @param method the method to validate.
   * @throws IllegalArgumentException when the input is not a valid method.
   */
  public void validateMethod(String method) {
    try {
      SearchMethod.valueOf(method);
    } catch (Exception e) {
      throw new IllegalArgumentException("Illegal value for search method: " + method);
    }
  }
}
