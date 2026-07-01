package org.aiassistant.repositories;

import org.aiassistant.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepo extends JpaRepository<Document, Long> {
}
