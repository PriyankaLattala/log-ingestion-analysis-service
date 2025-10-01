package com.logs.transform.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logs.transform.dto.LogDocumentDto;
import com.logs.transform.service.LogAnalysisService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class LogAnalysisControllerTest {

  @Mock
  private LogAnalysisService logAnalysisService;

  @InjectMocks
  private LogAnalysisController controller;

  @Test
  void getByDocumentAndLevel_shouldReturnLogs_whenValidRequest() {
    Long documentId = 1L;
    String level = "ERROR";

    List<JsonNode> mockLogs = List.of(new ObjectMapper().createObjectNode().put("log.level", "ERROR"));
    LogDocumentDto dto = new LogDocumentDto(documentId, mockLogs);

    when(logAnalysisService.getLogsByDocumentAndLevel(documentId, level)).thenReturn(dto);

    ResponseEntity<LogDocumentDto> response = controller.getByDocumentAndLevel(documentId, level);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(documentId, response.getBody().id());
    assertEquals(1, response.getBody().logs().size());

    verify(logAnalysisService).getLogsByDocumentAndLevel(documentId, level);
  }
}