package com.caputo.boxshare.service.engines;

import com.caputo.boxshare.builder.SearchResultBuilder;
import com.caputo.boxshare.entity.JsonSearchResult;
import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.enumerable.SearchMethod;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
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
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JsonResultsReaderTest {
  private final MockEngine engine = new MockEngine();
  TypeReference<List<MockResultEntity>> mockTypeRef = new TypeReference<>() {};
  String mockJson;

  @Value("classpath:mockJsonResults.json")
  Resource resource;

  @BeforeAll
  public void init() throws IOException {
    InputStream is = resource.getInputStream();
    byte[] bytes = FileCopyUtils.copyToByteArray(is);
    mockJson = new String(bytes, StandardCharsets.UTF_8);
  }

  @Test
  public void getJson_OutboundRequestFails_ShouldReturnNullString() {
    assertNull(engine.getJson("http://?:,.com"));
  }

  @Test
  public void deserialiseJson_IllegalInputJson_ShouldReturnEmptyOptional() {
    assertTrue(engine.deserialiseJson("[{non parsable json}]", mockTypeRef).isEmpty());
  }

  @Test
  public void deserialiseJson_QuickSearch_ShouldReturnSingleResult() {
    ReflectionTestUtils.setField(engine, "method", SearchMethod.QUICK);
    assertEquals(engine.deserialiseJson(mockJson, mockTypeRef).get().size(), 1);
  }

  @Test
  public void deserialiseJson_SmartSearch_ShouldNotReturnMoreResultsThanAllowed() {
    ReflectionTestUtils.setField(engine, "method", SearchMethod.SMART);
    assertThat(engine.deserialiseJson(mockJson, mockTypeRef).get().size())
        .isLessThanOrEqualTo(MockEngine.MOCK_SMART_SEARCH_MAX_RESULTS);
  }

  @Test
  public void deserialiseJson_SmartSearch_ShouldNotReturnResultsWithLessSeedersThanAllowed() {
    ReflectionTestUtils.setField(engine, "method", SearchMethod.SMART);
    List<SearchResult> results = engine.deserialiseJson(mockJson, mockTypeRef).get();
    results.forEach(
        searchResult ->
            assertThat(searchResult.getSeeders())
                .isGreaterThanOrEqualTo(MockEngine.MOCK_SMART_SEARCH_MIN_SEEDS));
  }

  @Test
  public void deserialiseJson_CompleteSearch_ShouldReturnEveryAvailableResult() {
    ReflectionTestUtils.setField(engine, "method", SearchMethod.COMPLETE);
    assertEquals(engine.deserialiseJson(mockJson, mockTypeRef).get().size(), 11);
  }

  static class MockEngine extends JsonResultsReader {

    public static final int MOCK_SMART_SEARCH_MAX_RESULTS = 3;
    public static final int MOCK_SMART_SEARCH_MIN_SEEDS = 5;

    public MockEngine() {
      ReflectionTestUtils.setField(this, "SMART_SEARCH_MAX_RESULTS", MOCK_SMART_SEARCH_MAX_RESULTS);
      ReflectionTestUtils.setField(this, "SMART_SEARCH_MIN_SEEDS", MOCK_SMART_SEARCH_MIN_SEEDS);
      ReflectionTestUtils.setField(this, "srBuilder", new SearchResultBuilder());
    }

    @Override
    public Optional<List<SearchResult>> search(String query, SearchMethod method) {
      return Optional.empty();
    }
  }

  static class MockResultEntity implements JsonSearchResult {

    @JsonProperty("name")
    private String name;

    @JsonProperty("info_hash")
    private String infoHash;

    @JsonProperty("seeders")
    private int seeders;

    @JsonProperty("size")
    private String size;

    @JsonProperty("name")
    public String getName() {
      return name;
    }

    @JsonProperty("info_hash")
    public String getInfoHash() {
      return infoHash;
    }

    @JsonProperty("seeders")
    public int getSeeders() {
      return seeders;
    }

    @JsonProperty("size")
    public String getSize() {
      return size;
    }
  }
}
