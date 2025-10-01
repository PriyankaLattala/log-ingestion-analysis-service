package com.logs.transform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logs.transform.datasource.model.LogDocument;
import com.logs.transform.datasource.repository.LogDocumentRepository;
import com.logs.transform.exception.model.FileParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class LogIngestionService {

  private LogDocumentRepository repository;
  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Save the provided log document to the database.
   *
   * @param file the uploaded log file
   * @throws JsonProcessingException if the provided log content cannot be parsed as valid JSON
   * @throws FileParseException if the provided log content is empty or cannot be parsed
   */
  public void uploadLogFile(MultipartFile file) {
    try {
      List<JsonNode> jsonLogs = new ArrayList<>();

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
        reader.lines().forEach(line -> {
          if (!line.trim().isEmpty()) {
            try {
              jsonLogs.add(objectMapper.readTree(line));
            } catch (JsonProcessingException e) {
              throw new RuntimeException("Invalid JSON line: " + line, e);
            }
          }
        });
      }

      if (jsonLogs.isEmpty()) {
        throw new FileParseException("File has no valid log entries.");
      }
      String jsonArrayString = objectMapper.writeValueAsString(jsonLogs);

      createLogDocument(file, jsonArrayString);
    } catch (IOException e) {
      throw new FileParseException("Failed to read the uploaded log file: " + e.getMessage(), e);
    }
  }

  private void createLogDocument(MultipartFile file, String jsonArrayString) {
    LogDocument document = new LogDocument();
    document.setFilename(file.getOriginalFilename());
    document.setUploadedAt(LocalDateTime.now());
    document.setContent(jsonArrayString);

    repository.save(document);
  }
}
