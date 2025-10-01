package com.logs.transform.controller;

import com.logs.transform.service.LogIngestionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/logs")
@AllArgsConstructor
public class LogIngestionController {

  private final LogIngestionService logIngestionService;

  @PostMapping("/upload")
  public ResponseEntity<String> uploadLogFile(
    @RequestParam("file") MultipartFile file) {
    if (file.isEmpty()) {
      throw new IllegalArgumentException("Uploaded file is empty");
    }

    logIngestionService.uploadLogFile(file);
    return ResponseEntity.ok("Log file ingested successfully.");
  }
}
