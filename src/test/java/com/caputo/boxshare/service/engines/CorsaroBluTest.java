package com.caputo.boxshare.service.engines;

import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class CorsaroBluTest {

  @Autowired CorsaroBlu corsaroBlu;

  @Test
  public void search_ShouldSuccessfullyConnectAndReturnResults() {
    List<SearchResult> results = corsaroBlu.search("1080p", SearchMethod.QUICK);
    assertFalse(results.isEmpty());
  }

  @Test
  public void parseRow_NonParsableRow_ShouldReturnNull() {
    assertNull(corsaroBlu.parseRow(new Element("tr")));
  }
}
