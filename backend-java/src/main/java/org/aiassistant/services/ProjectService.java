package org.aiassistant.services;

import org.aiassistant.dtos.ProjectDTO;
import org.aiassistant.entities.Project;

import java.util.List;

public interface ProjectService {
    Project create(ProjectDTO projectDTO);
    Project findById(String projectId);
    List<Project> getAll();
}
