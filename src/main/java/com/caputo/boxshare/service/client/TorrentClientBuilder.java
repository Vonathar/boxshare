package com.caputo.boxshare.service.client;

import bt.Bt;
import bt.data.Storage;
import bt.metainfo.Torrent;
import bt.runtime.BtClient;
import bt.runtime.Config;
import com.caputo.boxshare.service.TorrentFileTailer;
import java.util.function.Consumer;
import org.springframework.stereotype.Service;

/**
 * A class responsible for building pre-configured torrent clients. All static configuration is
 * delegated to {@link TorrentClientConfig}. For usage examples, see {@link TorrentClient}.
 */
@Service
public class TorrentClientBuilder {

  final Config config;
  final Storage storage;
  private final TorrentFileTailer torrentFileTailer;
  private String magnetUrl;

  public TorrentClientBuilder(TorrentFileTailer torrentFileTailer, Config config, Storage storage) {
    this.torrentFileTailer = torrentFileTailer;
    this.config = config;
    this.storage = storage;
  }

  /**
   * Builds and returns an instance of a BtClient, ready to be started.
   *
   * @return an instance of a BtClient.
   */
  public BtClient build() {
    return Bt.client()
        .afterTorrentFetched(updateTailerFileSize())
        .config(config)
        .magnet(magnetUrl)
        .sequentialSelector()
        .storage(storage)
        .autoLoadModules()
        .stopWhenDownloaded()
        .build();
  }

  /**
   * Updates the file size in the tailer using the torrent's parsed metadata.
   *
   * @return the callback for BtClient's afterTorrentFetched method.
   */
  private Consumer<Torrent> updateTailerFileSize() {
    return torrent -> torrentFileTailer.setSize((int) torrent.getSize());
  }

  /**
   * Sets the magnet URL to be downloaded by the client.
   *
   * @param magnetUrl the magnet URL.
   * @return the builder.
   */
  public TorrentClientBuilder setMagnetUrl(String magnetUrl) {
    this.magnetUrl = magnetUrl;
    return this;
  }
}
