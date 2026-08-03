package org.aiassistant.services;

import org.aiassistant.dtos.ProjectDTO;
import org.aiassistant.entities.Project;
import org.aiassistant.entities.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface ProjectService {
    Project create(ProjectDTO projectDTO, UserDetails userDetails);
    Project findById(String projectId);
    List<Project> getAll();
    List<Project> findByUser(User user);
    void save(Project project);
}
