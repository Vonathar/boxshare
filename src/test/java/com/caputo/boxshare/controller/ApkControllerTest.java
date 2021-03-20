package com.caputo.boxshare.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.file.Files;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
  public void getApk_ShouldReturnLatestVersionOfApk() throws Exception {
    String fileName = "boxshare_" + APK_VERSION.replaceAll("\\.", "_") + ".apk";
    File apk = new ClassPathResource(fileName).getFile();
    this.mockMvc
        .perform(get("/download"))
        .andExpect(status().isOk())
        .andExpect(content().bytes(Files.readAllBytes(apk.toPath())));
  }
}
