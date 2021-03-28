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
class LeetXTest {

  @Autowired LeetX leetX;

  @Test
  public void search_ShouldSuccessfullyConnectAndReturnResults() {
    Optional<List<SearchResult>> results = leetX.search("1080p", SearchMethod.QUICK);
    assertFalse(results.isEmpty());
  }

  @Test
  public void parseRow_NonParsableRow_ShouldReturnNull() {
    assertNull(leetX.parseResult(new Element("tr")));
  }
}
