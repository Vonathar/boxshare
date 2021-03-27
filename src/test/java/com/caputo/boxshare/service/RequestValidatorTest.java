package com.caputo.boxshare.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class RequestValidatorTest {

  @Autowired RequestValidator validator;

  @Test
  void validateQuery_LegalQuery_ShouldNotThrowAnyException() {
    assertDoesNotThrow(() -> validator.validateQuery("test"));
  }

  @Test
  void validateQuery_QueryTooShort_ShouldThrowIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> validator.validateQuery(""));
  }

  @Test
  void validateQuery_QueryWithoutWordChars_ShouldThrowIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> validator.validateQuery("?!."));
  }

  @Test
  void validateMethod_ExistingMethod_ShouldNotThrowAnyException() {
    assertDoesNotThrow(
        () -> {
          validator.validateQuery("quick");
          validator.validateQuery("smart");
          validator.validateQuery("complete");
        });
  }

  @Test
  void validateMethod_NonExistingMethod_ShouldThrowIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> validator.validateMethod("smrt"));
    assertThrows(IllegalArgumentException.class, () -> validator.validateMethod("quicj"));
    assertThrows(IllegalArgumentException.class, () -> validator.validateMethod("complwte"));
  }
}
