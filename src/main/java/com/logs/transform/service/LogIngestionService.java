package com.logs.transform.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.logs.transform.model.LogEntry;
import com.logs.transform.repository.LogEntryRepository;
@Service
public class LogIngestionService {

    private LogEntryRepository logEntryRepository;
    String regex = "^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})\\s+(INFO|ERROR|WARN|DEBUG|TRACE)\\s+(.*)$";
        private final Pattern loPattern = Pattern.compile(regex);
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LogIngestionService(LogEntryRepository logEntryRepository){
        this.logEntryRepository = logEntryRepository;
    }

    public void parseAndSave(InputStream inputStream) {
       try{
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        reader.lines().forEach(line->{
            Matcher matcher = loPattern.matcher(line);
            if(matcher.matches()){
                LocalDateTime time = LocalDateTime.parse(matcher.group(1), formatter);
                String level = matcher.group(2);
                String message = matcher.group(3);
                LogEntry entry = new LogEntry(time, level, message);
                logEntryRepository.save(entry);
            }
        });
       } catch(Exception e){
        throw new RuntimeException("Error parsing log file");
       }
        
    }

}
