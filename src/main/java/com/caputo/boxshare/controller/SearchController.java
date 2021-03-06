package com.caputo.boxshare.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

  @GetMapping("search/{query}")
  public String getContent(@PathVariable String query) {
    return "";
  }
}
