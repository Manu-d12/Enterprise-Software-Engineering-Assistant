package org.aiassistant.ai.controllers;

import lombok.AllArgsConstructor;
import org.aiassistant.ai.services.DocumentIngestionReaderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/rag")
public class RagChatController {

    private final DocumentIngestionReaderService documentIngestionReaderService;

    @PostMapping("/upload/{projectId}")
    public ResponseEntity<String> ingestDocs(
            @RequestParam MultipartFile[] files,
            @PathVariable String projectId
            ) {
        try {
            documentIngestionReaderService.ingestDocs(projectId, files);
            return ResponseEntity.ok("Success!!");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Failed!!");
        }
    }

    @GetMapping("/ask/{projectId}")
    public ResponseEntity<Map<String, String>> query(
            @PathVariable String projectId,
            @RequestParam String q
    ) {
        String result = documentIngestionReaderService.query(projectId, q);
        Map<String, String> response = new HashMap<>();
        response.put("content", result);
        response.put("projectId", projectId);
        return ResponseEntity.ok(response);
    }
}
