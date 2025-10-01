package com.logs.transform.dto;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

public record LogDocumentDto(Long id, List<JsonNode> logs) {

}
