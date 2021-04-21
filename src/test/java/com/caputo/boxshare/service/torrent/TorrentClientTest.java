package com.caputo.boxshare.service.torrent;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class TorrentClientTest {
  @Autowired TorrentClient client;
  @Autowired TorrentFileTailer torrentFileTailer;

  @Value("${torrent.download.directory}")
  File DOWNLOADS_DIR;

  @Value("${torrent.magnet.small.url}")
  String MAGNET_URL;

  @Value("${torrent.magnet.small.name}")
  String MAGNET_NAME;

  @Value("${torrent.magnet.small.size}")
  int MAGNET_SIZE;

  @BeforeAll
  public void cleanDownloadsDir() throws IOException {
    FileUtils.deleteDirectory(DOWNLOADS_DIR);
  }

  @Test
  void get_ShouldStartClientAndStopWhenDone() throws Exception {
    client.get(MAGNET_URL);
    Field isRunning = client.getClass().getDeclaredField("isRunning");
    isRunning.setAccessible(true);
    assertTrue(isRunning.getBoolean(client));
  }
}
