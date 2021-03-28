package com.caputo.boxshare.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PingControllerTest {
  @Value("${server.port}")
  private String PORT_NUMBER;

  @Autowired private MockMvc mockMvc;

  @Test
  public void getVersion_ShouldReturnLatestVersionNumberOfApk() throws Exception {
    String EXPECTED_RESPONSE = "Boxshare is running on port " + PORT_NUMBER + ".";
    this.mockMvc
        .perform(get("/ping"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(EXPECTED_RESPONSE)));
  }
}
