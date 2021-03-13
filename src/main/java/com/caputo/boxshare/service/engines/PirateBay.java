package com.caputo.boxshare.service.engines;

import com.caputo.boxshare.builder.SearchResultBuilder;
import com.caputo.boxshare.entity.PirateBaySearchResult;
import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PirateBay implements SearchEngine {
  final SearchResultBuilder srBuilder;
  Logger logger = LoggerFactory.getLogger(PirateBay.class);

  public PirateBay(SearchResultBuilder srBuilder) {
    this.srBuilder = srBuilder;
  }

  /**
   * Queries TPB and fetches the deserialised search results.
   *
   * @param query the term to search on TPB.
   * @return the deserialised search results.
   */
  public List<SearchResult> search(String query, SearchMethod method) {
    logger.info("Searching for \"{}\" on TPB..", query);
    String URL = "https://pirateproxy.ltda/newapi/q.php?q=" + query;
    String json = null;
    try {
      HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
      HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(URL));
      json = request.execute().parseAsString();
    } catch (Exception e) {
      logger.error("Failed to query TBP for: \"{}\". Error: {}", query, e);
    }
    List<SearchResult> results = new ArrayList<>();
    try {
      List<PirateBaySearchResult> pbResults =
          new ObjectMapper()
              .reader()
              .forType(new TypeReference<List<PirateBaySearchResult>>() {})
              .readValue(json);
      pbResults.forEach(
          pbResult ->
              results.add(
                  srBuilder
                      .setName(pbResult.getName())
                      .setSeeders(pbResult.getSeeders())
                      .setInfoHash(pbResult.getInfoHash())
                      .setUser(pbResult.getUsername())
                      .setSize(pbResult.getSize())
                      .setOrigin(this.getClass().getSimpleName())
                      .build()));
    } catch (Exception e) {
      logger.error("Failed to parse JSON for query: \"{}\". Error: {}", query, e);
    }
    logger.info("Successfully found {} search results.", results.size());
    return results;
  }
}
