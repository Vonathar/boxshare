package com.caputo.boxshare.service.engines;

import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class CorsaroNeroTest {

  @Autowired CorsaroNero corsaroNero;

  @Test
  public void search_ShouldSuccessfullyConnectAndReturnResults() {
    Optional<List<SearchResult>> results = corsaroNero.search("1080p", SearchMethod.QUICK);
    assertFalse(results.isEmpty());
  }

  @Test
  public void parseRow_NonParsableRow_ShouldReturnNull() {
    assertNull(corsaroNero.parseRow(new Element("tr")));
  }
}
