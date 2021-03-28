package com.caputo.boxshare.service.engines;

import com.caputo.boxshare.builder.SearchResultBuilder;
import com.caputo.boxshare.entity.JsonSearchResult;
import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * A class to be extended by search engine classes that retrieve search results as JSON. This class
 * provides multiple methods for common serialisation actions shared by all engines, and only
 * requires its children to specify a type reference of the entity to use when serialising results.
 * For an example of a class that extends this abstract class, see {@link PirateBay}.
 */
public abstract class JsonResultsReader implements SearchEngine {

  @Autowired SearchResultBuilder srBuilder;
  String query;
  String className;
  SearchMethod method;

  @Value("${search.smart.minSeeders}")
  private int SMART_SEARCH_MIN_SEEDS;

  @Value("${search.smart.maxResults}")
  private int SMART_SEARCH_MAX_RESULTS;

  /**
   * Queries the engine and fetches the deserialised search results.
   *
   * @param query the term to search.
   * @param url the URL to query for the JSON.
   * @param method the searching method to apply while parsing the results.
   * @param typeRef the type reference to use when deserialising the results.
   * @param <T> the entity class to use when deserialising the results.
   * @return the deserialised search results.
   */
  protected <T> Optional<List<SearchResult>> getResults(
      String query, String url, SearchMethod method, TypeReference<T> typeRef) {
    this.query = query;
    this.method = method;
    this.className = this.getClass().getSimpleName();
    logger.info("Searching for \"{}\" on {} ({})..", query, className, method);
    String json = getJson(url);
    return deserialiseJson(json, typeRef);
  }

  /**
   * Queries a given URL and returns the JSON response.
   *
   * @param url the URL to query.
   * @return the JSON response.
   */
  protected String getJson(String url) {
    String json = null;
    try {
      HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
      HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(url));
      json = request.execute().parseAsString();
    } catch (Exception e) {
      logger.error("Failed to get JSON. Error=\"{}\"", e.getMessage());
    }
    return json;
  }

  /**
   * Parses an instance of JsonSearchResultBuilds as a SearchResult.
   *
   * @param sr the instance to transform into SearchResult
   * @return the parsed result.
   */
  protected SearchResult parseResult(JsonSearchResult sr) {
    return srBuilder
        .setName(sr.getName())
        .setSeeders(sr.getSeeders())
        .setInfoHash(sr.getInfoHash())
        .setSize(sr.getSize())
        .setOrigin(this.getClass().getSimpleName())
        .build();
  }

  /**
   * Builds a list of SearchResult by deserialising the input json.
   *
   * @param json the input to deserialise.
   * @param typeRef the type reference to use when deserialising.
   * @param <T> the entity to use for deserialisation.
   * @return a list of populated SearchResult instances.
   */
  protected <T> Optional<List<SearchResult>> deserialiseJson(
      String json, TypeReference<T> typeRef) {
    List<SearchResult> results = new ArrayList<>();
    List<JsonSearchResult> jsonResults;
    try {
      jsonResults = new ObjectMapper().reader().forType(typeRef).readValue(json);
    } catch (JsonProcessingException e) {
      logger.error("Failed to deserialise JSON. Json=\"{}\". Error=\"{}\"", json, e.getMessage());
      return Optional.empty();
    }
    if (jsonResults != null && !jsonResults.get(0).getName().equals("No results returned")) {
      switch (method) {
        case QUICK:
          results.add(parseResult(jsonResults.get(0)));
          break;
        case COMPLETE:
          jsonResults.forEach(r -> results.add(parseResult(r)));
          break;
        case SMART:
          List<JsonSearchResult> filteredResults =
              jsonResults.stream()
                  .filter(r -> r.getSeeders() > SMART_SEARCH_MIN_SEEDS)
                  .collect(Collectors.toList());
          for (int i = 0; i < filteredResults.size() && i < SMART_SEARCH_MAX_RESULTS; i++) {
            results.add(parseResult(filteredResults.get(i)));
          }
      }
    }
    logger.info("{} results found.", results.size());
    return Optional.of(results);
  }
}
