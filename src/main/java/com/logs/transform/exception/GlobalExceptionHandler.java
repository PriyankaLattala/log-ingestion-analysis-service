package com.logs.transform.exception;

import com.logs.transform.exception.model.BadRequestException;
import com.logs.transform.exception.model.EntityNotFoundException;
import com.logs.transform.exception.model.ErrorResponse;
import com.logs.transform.exception.model.FileParseException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(FileParseException.class)
  public ResponseEntity<ErrorResponse> handleFileParseException(FileParseException ex) {
    return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ErrorResponse> handleFileTooLarge(MaxUploadSizeExceededException ex) {
    return buildResponse(HttpStatus.PAYLOAD_TOO_LARGE, "Uploaded file is too large. Please upload a smaller file.");
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleOtherExceptions(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong.");
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
    return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
    return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
    return new ResponseEntity<>(new ErrorResponse(status.value(), message, LocalDateTime.now()), status);
  }
}
