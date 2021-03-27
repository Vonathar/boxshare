package com.caputo.boxshare.service.engines;

import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HtmlResultsReaderTest {
  private final MockEngine engine = new MockEngine();
  Elements mockRows;

  @Value("classpath:mockHtmlResults.html")
  Resource resource;

  @BeforeAll
  public void init() throws IOException {
    InputStream is = resource.getInputStream();
    byte[] bytes = FileCopyUtils.copyToByteArray(is);
    String data = new String(bytes, StandardCharsets.UTF_8);
    mockRows = Jsoup.parse(data).select("tr");
  }

  @Test
  public void getSearchPage_IllegalUrl_ShouldReturnEmptyOptional() {
    assertTrue(engine.getSearchPage("https://;'#.com").isEmpty());
  }

  @Test
  public void parseTable_NoRows_ShouldReturnEmptyOptional() {
    assertTrue(engine.parseTable(new Elements(0), SearchMethod.QUICK).isEmpty());
  }

  @Test
  public void parseTable_QuickSearch_ShouldReturnSingleResult() {
    assertEquals(engine.parseTable(mockRows, SearchMethod.QUICK).get().size(), 1);
  }

  @Test
  public void parseTable_SmartSearch_ShouldNotReturnMoreResultsThanAllowed() {
    assertThat(engine.parseTable(mockRows, SearchMethod.SMART).get().size())
        .isLessThanOrEqualTo(MockEngine.MOCK_SMART_SEARCH_MAX_RESULTS);
  }

  @Test
  public void parseTable_SmartSearch_ShouldNotReturnResultsWithLessSeedersThanAllowed() {
    engine
        .parseTable(mockRows, SearchMethod.SMART)
        .get()
        .forEach(
            searchResult -> {
              assertThat(searchResult.getSeeders())
                  .isGreaterThanOrEqualTo(MockEngine.MOCK_SMART_SEARCH_MIN_SEEDS);
            });
  }

  @Test
  public void parseTable_CompleteSearch_ShouldReturnEveryAvailableResult() {
    assertEquals(engine.parseTable(mockRows, SearchMethod.COMPLETE).get().size(), mockRows.size());
  }

  static class MockEngine extends HtmlResultsReader {

    public static final int MOCK_SMART_SEARCH_MAX_RESULTS = 3;
    public static final int MOCK_SMART_SEARCH_MIN_SEEDS = 5;

    public MockEngine() {
      ReflectionTestUtils.setField(this, "SMART_SEARCH_MAX_RESULTS", MOCK_SMART_SEARCH_MAX_RESULTS);
      ReflectionTestUtils.setField(this, "SMART_SEARCH_MIN_SEEDS", MOCK_SMART_SEARCH_MIN_SEEDS);
    }

    @Override
    protected SearchResult parseRow(Element row) {
      return new SearchResult(
          row.select(".name").text(),
          row.select(".hash").text(),
          Integer.parseInt(row.select(".seeders").text()),
          row.select(".size").text(),
          "o");
    }

    @Override
    public Optional<List<SearchResult>> search(String query, SearchMethod method) {
      return Optional.empty();
    }
  }
}
