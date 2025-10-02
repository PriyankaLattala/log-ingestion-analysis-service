package com.logs.transform.controller;

import com.logs.transform.exception.model.BadRequestException;
import com.logs.transform.service.LogIngestionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/logs")
@AllArgsConstructor
@Validated
public class LogIngestionController {

  private final LogIngestionService logIngestionService;

  /**
   * Endpoint to upload a log file for ingestion.
   *
   * @param file the uploaded multipart file
   * @return HTTP 200 with success message
   */
  @PostMapping("/upload")
  public ResponseEntity<String> uploadLogFile(@RequestParam("file") MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new BadRequestException("Uploaded file is empty or missing");
    }

    String filename = file.getOriginalFilename();
    if (!(filename.endsWith(".log") || filename.endsWith(".txt"))) {
      throw new IllegalArgumentException("Only .log or .txt files are supported");
    }

    logIngestionService.uploadLogFile(file);
    return ResponseEntity.ok("Log file ingested successfully.");
  }
}
