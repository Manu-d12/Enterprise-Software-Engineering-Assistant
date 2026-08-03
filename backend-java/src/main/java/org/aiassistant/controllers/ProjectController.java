package org.aiassistant.controllers;

import lombok.AllArgsConstructor;
import org.aiassistant.dtos.ProjectDTO;
import org.aiassistant.entities.Project;
import org.aiassistant.entities.User;
import org.aiassistant.services.ProjectService;
import org.aiassistant.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ModelMapper modelMapper;
    private final ProjectService projectService;
    private final UserService userService;

    @PostMapping("/save")
    public ResponseEntity<ProjectDTO> create (
            @RequestBody ProjectDTO projectDTO,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Project savedProject = projectService.create(projectDTO, userDetails);
        ProjectDTO savedDTO = this.modelMapper.map(savedProject, ProjectDTO.class);

        return new ResponseEntity<>(savedDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAll() {
        List<Project> projects = projectService.getAll();
        return ResponseEntity.ok(projects.stream().map(project -> modelMapper.map(project, ProjectDTO.class)).toList());
    }

    @GetMapping("/user")
    public  ResponseEntity<List<ProjectDTO>>findByUser(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.findUserByUsername(userDetails.getUsername());
        List<ProjectDTO> projects = projectService.findByUser(user).stream().map(project -> modelMapper.map(project, ProjectDTO.class)).toList();
        return ResponseEntity.ok(projects);
    }


    @GetMapping("/{id}")
    public  ResponseEntity<ProjectDTO> findById(
            @PathVariable String id
    ) {
        final Project byId = projectService.findById(id);
        return ResponseEntity.ok(modelMapper.map(byId, ProjectDTO.class));
    }
}
