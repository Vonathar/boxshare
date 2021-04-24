package com.caputo.boxshare.controller;

import com.caputo.boxshare.service.StreamingService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * A controller responsible for media streaming requests. It follows the standard protocol for HTTP
 * range requests, so it supports requests for either the whole file or partial content. For more
 * information about the protocol implementation, see {@link StreamingService}.
 */
@Controller
public class StreamController {

  private final StreamingService streamingService;

  public StreamController(StreamingService streamingService) {
    this.streamingService = streamingService;
  }

  /**
   * Streams a torrent media file to the client.
   *
   * @param magnetUrl the magnet URL from which the torrent should be downloaded.
   * @param range the HTTP range header of the incoming request.
   * @return an output stream with the bytes of the requested file.
   */
  @GetMapping(value = "/stream")
  public ResponseEntity<StreamingResponseBody> stream(
      @RequestBody String magnetUrl,
      @RequestHeader(value = "Range", required = false) String range) {
    return streamingService.getStream(magnetUrl, range);
  }
}
