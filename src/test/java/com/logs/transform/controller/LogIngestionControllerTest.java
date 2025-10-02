package com.logs.transform.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.logs.transform.exception.model.BadRequestException;
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
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

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

    BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
      controller.uploadLogFile(emptyFile);
    });

    assertEquals("Uploaded file is empty or missing", thrown.getMessage());
    verify(logIngestionService, never()).uploadLogFile(any());
  }

  @Test
  void uploadLogFile_shouldThrowException_whenFileExtensionIsInvalid() {
    MultipartFile file = new MockMultipartFile("file", "file.pdf", "application/pdf", "dummy".getBytes());

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
      controller.uploadLogFile(file)
    );

    assertEquals("Only .log or .txt files are supported", ex.getMessage());
  }

}