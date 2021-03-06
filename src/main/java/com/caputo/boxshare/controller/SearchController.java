package com.caputo.boxshare.controller;

import com.caputo.boxshare.service.QueryValidator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

  final QueryValidator queryValidator;

  public SearchController(QueryValidator queryValidator) {
    this.queryValidator = queryValidator;
  }

  @GetMapping("search/{query}")
  public String getContent(@PathVariable String query) {
    queryValidator.validate(query);
    return "";
  }
}
