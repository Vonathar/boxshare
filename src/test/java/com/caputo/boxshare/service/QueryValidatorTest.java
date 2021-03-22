package com.caputo.boxshare.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class QueryValidatorTest {

  @Autowired QueryValidator validator;

  @Test
  void validate_LegalQuery_ShouldReturnTrue() {
    assertTrue(validator.validate("test"));
  }

  @Test
  void validate_QueryTooShort_ShouldThrow400BadRequestException() {
    assertThrows(ResponseStatusException.class, () -> validator.validate(""));
  }

  @Test
  void validate_QueryWithoutWordChars_ShouldThrow400BadRequestException() {
    assertThrows(ResponseStatusException.class, () -> validator.validate("?!."));
  }
}
