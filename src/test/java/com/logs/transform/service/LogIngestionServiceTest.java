package com.logs.transform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logs.transform.datasource.model.LogDocument;
import com.logs.transform.datasource.repository.LogDocumentRepository;
import com.logs.transform.exception.model.FileParseException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
public class LogIngestionServiceTest {

  @Mock
  private LogDocumentRepository repository;

  @InjectMocks
  private LogIngestionService service;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void uploadLogFile_shouldParseAndSaveLogDocument() throws Exception {
    String logContent =
      "{\"@timestamp\":\"2025-10-01T13:32:43.082Z\",\"log.level\":\"INFO\",\"message\":\"request received {\\\"operation\\\":\\\"Test\\\"}\"}\n" +
        "{\"@timestamp\":\"2025-10-01T13:32:44.100Z\",\"log.level\":\"ERROR\",\"message\":\"processing failed\"}\n";

    MockMultipartFile mockFile = new MockMultipartFile(
      "file",
      "logs.txt",
      MediaType.TEXT_PLAIN_VALUE,
      logContent.getBytes(StandardCharsets.UTF_8)
    );

    // Capture the saved LogDocument
    ArgumentCaptor<LogDocument> captor = ArgumentCaptor.forClass(LogDocument.class);

    service.uploadLogFile(mockFile);

    verify(repository).save(captor.capture());
    LogDocument savedDoc = captor.getValue();

    assertEquals("logs.txt", savedDoc.getFilename());
    assertNotNull(savedDoc.getUploadedAt());

    // Check content is a valid JSON array string containing both logs
    JsonNode node = objectMapper.readTree(savedDoc.getContent());
    assertTrue(node.isArray());
    assertEquals(2, node.size());

    assertEquals("INFO", node.get(0).get("log.level").asText());
    assertEquals("ERROR", node.get(1).get("log.level").asText());
  }

  @Test
  void uploadLogFile_shouldThrowFileParseException_whenFileHasNoValidEntries() throws Exception {
    String emptyContent = "\n\n   \n";

    MockMultipartFile emptyFile = new MockMultipartFile(
      "file",
      "empty.log",
      MediaType.TEXT_PLAIN_VALUE,
      emptyContent.getBytes(StandardCharsets.UTF_8)
    );

    FileParseException exception = assertThrows(FileParseException.class, () -> {
      service.uploadLogFile(emptyFile);
    });

    assertEquals("File contains no valid log entries.", exception.getMessage());
    verify(repository, never()).save(any());
  }

  @Test
  void uploadLogFile_shouldThrowRuntimeException_onInvalidJsonLine() throws Exception {
    String invalidJsonLine = "{\"invalidJson: true}\n";

    MockMultipartFile mockFile = new MockMultipartFile(
      "file",
      "invalid.log",
      MediaType.TEXT_PLAIN_VALUE,
      invalidJsonLine.getBytes(StandardCharsets.UTF_8)
    );

    RuntimeException ex = assertThrows(RuntimeException.class, () -> {
      service.uploadLogFile(mockFile);
    });

    assertTrue(ex.getMessage().contains("Invalid JSON line"));
    verify(repository, never()).save(any());
  }
}