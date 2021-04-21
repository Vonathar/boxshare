package com.caputo.boxshare.service.client;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bt.runtime.BtClient;
import com.caputo.boxshare.service.TorrentFileTailer;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class TorrentClientBuilderTest {

  @Autowired TorrentClientBuilder clientBuilder;
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
  void build_LegalMagnetUrl_ShouldBuildAndReturnNewTorrentClientWithGivenMagnetUrl() {
    BtClient client = clientBuilder.setMagnetUrl(MAGNET_URL).build();
    assertDoesNotThrow((Executable) client::startAsync);
  }

  @Test
  void build_IllegalMagnetUrl_ShouldThrowIllegalArgumentException() {
    String MAGNET_URL = "";
    assertThrows(
        IllegalArgumentException.class, () -> clientBuilder.setMagnetUrl(MAGNET_URL).build());
  }

  @Test
  void build_ShouldSetTorrentFileInTorrentFileTailer() {
    BtClient client = clientBuilder.setMagnetUrl(MAGNET_URL).build();
    client
        .startAsync(
            state -> {
              if (state.getPiecesComplete() > 0) {
                client.stop();
              }
            },
            1000)
        .join();
    File torrentFile =
        (File)
            ReflectionTestUtils.getField(torrentFileTailer, TorrentFileTailer.class, "torrentFile");
    assertEquals(MAGNET_NAME, torrentFile.getName());
  }

  @Test
  void build_ShouldSetTorrentSizeInTorrentFileTailer() {
    BtClient client = clientBuilder.setMagnetUrl(MAGNET_URL).build();
    client
        .startAsync(
            state -> {
              if (state.getPiecesComplete() > 0) {
                client.stop();
              }
            },
            1000)
        .join();
    assertEquals(MAGNET_SIZE, torrentFileTailer.getTorrentSize());
  }

  @Test
  void build_ShouldStartTorrentFileTailer() {
    BtClient client = clientBuilder.setMagnetUrl(MAGNET_URL).build();
    client
        .startAsync(
            state -> {
              if (state.getPiecesComplete() > 0) {
                client.stop();
              }
            },
            1000)
        .join();
    assertTrue(torrentFileTailer.isTailing());
  }
}
