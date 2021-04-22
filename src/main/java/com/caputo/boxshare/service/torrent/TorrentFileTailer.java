package com.caputo.boxshare.service.torrent;

import java.io.File;
import java.io.RandomAccessFile;
import org.springframework.stereotype.Service;

/**
 * A class that represents a file tailer, responsible for continuously keeping track of a given file
 * for any newly added bytes. Whenever new bytes are added to the tailed file, the tailer notifies
 * its listeners - sending them the newly-added bytes. For the interface that a listener has to
 * implement, see {@link TorrentFileTailerListener}.
 */
@Service
public class TorrentFileTailer extends Thread {

  final TorrentMetadata torrentMetadata;
  private boolean isTailing = true;
  private TorrentFileTailerListener listener;

  public TorrentFileTailer(TorrentMetadata torrentMetadata) {
    this.torrentMetadata = torrentMetadata;
  }

  public boolean isTailing() {
    return isTailing;
  }

  public void setListener(TorrentFileTailerListener listener) {
    this.listener = listener;
  }

  /**
   * Starts the tailer to continuously monitor the file for any new bytes, which are then sent to
   * the registered listener. The tailer automatically stops whenever the tailed file reaches the
   * size declared in the torrent's metadata.
   */
  public void run() {
    try {
      int torrentSize = torrentMetadata.getSize();
      File torrentFile = torrentMetadata.getFile();
      while (!torrentFile.exists()) {
        sleep(1000);
      }
      isTailing = true;
      RandomAccessFile raf = new RandomAccessFile(torrentFile, "r");
      long rafPointer = 0;
      while (isTailing) {
        long localFileLength = torrentFile.length();
        while (localFileLength > rafPointer) {
          byte[] buffer;
          int PREFERRED_BUFFER_SIZE = 1024 * 1024;
          if (localFileLength - rafPointer < PREFERRED_BUFFER_SIZE) {
            buffer = new byte[(int) (localFileLength - rafPointer)];
          } else {
            buffer = new byte[1024 * 1024];
          }
          raf.seek(rafPointer);
          raf.read(buffer);
          listener.newBytes(buffer);
          rafPointer = raf.getFilePointer();
        }
        if (rafPointer >= torrentSize) {
          isTailing = false;
        }
        sleep(1000);
      }
      raf.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
