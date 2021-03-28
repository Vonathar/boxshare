package com.caputo.boxshare.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * A controller responsible for requests that concern the client APK. The APK is expected to be in
 * the resources directory, with a name in the format: boxshare_MAJ_MIN_REV.apk (for example,
 * boxshare_0_0_1.apk).
 */
@RestController
public class ApkController {

  Logger logger = LoggerFactory.getLogger(ApkController.class);

  @Value("${boxshare.client.version}")
  private String APK_VERSION;

  /**
   * Returns the latest version number of the APK.
   *
   * @return the latest version number of the APK.
   */
  @GetMapping("version")
  public String getVersion() {
    logger.info("Incoming request for APK version.");
    return APK_VERSION;
  }

  /**
   * Returns the client APK.
   *
   * @return the installable APK.
   */
  @GetMapping(value = "download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public @ResponseBody byte[] getApk() {
    logger.info("Incoming request for APK download.");
    String fileName = "boxshare_" + APK_VERSION.replaceAll("\\.", "_") + ".apk";
    try {
      File apk = new ClassPathResource(fileName).getFile();
      InputStream in = new FileInputStream(apk);
      return IOUtils.toByteArray(in);
    } catch (IOException ex) {
      logger.error("APK not found in the resources folder: {}", fileName);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "APK not found.");
    }
  }
}
