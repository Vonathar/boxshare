package com.caputo.boxshare.service.torrent;

/**
 * An interface that must be implemented by any class that should handle the bytes found by the
 * {@link TorrentFileTailer}.
 */
public interface TorrentFileTailerListener {
  /**
   * Handles the newly-found bytes by the tailer.
   *
   * @param bytes the new bytes received by the tailer.
   */
  void newBytes(byte[] bytes);
}
