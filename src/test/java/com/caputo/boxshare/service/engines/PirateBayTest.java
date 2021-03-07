package com.caputo.boxshare.service;

import com.caputo.boxshare.entity.PirateBaySearchResult;
import com.caputo.boxshare.mock.MockSearchResults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PirateBayTest {

  @Autowired PirateBay pb;
  @Autowired MockSearchResults mockSearchResults;

  @Test
  void search_LegalQuery_ShouldReturnListOfSearchResults() {
    List<PirateBaySearchResult> searchResults = pb.search("test");
    assertThat(searchResults).isNotNull();
  }
}
