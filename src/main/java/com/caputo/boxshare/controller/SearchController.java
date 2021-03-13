package com.caputo.boxshare.controller;

import com.caputo.boxshare.service.QueryValidator;
import com.caputo.boxshare.service.engines.PirateBay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

  final QueryValidator queryValidator;
  Logger logger = LoggerFactory.getLogger(PirateBay.class);

  public SearchController(QueryValidator queryValidator) {
    this.queryValidator = queryValidator;
  }

  @GetMapping("search/{query}")
  public String getContent(@PathVariable String query) {
    logger.info("Incoming request: \"{}\".", query);
    queryValidator.validate(query);
    return "";
  }
}
