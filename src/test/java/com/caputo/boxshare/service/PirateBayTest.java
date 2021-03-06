package com.caputo.boxshare.service;

import com.caputo.boxshare.entity.PirateBaySearchResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PirateBayTest {

  @Autowired PirateBay pb;

  @Test
  void search_LegalQuery_ShouldReturnListOfSearchResults() {
    List<PirateBaySearchResult> searchResults = pb.search("test");
    assertThat(searchResults).isNotNull();
  }
}
