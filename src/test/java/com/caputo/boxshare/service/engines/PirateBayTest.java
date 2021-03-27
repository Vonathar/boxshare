package com.caputo.boxshare.service.engines;

import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class PirateBayTest {

  @Autowired PirateBay pb;

  @Test
  public void search_ShouldSuccessfullyConnectAndReturnResults() {
    Optional<List<SearchResult>> results = pb.search("1080p", SearchMethod.QUICK);
    assertFalse(results.isEmpty());
  }
}
