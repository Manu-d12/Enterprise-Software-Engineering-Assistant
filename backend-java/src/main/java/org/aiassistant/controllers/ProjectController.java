package org.aiassistant.controllers;

import lombok.AllArgsConstructor;
import org.aiassistant.dtos.ProjectDTO;
import org.aiassistant.entities.Project;
import org.aiassistant.services.ProjectService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ModelMapper modelMapper;
    private final ProjectService projectService;

    @PostMapping("/save")
    public ResponseEntity<ProjectDTO> create (
            @RequestBody ProjectDTO projectDTO
    ) {
        Project savedProject = projectService.create(projectDTO);
        ProjectDTO savedDTO = this.modelMapper.map(savedProject, ProjectDTO.class);

        return new ResponseEntity<>(savedDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAll() {
        List<Project> projects = projectService.getAll();
        return ResponseEntity.ok(projects.stream().map(project -> modelMapper.map(project, ProjectDTO.class)).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> findById(
            @PathVariable String id
    ) {
        Project project = projectService.findById(id);
        return ResponseEntity.ok(modelMapper.map(project, ProjectDTO.class));
    }
}
