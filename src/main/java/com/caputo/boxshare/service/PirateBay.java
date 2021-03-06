package com.caputo.boxshare.service;

import com.caputo.boxshare.entity.PirateBaySearchResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PirateBay {

  /**
   * Queries TPB and fetches the deserialised search results for a given query.
   *
   * @param query the term to search on TPB.
   * @return the deserialised search results.
   */
  public List<PirateBaySearchResult> search(String query) {
    String URL = "https://pirateproxy.ltda/newapi/q.php?q=" + query;
    String json = null;
    try {
      HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
      HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(URL));
      json = request.execute().parseAsString();
    } catch (Exception e) {
      System.out.printf("Failed to query TBP for: \"%s\". Error: %s", query, e);
    }
    ObjectMapper mapper = new ObjectMapper();
    List<PirateBaySearchResult> searchResult = null;
    try {
      searchResult =
          mapper
              .reader()
              .forType(new TypeReference<List<PirateBaySearchResult>>() {})
              .readValue(json);
    } catch (Exception e) {
      System.out.printf("Failed to parse JSON for query: \"%s\". Error: %s", query, e);
    }
    return searchResult;
  }

  /**
   * Fetches the magnet link for a given search result.
   *
   * @param searchResult the search result from which the magnet link is built.
   * @return the magnet link of the search result.
   */
  public String getMagnet(PirateBaySearchResult searchResult) {
    return String.format("magnet:?xt=urn:btih:%s", searchResult.getInfoHash());
  }
}
