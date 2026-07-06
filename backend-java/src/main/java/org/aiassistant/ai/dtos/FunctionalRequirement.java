package org.aiassistant.ai.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record FunctionalRequirement(
        @JsonPropertyDescription("Unique identifier, e.g. FR-001")
        String id,
        @JsonPropertyDescription("Short title of the requirement")
        String title,
        @JsonPropertyDescription("Clear description of what the system must do")
        String description,
        @JsonPropertyDescription("Priority of the requirement")
        Priority priority
) {}
