package com.caputo.boxshare.controller;

import com.caputo.boxshare.entity.ResultList;
import com.caputo.boxshare.enumerable.SearchMethod;
import com.caputo.boxshare.service.QueryValidator;
import com.caputo.boxshare.service.TorrentSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller is responsible for all requests that concern the torrent searches. To change the
 * list of enabled search engines, refer to the TorrentSearcher class.
 */
@RestController
public class SearchController {

  final QueryValidator queryValidator;
  final TorrentSearcher torrentSearcher;
  final Logger logger = LoggerFactory.getLogger(SearchController.class);

  public SearchController(QueryValidator queryValidator, TorrentSearcher torrentSearcher) {
    this.queryValidator = queryValidator;
    this.torrentSearcher = torrentSearcher;
  }

  /**
   * Searches for a given term on all available torrent engines.
   *
   * @param query the search term.
   * @return the list of torrent results.
   */
  @GetMapping("search/{query}")
  public ResultList search(
      @PathVariable String query, @RequestParam(defaultValue = "quick") String method) {
    logger.info("Incoming request: \"{}\" ({}).", query, method);
    queryValidator.validate(query);
    return torrentSearcher.get(query, SearchMethod.valueOf(method.toUpperCase()));
  }
}
