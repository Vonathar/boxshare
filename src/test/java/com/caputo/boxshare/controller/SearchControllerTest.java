package com.caputo.boxshare.controller;

import com.caputo.boxshare.entity.ResultList;
import com.caputo.boxshare.entity.SearchResult;
import com.caputo.boxshare.service.QueryValidator;
import com.caputo.boxshare.service.TorrentSearcher;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@SpringBootTest
@AutoConfigureMockMvc
class SearchControllerTest {

  @Mock private QueryValidator queryValidator;
  @Mock private TorrentSearcher torrentSearcher;

  @Test
  public void search_LegalQuery_ShouldReturn200HttpResponse() throws Exception {
    when(queryValidator.validate(any())).thenReturn(true);
    when(torrentSearcher.get(any(), any()))
        .thenReturn(
            new ResultList(Collections.singletonList(new SearchResult("n", "h", 0, "0", "o"))));
    SearchController searchController = new SearchController(queryValidator, torrentSearcher);
    MockMvc mockMvc = standaloneSetup(searchController).build();
    mockMvc.perform(get("/search/test")).andExpect(status().isOk());
  }
}
