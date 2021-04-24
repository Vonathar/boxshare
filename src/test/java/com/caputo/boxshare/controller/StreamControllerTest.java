package com.caputo.boxshare.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.caputo.boxshare.service.StreamingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StreamControllerTest {

  @Value("${torrent.magnet.small.url}")
  String MAGNET_URL;

  @Mock private StreamingService streamingService;
  @Autowired private StreamController streamController;
  @Autowired private MockMvc mockMvc;

  @BeforeEach
  void injectMockService() {
    when(streamingService.getStream(anyString(), anyString()))
        .thenReturn(ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(null));
    when(streamingService.getStream(anyString(), isNull()))
        .thenReturn(ResponseEntity.status(HttpStatus.OK).body(null));
    ReflectionTestUtils.setField(streamController, "streamingService", streamingService);
  }

  @Test
  void stream_RangeSpecified_ShouldReturnHttpStatus206() throws Exception {
    mockMvc
        .perform(get("/stream").content(MAGNET_URL).header("Range", "range=1024-2048"))
        .andExpect(status().isPartialContent());
  }

  @Test
  void stream_RangeNotSpecified_ShouldReturnHttpStatus200() throws Exception {
    mockMvc.perform(get("/stream").content(MAGNET_URL)).andExpect(status().isOk());
  }

  @Test
  void stream_MagnetUrlNotSpecified_ShouldReturnHttpStatus400() throws Exception {
    mockMvc.perform(get("/stream")).andExpect(status().isBadRequest());
  }
}
