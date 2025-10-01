package com.logs.transform.controller;

import com.logs.transform.dto.LogDocumentDto;
import com.logs.transform.service.LogAnalysisService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
@AllArgsConstructor
public class LogAnalysisController {

  private final LogAnalysisService logAnalysisService;
  @GetMapping("/{documentId}")
  public ResponseEntity<LogDocumentDto> getByDocumentAndLevel(
    @PathVariable Long documentId,
    @RequestParam("level") String level)  {

    LogDocumentDto logs = logAnalysisService.getLogsByDocumentAndLevel(documentId, level);
    return ResponseEntity.ok(logs);
  }
}
