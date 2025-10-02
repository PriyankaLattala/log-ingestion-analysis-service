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

  private final LogDocumentRepository repository;
  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Saves the uploaded log file as a LogDocument. Each line in the file is expected to be a JSON object.
   *
   * @param file the uploaded log file
   * @throws FileParseException if file is empty or contains invalid JSON
   */
  public void uploadLogFile(MultipartFile file) {
    List<JsonNode> jsonLogs = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(
      new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

      reader.lines()
            .filter(line -> !line.trim().isEmpty())
            .forEach(line -> {
              try {
                jsonLogs.add(objectMapper.readTree(line));
              } catch (JsonProcessingException e) {
                throw new FileParseException("Invalid JSON line: " + line, e);
              }
            });

    } catch (IOException e) {
      throw new FileParseException("Failed to read uploaded file: " + e.getMessage(), e);
    }

    if (jsonLogs.isEmpty()) {
      throw new FileParseException("File contains no valid log entries.");
    }

    try {
      String jsonArrayString = objectMapper.writeValueAsString(jsonLogs);
      createLogDocument(file, jsonArrayString);
    } catch (JsonProcessingException e) {
      throw new FileParseException("Failed to serialize log entries to JSON array.", e);
    }
  }

  private void createLogDocument(MultipartFile file, String jsonArrayString) {
    LogDocument document = LogDocument.builder().
                                      content(jsonArrayString).filename(file.getOriginalFilename())
                                      .uploadedAt(LocalDateTime.now()).build();

    repository.save(document);
  }
}
