package com.caputo.boxshare.service.engines;

import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class LeetXTest {

  @Autowired LeetX leetX;
  private List<SearchResult> quickResults;
  private List<SearchResult> smartResults;
  private List<SearchResult> completeResults;

  @Value("${search.smart.minSeeders}")
  private int SMART_SEARCH_MIN_SEEDS;

  @Value("${search.smart.maxResults}")
  private int SMART_SEARCH_MAX_RESULTS;

  @BeforeAll
  public void getResults() {
    quickResults = leetX.search("sample test", SearchMethod.QUICK);
    smartResults = leetX.search("sample test", SearchMethod.SMART);
    completeResults = leetX.search("sample test", SearchMethod.COMPLETE);
  }

  @Test
  public void search_QuickSearch_ShouldReturnListWithSingleSearchResult() {
    assertThat(quickResults).hasSize(1);
  }

  @Test
  public void search_SmartSearch_ShouldNotReturnMoreResultsThanMaxAllowed() {
    assertThat(smartResults).hasSizeLessThan(SMART_SEARCH_MAX_RESULTS);
  }

  @Test
  public void search_SmartSearch_ShouldNotReturnResultsWithLessSeedersThanMinAllowed() {
    for (SearchResult sr : smartResults) {
      assertThat(sr.getSeeders()).isGreaterThanOrEqualTo(SMART_SEARCH_MIN_SEEDS);
    }
  }

  @Test
  public void search_CompleteSearch_ShouldReturnListWithAtLeastOneSearchResult() {
    assertThat(completeResults.size()).isGreaterThanOrEqualTo(1);
  }
}
