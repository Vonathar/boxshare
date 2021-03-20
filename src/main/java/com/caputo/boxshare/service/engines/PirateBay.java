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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PirateBay implements SearchEngine {

  final SearchResultBuilder srBuilder;
  Logger logger = LoggerFactory.getLogger(PirateBay.class);

  @Value("${search.smart.minSeeders}")
  private int SMART_SEARCH_MIN_SEEDS;

  @Value("${search.smart.maxResults}")
  private int SMART_SEARCH_MAX_RESULTS;

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
    List<SearchResult> results = new ArrayList<>();
    logger.info("Searching for \"{}\" on TPB ({})..", query, method);
    String URL = "https://pirateproxy.ltda/newapi/q.php?q=" + query;
    String json = null;
    try {
      HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
      HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(URL));
      json = request.execute().parseAsString();
    } catch (Exception e) {
      logger.error("Failed to query TBP for: \"{}\". Error: {}", query, e);
    }
    try {
      List<PirateBaySearchResult> pbResults =
          new ObjectMapper()
              .reader()
              .forType(new TypeReference<List<PirateBaySearchResult>>() {})
              .readValue(json);
      if (!pbResults.get(0).getName().equals("No results returned")) {
        switch (method) {
          case QUICK:
            results.add(parseResult(pbResults.get(0)));
            break;
          case COMPLETE:
            pbResults.forEach(r -> results.add(parseResult(r)));
            break;
          case SMART:
            List<PirateBaySearchResult> filteredResults =
                pbResults.stream()
                    .filter(r -> r.getSeeders() > SMART_SEARCH_MIN_SEEDS)
                    .collect(Collectors.toList());
            for (int i = 0; i < filteredResults.size() && i < SMART_SEARCH_MAX_RESULTS; i++) {
              results.add(parseResult(filteredResults.get(i)));
            }
        }
      }
    } catch (Exception e) {
      logger.error("Failed to parse JSON for query: \"{}\". Error: {}", query, e);
    }
    logger.info("Successfully found {} search results.", results.size());
    return results;
  }

  /**
   * Builds a SearchResult object from an instance of PirateBaySearchResult.
   *
   * @param sr the instance to transform into SearchResult
   * @return the object parsed from PirateBaySearchResult.
   */
  private SearchResult parseResult(PirateBaySearchResult sr) {
    logger.info(
        "Successfully parsed: {} - {}. Seeders: {} Size: {}",
        sr.getInfoHash(),
        sr.getName(),
        sr.getSeeders(),
        sr.getSize());
    return srBuilder
        .setName(sr.getName())
        .setSeeders(sr.getSeeders())
        .setInfoHash(sr.getInfoHash())
        .setUser(sr.getUsername())
        .setSize(sr.getSize())
        .setOrigin(this.getClass().getSimpleName())
        .build();
  }
}
