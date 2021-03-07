package com.caputo.boxshare.mock;

import com.caputo.boxshare.entity.PirateBaySearchResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * This utility class is used to read mock search results from the local "test.json" file. The
 * returned search results are very likely not representative of the response of an actual search,
 * as these are going to change nearly constantly.
 */
@Service
public class PirateBayMockResults {
  @Value("classpath:tpb-mock-search-results/test.json")
  Resource resourceFile;

  public List<PirateBaySearchResult> getAll() throws IOException {
    Reader reader = new InputStreamReader(resourceFile.getInputStream(), UTF_8);
    String in = FileCopyUtils.copyToString(reader);
    return new ObjectMapper()
        .reader()
        .forType(new TypeReference<List<PirateBaySearchResult>>() {})
        .readValue(in);
  }

  public PirateBaySearchResult getFirst() throws IOException {
    return getAll().get(0);
  }
}
