package org.aiassistant.repositories;

import org.aiassistant.entities.Document;
import org.aiassistant.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepo extends JpaRepository<Document, Long> {
    List<Document> findByProject(Project project);
}
