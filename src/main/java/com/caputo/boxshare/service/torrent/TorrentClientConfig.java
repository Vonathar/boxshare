package com.caputo.boxshare.service.torrent;

import bt.data.Storage;
import bt.data.file.FileSystemStorage;
import bt.runtime.Config;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A configuration class that holds the settings used by the torrent client. This class is only
 * responsible for the pre-build configuration. For its usage, see {@link TorrentClientBuilder}
 */
@Configuration
public class TorrentClientConfig {

  @Value("${torrent.client.ipaddr}")
  private String CLIENT_IP_ADDR;

  @Value("${torrent.download.directory}")
  private String downloadDir;

  @Bean
  public Config getConfig() {
    return new Config() {
      @Override
      public int getNumOfHashingThreads() {
        return Runtime.getRuntime().availableProcessors() * 2;
      }

      @Override
      public InetAddress getAcceptorAddress() {
        try {
          return InetAddress.getByName(CLIENT_IP_ADDR);
        } catch (UnknownHostException e) {
          e.printStackTrace();
          return null;
        }
      }
    };
  }

  @Bean
  public Storage getStorage() {
    return new FileSystemStorage(Paths.get(downloadDir));
  }
}
