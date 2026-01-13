package ar.com.smg.document_service.repository;

import ar.com.smg.document_service.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> { }
