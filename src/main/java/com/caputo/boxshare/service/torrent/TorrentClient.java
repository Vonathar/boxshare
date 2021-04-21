package com.caputo.boxshare.service.torrent;

import bt.runtime.BtClient;
import bt.torrent.TorrentSessionState;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * A class that represents an instance of a torrent client, responsible for starting and managing
 * the torrent client. Instantiation of the client itself is delegated to the corresponding builder
 * class, {@link TorrentClientBuilder}. For the configuration, see {@link TorrentClientConfig}
 */
@Service
public class TorrentClient {
  private final TorrentClientBuilder clientBuilder;
  private final TorrentFileTailer torrentFileTailer;
  private final Logger logger = LoggerFactory.getLogger(TorrentClient.class);
  private BtClient client;
  private boolean isRunning = false;

  public TorrentClient(TorrentFileTailer torrentFileTailer, TorrentClientBuilder clientBuilder) {
    this.torrentFileTailer = torrentFileTailer;
    this.clientBuilder = clientBuilder;
  }

  /**
   * Starts the torrent client to download the given magnet URL. The torrent client is only started
   * if it's not already running.
   *
   * @param magnetUrl the magnet URL
   */
  public void get(String magnetUrl) {
    if (isRunning) {
      return;
    }
    client = clientBuilder.setMagnetUrl(magnetUrl).build();
    client.startAsync(onStateChange(), 1000);
    isRunning = true;
  }

  /**
   * Monitors the status of the download, and starts the tailer as soon as some pieces have been
   * downloaded. It closes the client once all pieces have been downloaded.
   *
   * @return the callback for BtClient's startAsync method.
   */
  private Consumer<TorrentSessionState> onStateChange() {
    return state -> {
      int peers = state.getConnectedPeers().size();
      int downloaded = (state.getPiecesComplete() / state.getPiecesTotal()) * 100;
      logger.info("Peers: {}, Downloaded: {}", peers, downloaded);
      if (state.getPiecesRemaining() == 0) {
        client.stop();
      }
    };
  }
}
