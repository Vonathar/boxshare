package com.caputo.boxshare.service.client;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class TorrentClientBuilderTest {

  @Autowired TorrentClientBuilder clientBuilder;
  @Autowired TorrentFileTailer torrentFileTailer;

  @Value("${torrent.download.directory}")
  File DOWNLOADS_DIR;

  @Value("${torrent.magnet.small}")
  String MAGNET_URL;

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
  void build_ShouldSetFileSizeInTorrentFileTailerWhenAvailable() {
    BtClient client = clientBuilder.setMagnetUrl(MAGNET_URL).build();
    client
        .startAsync(
            state -> {
              if (state.getPiecesComplete() > 0) {
                assertTrue(torrentFileTailer.isTailing());
              }
            },
            1000)
        .join();
  }
}
