package com.caputo.boxshare.service;

import com.caputo.boxshare.enumerable.SizeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * A class responsible for converting strings representing file sizes into bytes. Processable
 * strings are expected to have at least a unit prefix (such as GB) and a numerical value.
 */
@Service
public class FileSize {
  private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
  private Matcher m;

  /**
   * Converts a string with a file size to its equivalent in bytes.
   *
   * <p>Example usage:
   *
   * <pre>{@code
   * fs.toBytes("5.87GB");
   * }</pre>
   *
   * @param input any parsable string with a value and a unit prefix.
   * @return the equivalent size of the input string in bytes.
   */
  public double toBytes(String input) {
    double value = getValue(input);
    double factor = getFactor(input);
    if (value == 0 | factor == 0) {
      logger.error("Failed to convert size to bytes. Input=\"" + input + "\"");
      return 0;
    }
    return value * factor;
  }

  /**
   * Calculates a factor from the size prefix of a given string.
   *
   * @param input any parsable string with a unit measurement.
   * @return the factor to use when converting the value into bytes.
   */
  private double getFactor(String input) {
    Pattern unitPattern = Pattern.compile("([A-Za-z])+");
    m = unitPattern.matcher(input);
    if (m.find()) {
      try {
        return SizeUnit.valueOf(m.group(0).toUpperCase()).getFactor();
      } catch (IllegalArgumentException e) {
        logger.error("Failed to parse factor. Input=\"{}\"", input);
      }
    }
    return 0;
  }

  /**
   * Extracts a numerical value from a given string.
   *
   * @param input any parsable string with a numerical value.
   * @return the numerical value.
   */
  private double getValue(String input) {
    Pattern valuePattern = Pattern.compile("([0-9]+)?\\.?([0-9]+)?");
    m = valuePattern.matcher(input);
    if (m.find()) {
      try {
        return Double.parseDouble(m.group(0));
      } catch (NumberFormatException e) {
        logger.error("Failed to parse value. Input=\"{}\"", input);
      }
    }
    return 0;
  }
}
