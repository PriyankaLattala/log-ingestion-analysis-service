package com.logs.transform.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.logs.transform.service.LogIngestionService;

@RestController
@RequestMapping("/logs")
public class LogIngestionController {
    private final LogIngestionService logIngestionService;
    
    public LogIngestionController(LogIngestionService logIngestionService){
        this.logIngestionService = logIngestionService;
    }

    public ResponseEntity<String> uploadLogFile(@RequestParam("file") MultipartFile file){
        try{
            logIngestionService.parseAndSave(file.getInputStream());
            return ResponseEntity.ok("Log file ingested successfully.");
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Failed to process log file");
        }
    }
}
