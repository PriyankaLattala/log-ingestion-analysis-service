package com.logs.transform.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.logs.transform.service.LogIngestionService;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
public class LogIngestionControllerTest {

  @Mock
  private LogIngestionService logIngestionService;

  @InjectMocks
  private LogIngestionController controller;

  @Test
  void uploadLogFile_shouldReturnOk_whenFileIsValid() throws Exception {
    MockMultipartFile mockFile = new MockMultipartFile(
      "file",
      "test.log",
      MediaType.TEXT_PLAIN_VALUE,
      "dummy content".getBytes(StandardCharsets.UTF_8)
    );

    ResponseEntity<String> response = controller.uploadLogFile(mockFile);

    verify(logIngestionService).uploadLogFile(mockFile);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Log file ingested successfully.", response.getBody());
  }

  @Test
  void uploadLogFile_shouldThrowException_whenFileIsEmpty() {
    MockMultipartFile emptyFile = new MockMultipartFile(
      "file",
      "empty.log",
      MediaType.TEXT_PLAIN_VALUE,
      new byte[0]
    );

    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      controller.uploadLogFile(emptyFile);
    });

    assertEquals("Uploaded file is empty", thrown.getMessage());
    verify(logIngestionService, never()).uploadLogFile(any());
  }
}