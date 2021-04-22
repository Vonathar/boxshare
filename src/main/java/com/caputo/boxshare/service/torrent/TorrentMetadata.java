package com.caputo.boxshare.service.torrent;

import java.io.File;
import org.springframework.stereotype.Component;

/**
 * A class that represents the metadata of a given torrent entity. All fields are dynamically
 * populated at runtime; for an example, see {@link TorrentClientBuilder}.
 */
@Component
public class TorrentMetadata {

  private String name;
  private int size;
  private File file;
  private String extension;

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public String getExtension() {
    return extension;
  }

  public void setExtension(String extension) {
    this.extension = extension;
  }
}
