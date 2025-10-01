package com.logs.transform.exception.model;
import java.time.LocalDateTime;
public class ErrorResponse {
  private final int status;
  private final String message;
  private final LocalDateTime timestamp;

  public ErrorResponse(int status, String message, LocalDateTime timestamp) {
    this.status = status;
    this.message = message;
    this.timestamp = timestamp;
  }

}