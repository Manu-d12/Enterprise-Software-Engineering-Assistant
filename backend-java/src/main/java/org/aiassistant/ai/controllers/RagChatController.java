package org.aiassistant.ai.controllers;

import lombok.AllArgsConstructor;
import org.aiassistant.ai.services.DocumentIngestionReaderService;
import org.aiassistant.entities.User;
import org.aiassistant.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/rag")
public class RagChatController {

    private final DocumentIngestionReaderService documentIngestionReaderService;
    private final UserService userService;

    @GetMapping("/ask/{projectId}")
    public ResponseEntity<Map<String, String>> query(
            @PathVariable String projectId,
            @RequestParam String q,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.findUserByUsername(userDetails.getUsername());
        String result = documentIngestionReaderService.query(projectId, q, user.getId().toString());
        Map<String, String> response = new HashMap<>();
        response.put("content", result);
        response.put("projectId", projectId);
        return ResponseEntity.ok(response);
    }
}
