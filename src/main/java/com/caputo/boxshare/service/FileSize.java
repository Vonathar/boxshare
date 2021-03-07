package com.caputo.boxshare.service;

import com.caputo.boxshare.enumerable.SizeUnit;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FileSize {

  private final Pattern unitPattern = Pattern.compile("([A-Za-z])+");
  private final Pattern valuePattern = Pattern.compile("([0-9]+)?\\.?([0-9]+)?");

  /**
   * Converts a string with a value to bytes.
   *
   * @param input any parsable string with a value and unit measurement.
   * @return the value of the input converted to bytes.
   */
  public double toBytes(String input) {
    Matcher m = unitPattern.matcher(input);
    double factor = 0;
    if (m.find()) {
      factor = SizeUnit.valueOf(m.group(0).toUpperCase()).getFactor();
    }
    m = valuePattern.matcher(input);
    double value = 0;
    if (m.find()) {
      value = Double.parseDouble(m.group(0));
    }
    return value * factor;
  }
}
