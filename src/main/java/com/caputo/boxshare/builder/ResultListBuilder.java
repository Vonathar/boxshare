package com.caputo.boxshare.builder;

import com.caputo.boxshare.entity.ResultList;
import com.caputo.boxshare.entity.SearchResult;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * A class responsible for creating new instances of ResultList objects. Before returning the built
 * object, the list of results is sorted by the number of seeders (in ascending order). For more
 * information about the instanced class, see {@link ResultList}.
 */
@Service
public class ResultListBuilder {
  private List<SearchResult> results = new ArrayList<>();

  public ResultListBuilder add(List<SearchResult> sr) {
    results.addAll(sr);
    return this;
  }

  /**
   * Builds and sorts a new ResultList.
   *
   * @return the ResultList sorted by number of seeders.
   */
  public ResultList build() {
    ResultList list = new ResultList(results);
    results = new ArrayList<>();
    return sortBySeeders(list);
  }

  /**
   * Sorts the results in the ResultList by number of seeders.
   *
   * @param list the unsorted ResultList.
   * @return the sorted ResultList.
   */
  private ResultList sortBySeeders(ResultList list) {
    list.getAll().sort(Comparator.comparing(SearchResult::getSeeders));
    return list;
  }
}
