package com.caputo.boxshare.service.engines;

import com.caputo.boxshare.entity.PirateBaySearchResults;
import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** A class responsible for querying the torrent search engine ThePirateBay. */
@Service
public class PirateBay extends JsonResultsReader implements SearchEngine {

  @Value("${search.engine.thePirateBay.baseUrl}")
  private String BASE_URL;

  /**
   * Queries ThePirateBay and fetches the serialised search results.
   *
   * @param query the term to search.
   * @param method the searching method to apply while parsing the results.
   * @return the serialised search results, or an empty Optional for non-serialisable JSON .
   */
  public Optional<List<SearchResult>> search(String query, SearchMethod method) {
    String url = BASE_URL + query;
    TypeReference<List<PirateBaySearchResults>> typeRef = new TypeReference<>() {};
    return getResults(query, url, method, typeRef);
  }
}
