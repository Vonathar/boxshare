package com.caputo.boxshare.controller;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@AutoConfigureMockMvc
class ApkControllerTest {

  @Value("${boxshare.client.version}")
  String APK_VERSION;

  @Autowired private MockMvc mockMvc;

  @Test
  public void getVersion_ShouldReturnLatestVersionNumberOfApk() throws Exception {
    this.mockMvc
        .perform(get("/version"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(APK_VERSION)));
  }

  @Test
  public void getApk_ApkFound_ShouldReturnLatestVersionOfApk() throws Exception {
    String fileName = "boxshare_" + APK_VERSION.replaceAll("\\.", "_") + ".apk";
    File apk = new ClassPathResource(fileName).getFile();
    this.mockMvc
        .perform(get("/download"))
        .andExpect(status().isOk())
        .andExpect(content().bytes(Files.readAllBytes(apk.toPath())));
  }

  @Test
  public void getApk_ApkNotFound_ShouldThrow404ResponseStatusException() {
    ApkController apkController = new ApkController();
    ReflectionTestUtils.setField(apkController, "APK_VERSION", "0.0.0");
    try {
      apkController.getApk();
    } catch (ResponseStatusException e) {
      assertEquals(e.getReason(), "APK not found.");
      assertEquals(e.getStatus(), HttpStatus.NOT_FOUND);
    }
  }
}
