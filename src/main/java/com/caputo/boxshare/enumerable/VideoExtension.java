package com.caputo.boxshare.enumerable;

/**
 * An enumerable that represents the valid file extensions of a video file. The main purpose is that
 * of obtaining the relevant files from a given torrent's file list. For an example of its usage,
 * see {@link com.caputo.boxshare.service.torrent.TorrentClientBuilder}
 */
public enum VideoExtension {
  MKV,
  AVI,
  MP4;

  /**
   * Checks whether a given string represents any value of this enum.
   *
   * @param value the string to validate.
   * @return whether the string represents any value of this enum.
   */
  public static boolean isValidEnum(String value) {
    try {
      VideoExtension.valueOf(value.toUpperCase());
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
