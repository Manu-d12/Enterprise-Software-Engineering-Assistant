package org.aiassistant.controllers;


import lombok.AllArgsConstructor;
import org.aiassistant.dtos.DocumentDTO;
import org.aiassistant.entities.Document;
import org.aiassistant.services.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/documents")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/create/{projectId}")
    public ResponseEntity<List<DocumentDTO>> saveDocs(
            @PathVariable String projectId,
            @RequestParam MultipartFile[] files
    ) {
        List<DocumentDTO> documents = documentService.uploadDocument(projectId, files);
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
