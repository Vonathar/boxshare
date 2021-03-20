package com.caputo.boxshare.controller;

import com.caputo.boxshare.controller.mock.MockResultList;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SearchControllerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  public void search_ShouldReturnSearchResultsDeserialisableByTheClient() throws Exception {
    String result =
        this.mockMvc
            .perform(get("/search/sample test"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    new ObjectMapper().readValue(result, MockResultList.class);
  }
}
