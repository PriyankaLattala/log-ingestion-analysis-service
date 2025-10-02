package com.logs.transform.datasource.repository;

import com.logs.transform.datasource.model.LogDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  LogDocumentRepository extends JpaRepository<LogDocument, Long> {

}
