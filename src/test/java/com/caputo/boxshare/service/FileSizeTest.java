package com.caputo.boxshare.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FileSizeTest {

  @Autowired FileSize fs;

  @Test
  public void toBytes_IllegalValue_ShouldReturnZero() {
    assertEquals(fs.toBytes("?MB"), 0);
  }

  @Test
  public void toBytes_IllegalFactor_ShouldReturnZero() {
    assertEquals(fs.toBytes("5.52XB"), 0);
  }

  @Test
  public void toBytes_ArbitraryWhitespaces_ShouldParseBytesFromStringFileSize() {
    assertThat(fs.toBytes("5.87GiB")).isEqualTo(6302864506.88);
    assertThat(fs.toBytes("26.32 MiB")).isEqualTo(27598520.32);
    assertThat(fs.toBytes("146.43  KiB")).isEqualTo(149944.32);
    assertThat(fs.toBytes("5.87GB")).isEqualTo(5870000000L);
    assertThat(fs.toBytes("26.32 MB")).isEqualTo(26320000);
    assertThat(fs.toBytes("146.43  KB")).isEqualTo(146430);
  }

  @Test
  public void toBytes_ArbitraryCase_ShouldParseBytesFromStringFileSize() {
    assertThat(fs.toBytes("5.87 gib")).isEqualTo(6302864506.88);
    assertThat(fs.toBytes("6.36 GIB")).isEqualTo(6828998000.64);
    assertThat(fs.toBytes("1.23 gIb")).isEqualTo(1320702443.52);
    assertThat(fs.toBytes("5.87gB")).isEqualTo(5870000000L);
    assertThat(fs.toBytes("26.32 Mb")).isEqualTo(26320000);
    assertThat(fs.toBytes("146.43  kb")).isEqualTo(146430);
  }

  @Test
  public void toBytes_ArbitraryDecimalPlaces_ShouldParseBytesFromStringFileSize() {
    assertThat(fs.toBytes("5.87142 gib")).isEqualTo(6304389220.27008);
    assertThat(fs.toBytes("6.36512 GIB")).isEqualTo(6834495558.77888);
    assertThat(fs.toBytes("1.23661 gIb")).isEqualTo(1327799876.97664);
    assertThat(fs.toBytes("5.87661GB")).isEqualTo(5876610000L);
    assertThat(fs.toBytes("26.3241 MB")).isEqualTo(26324100);
    assertThat(fs.toBytes("146.435  KB")).isEqualTo(146435);
  }

  @Test
  public void parseFactor_IllegalInput_ShouldThrowIllegalArgumentException() throws Exception {
    Method parseFactor = fs.getClass().getDeclaredMethod("parseFactor", String.class);
    parseFactor.setAccessible(true);
    assertThrows(Exception.class, () -> parseFactor.invoke(fs, "7xb"));
  }

  @Test
  public void parseValue_IllegalInput_ShouldThrowIllegalArgumentException() throws Exception {
    Method parseValue = fs.getClass().getDeclaredMethod("parseValue", String.class);
    parseValue.setAccessible(true);
    assertThrows(Exception.class, () -> parseValue.invoke(fs, "?gb"));
  }
}
