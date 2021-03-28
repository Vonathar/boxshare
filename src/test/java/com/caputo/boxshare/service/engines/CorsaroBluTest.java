package com.caputo.boxshare.service.engines;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import java.util.List;
import java.util.Optional;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CorsaroBluTest {

  @Autowired CorsaroBlu corsaroBlu;

  @Test
  public void search_ShouldSuccessfullyConnectAndReturnResults() {
    Optional<List<SearchResult>> results = corsaroBlu.search("1080p", SearchMethod.QUICK);
    assertFalse(results.isEmpty());
  }

  @Test
  public void parseRow_NonParsableRow_ShouldReturnNull() {
    assertNull(corsaroBlu.parseResult(new Element("tr")));
  }
}
