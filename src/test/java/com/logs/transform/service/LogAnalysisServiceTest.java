package com.logs.transform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.logs.transform.datasource.model.LogDocument;
import com.logs.transform.datasource.repository.LogDocumentRepository;
import com.logs.transform.dto.LogDocumentDto;
import com.logs.transform.exception.model.BadRequestException;
import com.logs.transform.exception.model.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LogAnalysisServiceTest {

  @Mock
  private LogDocumentRepository repository;

  @InjectMocks
  private LogAnalysisService service;

  @Test
  void getLogsByDocumentAndLevel_shouldReturnFilteredLogs_whenValidInput() throws Exception {
    Long documentId = 1L;
    String level = "ERROR";

    // JSON array string with mixed log.level entries
    String jsonContent = "[" +
      "{\"log.level\":\"INFO\",\"message\":\"info message\"}," +
      "{\"log.level\":\"ERROR\",\"message\":\"error message\"}," +
      "{\"log.level\":\"ERROR\",\"message\":\"another error message\"}" +
      "]";

    LogDocument mockDocument = new LogDocument();
    mockDocument.setId(documentId);
    mockDocument.setContent(jsonContent);

    when(repository.findById(documentId)).thenReturn(Optional.of(mockDocument));

    LogDocumentDto result = service.getLogsByDocumentAndLevel(documentId, level);

    assertEquals(documentId, result.id());
    assertEquals(2, result.logs().size());

    for (JsonNode log : result.logs()) {
      assertEquals(level.toUpperCase(), log.get("log.level").asText().toUpperCase());
    }
  }

  @Test
  void getLogsByDocumentAndLevel_shouldThrowEntityNotFoundException_whenDocumentMissing() {
    Long documentId = 99L;
    String level = "INFO";

    when(repository.findById(documentId)).thenReturn(Optional.empty());

    EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
      service.getLogsByDocumentAndLevel(documentId, level));

    assertTrue(ex.getMessage().contains("Log document not found with id: " + documentId));
  }

  @Test
  void getLogsByDocumentAndLevel_shouldThrowBadRequestException_whenLevelIsEmpty() {
    Long documentId = 1L;
    String level = "  ";

    LogDocument mockDocument = new LogDocument();
    mockDocument.setId(documentId);
    mockDocument.setContent("[]");
    when(repository.findById(documentId)).thenReturn(Optional.of(mockDocument));

    BadRequestException ex = assertThrows(BadRequestException.class, () ->
      service.getLogsByDocumentAndLevel(documentId, level));

    assertEquals("Log level must be provided.", ex.getMessage());
  }

  @Test
  void getLogsByDocumentAndLevel_shouldThrowRuntimeException_whenJsonParsingFails() {
    Long documentId = 1L;
    String level = "INFO";

    String invalidJson = "{invalid-json}";

    LogDocument mockDocument = new LogDocument();
    mockDocument.setId(documentId);
    mockDocument.setContent(invalidJson);
    when(repository.findById(documentId)).thenReturn(Optional.of(mockDocument));

    RuntimeException ex = assertThrows(RuntimeException.class, () ->
      service.getLogsByDocumentAndLevel(documentId, level));

    assertTrue(ex.getMessage().contains("Failed to parse JSON content from document " + documentId));
  }
}