package org.aiassistant.services.impl;

import lombok.AllArgsConstructor;
import org.aiassistant.dtos.ProjectDTO;
import org.aiassistant.entities.Project;
import org.aiassistant.entities.User;
import org.aiassistant.repositories.ProjectRepo;
import org.aiassistant.services.ProjectService;
import org.aiassistant.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepo projectRepo;
    private final ModelMapper modelMapper;
    private final UserService userService;

    @Transactional
    @Override
    public Project create(ProjectDTO projectDTO, UserDetails userDetails) {
        Project toSave = modelMapper.map(projectDTO, Project.class);
        final User user = userService.findUserByUsername(userDetails.getUsername());
        toSave.setUser(user);
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

    @Override
    public List<Project> findByUser(User user) {
        return projectRepo.findByUser(user).get();
    }

    @Override
    public void save(Project project) {
        projectRepo.save(project);
    }

    @Override
    public void setS3Path(String projectId, String path) {
        Project project = projectRepo.findById(Long.valueOf(projectId)).get();
        project.setS3Path(path);
        projectRepo.save(project);
    }
}
