package com.caputo.boxshare.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@SpringBootTest
@ActiveProfiles("test")
class StreamingServiceTest {

  @Autowired private StreamingService streamingService;

  @Value("${torrent.magnet.small.url}")
  private String MAGNET_URL;

  @Value("${torrent.magnet.small.size}")
  private int MAGNET_SIZE;

  @Test
  void getStream_NoRange_ShouldReturnAllBytes() throws IOException {
    ResponseEntity<StreamingResponseBody> responseEntity =
        streamingService.getStream(MAGNET_URL, null);
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    responseEntity.getBody().writeTo(os);
    assertEquals(MAGNET_SIZE, os.size());
  }

  @Test
  void getStream_WithRange_ShouldReturnBytesInRequestedRange() throws IOException {
    int START_RANGE = 1048576;
    int END_RANGE = 8388608;
    String range = "bytes=" + START_RANGE + "-" + END_RANGE;
    ResponseEntity<StreamingResponseBody> responseEntity =
        streamingService.getStream(MAGNET_URL, range);
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    responseEntity.getBody().writeTo(os);
    int EXPECTED_SIZE = (END_RANGE - START_RANGE) - 1;
    assertEquals(EXPECTED_SIZE, os.size());
  }

  @Test
  void newBytes_EmptyFileData_ShouldStoreNewBytesInFileData() {
    streamingService.newBytes(new byte[1024 * 1024]);
    byte[] fileData = (byte[]) ReflectionTestUtils.getField(streamingService, "fileData");
    int EXPECTED_SIZE = 1024 * 1024;
    assertEquals(EXPECTED_SIZE, fileData.length);
  }

  @Test
  void newBytes_NonEmptyFileData_ShouldStoreOldAndNewBytesInFileData() {
    int BUFFER_SIZE = 1024 * 1024;
    ReflectionTestUtils.setField(streamingService, "fileData", new byte[BUFFER_SIZE]);
    streamingService.newBytes(new byte[BUFFER_SIZE]);
    byte[] fileData = (byte[]) ReflectionTestUtils.getField(streamingService, "fileData");
    int EXPECTED_SIZE = BUFFER_SIZE * 2;
    assertEquals(EXPECTED_SIZE, fileData.length);
  }
}
