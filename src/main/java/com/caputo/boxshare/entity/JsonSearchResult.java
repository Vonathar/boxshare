package com.caputo.boxshare.entity;

/**
 * A generic search result in the JSON format. Classes implementing this interface are expected to
 * handle serialisation, but make these required fields accessible. For an example of a class
 * implementing this interface, see {@link PirateBaySearchResults}.
 */
public interface JsonSearchResult {
  String getName();

  String getInfoHash();

  int getSeeders();

  String getSize();
}
