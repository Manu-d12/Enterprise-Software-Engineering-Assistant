package org.aiassistant.services.impl;

import lombok.AllArgsConstructor;
import org.aiassistant.dtos.ProjectDTO;
import org.aiassistant.entities.Project;
import org.aiassistant.repositories.ProjectRepo;
import org.aiassistant.services.ProjectService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepo projectRepo;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public Project create(ProjectDTO projectDTO) {
        Project toSave = modelMapper.map(projectDTO, Project.class);
        return projectRepo.save(toSave);
    }

    @Transactional(readOnly = true)
    @Override
    public Project findById(String projectId) {
        return this.projectRepo.findById(Long.parseLong(projectId)).get();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Project> getAll() {
        return projectRepo.findAll();
    }
}
