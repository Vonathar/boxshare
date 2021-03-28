package com.caputo.boxshare.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** A controller responsible for requests concerning the current status of the application. */
@RestController
public class PingController {

  @Value("${server.port}")
  private String PORT_NUMBER;

  /**
   * Returns a status message with the port number.
   *
   * @return the application status.
   */
  @GetMapping("ping")
  public String getStatus() {
    return "Boxshare is running on port " + PORT_NUMBER + ".";
  }
}
