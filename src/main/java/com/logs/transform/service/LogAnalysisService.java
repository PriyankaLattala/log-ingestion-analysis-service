package com.logs.transform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logs.transform.datasource.model.LogDocument;
import com.logs.transform.datasource.repository.LogDocumentRepository;
import com.logs.transform.dto.LogDocumentDto;
import com.logs.transform.exception.model.BadRequestException;
import com.logs.transform.exception.model.EntityNotFoundException;
import com.logs.transform.exception.model.FileParseException;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for analyzing log documents.
 */
@AllArgsConstructor
@Service
public class LogAnalysisService {

  private final LogDocumentRepository repository;
  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Retrieves filtered log entries for a given document ID and severity level.
   *
   * @param documentId the document ID
   * @param level      the log level (e.g., INFO, ERROR)
   * @return a DTO containing the document ID and filtered logs
   */
  public LogDocumentDto getLogsByDocumentAndLevel(Long documentId, String level) {
    LogDocument document = repository.findById(documentId)
                                     .orElseThrow(
                                       () -> new EntityNotFoundException("Log document not found with id: " + documentId));

    if (level == null || level.trim().isEmpty()) {
      throw new BadRequestException("Log level must be provided.");
    }
    List<JsonNode> filteredLogs = new ArrayList<>();
    try {
      JsonNode logsArray = objectMapper.readTree(document.getContent());

      if (logsArray.isArray()) {
        for (JsonNode logEntry : logsArray) {
          JsonNode logLevelNode = logEntry.get("log.level");
          if (logLevelNode != null && level.equalsIgnoreCase(logLevelNode.asText())) {
            filteredLogs.add(logEntry);
          }
        }
      }
    } catch (JsonProcessingException e) {
      throw new FileParseException("Failed to parse JSON content from document " + documentId, e);
    }
    return new LogDocumentDto(documentId, filteredLogs);
  }
}
