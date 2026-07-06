package org.aiassistant.ai.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record NonFunctionalRequirement(
        @JsonPropertyDescription("Unique identifier, e.g. NFR-001")
        String id,
        @JsonPropertyDescription("Quality attribute category")
        NfrCategory category,
        @JsonPropertyDescription("Description of the quality constraint")
        String description,
        @JsonPropertyDescription("A measurable target, e.g. '< 500ms latency'")
        String metric
) {}
