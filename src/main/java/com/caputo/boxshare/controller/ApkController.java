package com.caputo.boxshare.controller;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;

/**
 * This controller is responsible for all requests that concern the client APK. The APK is expected
 * to be in the resources directory, with a name in the format: boxshare_<MAJ>_<MIN>_<REV>.apk (for
 * example, boxshare_0_0_1.apk).
 */
@RestController
public class ApkController {

  @Value("${boxshare.client.version}")
  String APK_VERSION;

  Logger logger = LoggerFactory.getLogger(ApkController.class);

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
    File apk = null;
    String fileName = "boxshare_" + APK_VERSION.replaceAll("\\.", "_") + ".apk";
    System.out.println(fileName);
    try {
      apk = new ClassPathResource(fileName).getFile();
    } catch (IOException e) {
      logger.error("Failed to get APK from class path. Error: " + e);
    }
    InputStream in = null;
    try {
      assert apk != null;
      in = new FileInputStream(apk);
    } catch (FileNotFoundException e) {
      logger.error("APK not found. Error: " + e);
    }
    logger.info("Returning APK to client.");
    byte[] bytes = null;
    try {
      bytes = IOUtils.toByteArray(in);
    } catch (IOException e) {
      logger.error("Failed to read APK as input stream. Error: " + e);
    }
    return bytes;
  }
}
