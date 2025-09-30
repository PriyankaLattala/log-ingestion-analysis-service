package com.logs.transform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.logs.transform.model.LogEntry;
@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    
}
