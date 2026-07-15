package org.aiassistant.ai.controllers;

import lombok.AllArgsConstructor;
import org.aiassistant.ai.services.CodeGenService;
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

    @GetMapping
    public SseEmitter streamSseMvc(
            @RequestParam String q
    ) {
        return codeGenService.planAndGenCode(q);
    }
}
