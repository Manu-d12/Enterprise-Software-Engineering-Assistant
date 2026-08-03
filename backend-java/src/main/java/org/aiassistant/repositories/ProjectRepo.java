package org.aiassistant.repositories;

import org.aiassistant.entities.Project;
import org.aiassistant.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepo extends JpaRepository<Project, Long> {
    Optional<List<Project>> findByUser(User user);
}
