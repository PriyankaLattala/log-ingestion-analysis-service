package com.logs.transform;

import com.logs.transform.datasource.repository.LogDocumentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LogIngestionAnalysisIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private LogDocumentRepository repository;

  @Test
  void testUploadLogFileAndQueryByLevel() throws Exception {
    String logEntry1 = "{\"log.level\":\"INFO\",\"message\":\"Info log entry\"}\n";
    String logEntry2 = "{\"log.level\":\"ERROR\",\"message\":\"Error log entry\"}\n";

    MockMultipartFile file = new MockMultipartFile(
      "file", "test.log", "application/json",
      (logEntry1 + logEntry2).getBytes()
    );

    // Upload file
    mockMvc.perform(multipart("/logs/upload").file(file))
           .andExpect(status().isOk())
           .andExpect(content().string(containsString("successfully")));

    // Query logs with level=ERROR (assuming the uploaded document has id 1)
    Long documentId = repository.findAll().get(0).getId();

    mockMvc.perform(get("/logs/{documentId}", documentId).param("level", "ERROR"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.logs[0]['log.level']").value("ERROR"))
           .andExpect(jsonPath("$.logs[0]['message']").value("Error log entry"));
  }
}
