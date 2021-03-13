package com.caputo.boxshare.service;

import com.caputo.boxshare.DirectoryReader;
import com.caputo.boxshare.entity.ResultList;
import com.caputo.boxshare.enumerable.SearchMethod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class TorrentSearcherTest {

  @Autowired TorrentSearcher searcher;
  @Autowired DirectoryReader directoryReader;

  @Test
  void get_LegalQuery_ShouldReturnAggregatedSearchResultsFromAllEngines() {
    List<String> expectedEngines = directoryReader.getEngines();
    List<String> actualEngines = new ArrayList<>();
    ResultList results = searcher.get("sample test", SearchMethod.QUICK);
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
