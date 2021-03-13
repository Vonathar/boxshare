package com.caputo.boxshare;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DirectoryReader {
  public List<String> getClassNames(String path) {
    List<String> engines = new ArrayList<>();
    File[] files = new File(path).listFiles((dir, name) -> name.endsWith(".java"));
    for (File file : files) {
      String className = file.getName().replaceAll(".java$", "");
      engines.add(className);
    }
    return engines;
  }

  public List<String> getEngines() {
    return getClassNames("src/main/java/com/caputo/boxshare/service/engines").stream()
        .filter(name -> !name.equals("SearchEngine"))
        .collect(Collectors.toList());
  }
}
