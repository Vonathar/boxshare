package com.caputo.boxshare.builder;

import com.caputo.boxshare.entity.ResultList;
import com.caputo.boxshare.entity.SearchResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResultListBuilder {
  private final List<SearchResult> results = new ArrayList<>();

  public ResultListBuilder add(List<SearchResult> sr) {
    results.addAll(sr);
    return this;
  }

  public ResultList build() {
    return new ResultList(results);
  }
}
