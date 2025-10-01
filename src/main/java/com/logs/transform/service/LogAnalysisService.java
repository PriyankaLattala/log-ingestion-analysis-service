package com.logs.transform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logs.transform.datasource.model.LogDocument;
import com.logs.transform.datasource.repository.LogDocumentRepository;
import com.logs.transform.dto.LogDocumentDto;
import com.logs.transform.exception.model.BadRequestException;
import com.logs.transform.exception.model.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@AllArgsConstructor
@Service
public class LogAnalysisService {

  private final LogDocumentRepository repository;
  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Returns filtered logs by document id and level.
   *
   * @param documentId the document id
   * @param level the log level
   * @return the filtered logs
   * @throws EntityNotFoundException if the log document is not found
   * @throws BadRequestException if the log level is not provided
   */
  public LogDocumentDto getLogsByDocumentAndLevel(Long documentId, String level) {
    LogDocument document = repository.findById(documentId)
                                     .orElseThrow(
                                       () -> new EntityNotFoundException("Log document not found with id: " + documentId));

    if (ObjectUtils.isEmpty(level.trim())) {
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
      throw new RuntimeException("Failed to parse JSON content from document " + documentId, e);
    }
    return new LogDocumentDto(documentId, filteredLogs);
  }
}
