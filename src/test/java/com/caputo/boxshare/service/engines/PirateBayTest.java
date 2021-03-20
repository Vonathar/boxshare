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
class PirateBayTest {

  @Autowired PirateBay pb;

  @Value("${search.smart.minSeeders}")
  private int SMART_SEARCH_MIN_SEEDS;

  @Value("${search.smart.maxResults}")
  private int SMART_SEARCH_MAX_RESULTS;

  private List<SearchResult> quickResults;
  private List<SearchResult> smartResults;
  private List<SearchResult> completeResults;

  @BeforeAll
  public void getResults() {
    quickResults = pb.search("linux", SearchMethod.QUICK);
    smartResults = pb.search("linux", SearchMethod.SMART);
    completeResults = pb.search("linux", SearchMethod.COMPLETE);
  }

  @Test
  public void search_QuickSearch_ShouldReturnListWithSingleSearchResult() {
    assertThat(quickResults).hasSize(1);
  }

  @Test
  public void search_SmartSearch_ShouldNotReturnMoreResultsThanMaxAllowed() {
    assertThat(smartResults).hasSizeLessThanOrEqualTo(SMART_SEARCH_MAX_RESULTS);
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

  @Test
  public void search_QuickSearch_NoResults_ShouldReturnEmptyList() {
    List<SearchResult> quick = pb.search("Keywords without results.", SearchMethod.QUICK);
    assertThat(quick.size()).isEqualTo(0);
  }

  @Test
  public void search_SmartSearch_NoResults_ShouldReturnEmptyList() {
    List<SearchResult> smart = pb.search("Keywords without results.", SearchMethod.SMART);
    assertThat(smart.size()).isEqualTo(0);
  }

  @Test
  public void search_CompleteSearch_NoResults_ShouldReturnEmptyList() {
    List<SearchResult> complete = pb.search("Keywords without results.", SearchMethod.COMPLETE);
    assertThat(complete.size()).isEqualTo(0);
  }
}
