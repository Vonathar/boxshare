package com.caputo.boxshare.builder;

import com.caputo.boxshare.entity.ResultList;
import com.caputo.boxshare.entity.SearchResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ResultListBuilderTest {

  @Autowired ResultListBuilder rlBuilder;
  @Autowired SearchResultBuilder srBuilder;

  @Test
  void build_ShouldBuildListSortedByNumberOfSeedersInAscendingOrder() {
    List<SearchResult> unsortedList =
        Arrays.asList(
            srBuilder.setSeeders(9).build(),
            srBuilder.setSeeders(1).build(),
            srBuilder.setSeeders(7).build(),
            srBuilder.setSeeders(3).build(),
            srBuilder.setSeeders(5).build());
    ResultList sortedList = rlBuilder.add(unsortedList).build();
    int previous = 0;
    for (SearchResult sr : sortedList.getAll()) {
      assertThat(sr.getSeeders()).isGreaterThan(previous);
      previous = sr.getSeeders();
    }
  }
}
