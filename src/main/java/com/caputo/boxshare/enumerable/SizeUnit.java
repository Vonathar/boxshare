package com.caputo.boxshare.enumerable;

/**
 * An enumerable representing the most common unit prefixes found when parsing search results, as
 * well as their factors.
 */
public enum SizeUnit {
  BYTE(1),
  KIB(1024D),
  KB(1000D),
  MIB(1048576D),
  MB(1000000D),
  GIB(1073741824D),
  GB(1000000000D);

  public final double factor;

  SizeUnit(double factor) {
    this.factor = factor;
  }

  public double getFactor() {
    return factor;
  }
}
