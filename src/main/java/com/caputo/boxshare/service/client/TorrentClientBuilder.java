package com.caputo.boxshare.service.client;

import bt.Bt;
import bt.data.Storage;
import bt.metainfo.Torrent;
import bt.metainfo.TorrentFile;
import bt.runtime.BtClient;
import bt.runtime.Config;
import com.caputo.boxshare.enumerable.VideoExtension;
import com.caputo.boxshare.service.TorrentFileTailer;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("${torrent.download.directory}")
  private String DOWNLOADS_DIR;

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
        .afterTorrentFetched(afterTorrentFetched())
        .config(config)
        .magnet(magnetUrl)
        .sequentialSelector()
        .storage(storage)
        .autoLoadModules()
        .stopWhenDownloaded()
        .build();
  }

  /**
   * Updates the tailer with the newly parsed metadata.
   *
   * @return the callback for BtClient's afterTorrentFetched method.
   */
  private Consumer<Torrent> afterTorrentFetched() {
    return torrent -> {
      torrentFileTailer.setSize((int) torrent.getSize());
      torrentFileTailer.setTorrentFile(findVideoFile(torrent));
      torrentFileTailer.start();
    };
  }

  /**
   * Finds the video file in a torrent's path elements.
   *
   * @param torrent a torrent file with loaded metadata.
   * @return the video file.
   */
  private File findVideoFile(Torrent torrent) {
    for (TorrentFile file : torrent.getFiles()) {
      List<String> pathElements = file.getPathElements();
      String fileName = pathElements.get(pathElements.size() - 1);
      int i = fileName.lastIndexOf('.');
      String extension = fileName.substring(i + 1);
      if (VideoExtension.isValidEnum(extension)) {
        Path filePath = Path.of(DOWNLOADS_DIR, torrent.getName(), fileName);
        return new File(String.valueOf(filePath));
      }
    }
    return null;
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
