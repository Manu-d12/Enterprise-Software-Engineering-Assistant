package org.aiassistant.ai.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record EdgeCase(
        @JsonPropertyDescription("The edge-case scenario")
        String scenario,
        @JsonPropertyDescription("How the system should handle it")
        String expectedHandling
) {}
