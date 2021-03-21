package com.caputo.boxshare.service;

import com.caputo.boxshare.enumerable.SizeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FileSize {
  private final Pattern unitPattern = Pattern.compile("([A-Za-z])+");
  private final Pattern valuePattern = Pattern.compile("([0-9]+)?\\.?([0-9]+)?");
  private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
  private Matcher m;

  /**
   * Converts a string with a value to bytes.
   *
   * @param input any parsable string with a value and unit measurement.
   * @return the value of the input converted to bytes.
   */
  public double toBytes(String input) {
    try {
      double value = parseValue(input);
      double factor = parseFactor(input);
      return value * factor;
    } catch (IllegalArgumentException e) {
      logger.error(
          "Failed to convert input to bytes. Input: \"" + input + "\". Returning 0 as fallback..");
      return 0;
    }
  }

  /**
   * Parses a factor from the size unit of a given string.
   *
   * @param input any parsable string with a unit measurement.
   * @return the factor to use to transform the value into bytes.
   */
  private double parseFactor(String input) {
    m = unitPattern.matcher(input);
    if (m.find()) {
      return SizeUnit.valueOf(m.group(0).toUpperCase()).getFactor();
    } else {
      throw new IllegalArgumentException("Failed to parse factor. Input: \"" + input + "\"");
    }
  }

  /**
   * Parses a numerical value from a given string.
   *
   * @param input any parsable string with a value.
   * @return the numerical value to multiply by the factor.
   */
  private double parseValue(String input) {
    m = valuePattern.matcher(input);
    if (m.find()) {
      return Double.parseDouble(m.group(0));
    } else {
      throw new IllegalArgumentException("Failed to parse value. Input: \"" + input + "\"");
    }
  }
}
