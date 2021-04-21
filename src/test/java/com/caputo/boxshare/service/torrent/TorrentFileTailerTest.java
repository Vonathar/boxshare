package com.caputo.boxshare.service.torrent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TorrentFileTailerTest {

  private static String filePath;
  private final int FILE_SIZE = 5242880; // 5,242,880
  private final int PIECE_SIZE = 1048576; // 1,048,576

  @BeforeEach
  void initDownloadDir(@Value("${torrent.download.directory}") Path downloadsDir) throws Exception {
    if (Files.exists(downloadsDir)) {
      File dir = new File(String.valueOf(downloadsDir));
      FileUtils.cleanDirectory(dir);
    } else {
      Files.createDirectory(downloadsDir);
    }
    String FILE_NAME = "testFile.mkv";
    filePath = downloadsDir + "/" + FILE_NAME;
  }

  @Test
  void run_ShouldTailTorrentFileUntilCompletion() throws Exception {
    TorrentFileTailer torrentFileTailer = new TorrentFileTailer();
    torrentFileTailer.setTorrentFile(new File(filePath));
    torrentFileTailer.setTorrentSize(FILE_SIZE);
    TestListener listener = new TestListener();
    torrentFileTailer.setListener(listener);
    torrentFileTailer.start();
    try (RandomAccessFile raf = new RandomAccessFile(filePath, "rw")) {
      int downloadedSize = 0;
      while (downloadedSize < FILE_SIZE) {
        assertTrue(torrentFileTailer.isTailing());
        downloadedSize += PIECE_SIZE;
        raf.setLength(downloadedSize);
        Thread.sleep(1000);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    torrentFileTailer.join();
    assertFalse(torrentFileTailer.isTailing());
    assertEquals(listener.getBytesReceived(), FILE_SIZE);
  }

  @Test
  void run_HangingDownload_ShouldTailTorrentFileUntilCompletion() throws Exception {
    TorrentFileTailer torrentFileTailer = new TorrentFileTailer();
    torrentFileTailer.setTorrentFile(new File(filePath));
    torrentFileTailer.setTorrentSize(FILE_SIZE);
    TestListener listener = new TestListener();
    torrentFileTailer.setListener(listener);
    torrentFileTailer.start();
    try (RandomAccessFile raf = new RandomAccessFile(filePath, "rw")) {
      int downloadedSize = 0;
      boolean shouldPause = true;
      while (downloadedSize < FILE_SIZE) {
        assertTrue(torrentFileTailer.isTailing());
        downloadedSize += PIECE_SIZE;
        raf.setLength(downloadedSize);
        if (shouldPause) {
          Thread.sleep(3000);
        } else {
          Thread.sleep(1000);
        }
        shouldPause = !shouldPause;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    torrentFileTailer.join();
    assertFalse(torrentFileTailer.isTailing());
    assertEquals(listener.getBytesReceived(), FILE_SIZE);
  }

  @Test
  void run_IrregularIncomingByteCount_ShouldTailTorrentFileUntilCompletion() throws Exception {
    TorrentFileTailer torrentFileTailer = new TorrentFileTailer();
    torrentFileTailer.setTorrentFile(new File(filePath));
    torrentFileTailer.setTorrentSize(FILE_SIZE);
    TestListener listener = new TestListener();
    torrentFileTailer.setListener(listener);
    torrentFileTailer.start();
    try (RandomAccessFile raf = new RandomAccessFile(filePath, "rw")) {
      int downloadedSize = 0;
      while (downloadedSize < FILE_SIZE) {
        assertTrue(torrentFileTailer.isTailing());
        int newBytes = PIECE_SIZE - ThreadLocalRandom.current().nextInt(0, PIECE_SIZE);
        if (downloadedSize + newBytes < FILE_SIZE) {
          downloadedSize += newBytes;
        } else {
          downloadedSize += FILE_SIZE - downloadedSize;
        }
        raf.setLength(downloadedSize);
        Thread.sleep(1000);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    torrentFileTailer.join();
    assertFalse(torrentFileTailer.isTailing());
    assertEquals(FILE_SIZE, listener.getBytesReceived());
  }

  static class TestListener implements TorrentFileTailerListener {

    private int bytesReceived = 0;

    public int getBytesReceived() {
      return bytesReceived;
    }

    @Override
    public void newBytes(byte[] bytes) {
      bytesReceived += bytes.length;
    }
  }
}
