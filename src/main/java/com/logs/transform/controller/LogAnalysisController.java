package com.logs.transform.controller;

import com.logs.transform.dto.LogDocumentDto;
import com.logs.transform.service.LogAnalysisService;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for analyzing log documents by document ID and severity level.
 */
@Validated
@RestController
@RequestMapping("/logs")
@AllArgsConstructor
public class LogAnalysisController {

  private static final Logger logger = LoggerFactory.getLogger(LogAnalysisController.class);

  private final LogAnalysisService logAnalysisService;

  /**
   * Retrieves filtered log entries for a given document ID and severity level.
   *
   * @param documentId the ID of the log document
   * @param level      the log level to filter (e.g., INFO, ERROR)
   * @return filtered logs wrapped in a DTO
   */
  @GetMapping("/{documentId}")
  public ResponseEntity<LogDocumentDto> getByDocumentAndLevel(
    @PathVariable Long documentId,
    @RequestParam("level") @NotBlank String level) {

    logger.info("Fetching logs for documentId={} with level={}", documentId, level);
    LogDocumentDto logs = logAnalysisService.getLogsByDocumentAndLevel(documentId, level);
    return ResponseEntity.ok(logs);
  }
}
