package com.caputo.boxshare.service;

import com.caputo.boxshare.DirectoryReader;
import com.caputo.boxshare.builder.ResultListBuilder;
import com.caputo.boxshare.entity.ResultList;
import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import com.caputo.boxshare.service.engines.CorsaroBlu;
import com.caputo.boxshare.service.engines.CorsaroNero;
import com.caputo.boxshare.service.engines.LeetX;
import com.caputo.boxshare.service.engines.PirateBay;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class TorrentSearcherTest {
  @Autowired DirectoryReader directoryReader;
  @Autowired ResultListBuilder resultListBuilder;
  @Mock PirateBay pb;
  @Mock CorsaroNero cn;
  @Mock LeetX lx;
  @Mock CorsaroBlu cb;

  @Test
  void get_LegalQuery_ShouldReturnAggregatedSearchResultsFromAllEngines() {
    when(pb.search(any(), any()))
        .thenReturn(Collections.singletonList(new SearchResult("n", "h", 0, "0", "PirateBay")));
    when(cn.search(any(), any()))
        .thenReturn(Collections.singletonList(new SearchResult("n", "h", 0, "0", "CorsaroNero")));
    when(cb.search(any(), any()))
        .thenReturn(Collections.singletonList(new SearchResult("n", "h", 0, "0", "CorsaroBlu")));
    when(lx.search(any(), any()))
        .thenReturn(Collections.singletonList(new SearchResult("n", "h", 0, "0", "LeetX")));
    TorrentSearcher searcher = new TorrentSearcher(resultListBuilder, pb, lx, cn, cb);
    List<String> expectedEngines = directoryReader.getEngines();
    List<String> actualEngines = new ArrayList<>();
    ResultList results = searcher.get("1080p", SearchMethod.QUICK);
    results
        .getAll()
        .forEach(
            r -> {
              String origin = r.getOrigin();
              if (!actualEngines.contains(origin)) {
                actualEngines.add(origin);
              }
            });
    assertTrue(actualEngines.containsAll(expectedEngines));
  }
}
