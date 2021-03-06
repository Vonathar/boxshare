package com.caputo.boxshare.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QueryValidator {

  /**
   * Validates a search query.
   *
   * @param query the query to validate.
   * @return whether the query is valid.
   */
  public boolean validate(String query) {
    if (query.length() <= 1) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Query must be at least 2 characters long.");
    }
    if (query.matches("^\\W+$")) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Query cannot contain only non-word characters.");
    }
    return true;
  }
}
