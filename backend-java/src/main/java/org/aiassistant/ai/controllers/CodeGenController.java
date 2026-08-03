package org.aiassistant.ai.controllers;

import lombok.AllArgsConstructor;
import org.aiassistant.ai.services.CodeGenService;
import org.aiassistant.entities.User;
import org.aiassistant.services.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@AllArgsConstructor
@RestController
@RequestMapping("/generate-code")
public class CodeGenController {

    private final CodeGenService codeGenService;
    private final UserService userService;

    @GetMapping
    public SseEmitter streamSseMvc(
            @RequestParam String projectId,
            @AuthenticationPrincipal UserDetails userDetails
            ) {
        User user = userService.findUserByUsername(userDetails.getUsername());
        return codeGenService.planAndGenCode(projectId, user.getId().toString());
    }
}
