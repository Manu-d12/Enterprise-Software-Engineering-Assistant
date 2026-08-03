package org.aiassistant.controllers;


import lombok.AllArgsConstructor;
import org.aiassistant.dtos.DocumentDTO;
import org.aiassistant.entities.Document;
import org.aiassistant.entities.User;
import org.aiassistant.services.DocumentService;
import org.aiassistant.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final UserService userService;

    @PostMapping("/create/{projectId}")
    public ResponseEntity<List<DocumentDTO>> saveDocs(
            @PathVariable String projectId,
            @RequestParam MultipartFile[] files,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.findUserByUsername(userDetails.getUsername());
        List<DocumentDTO> documents = documentService.uploadDocument(projectId, files, user.getId().toString());
        return new ResponseEntity<>(documents, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DocumentDTO>> getProjectDocs(
            @RequestParam String projectId
    ) {
        List<DocumentDTO> docsByProject = documentService.getDocsByProject(projectId);
        return ResponseEntity.ok(docsByProject);
    }

}
