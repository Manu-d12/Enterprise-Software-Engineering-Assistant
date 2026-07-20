package org.aiassistant.ai.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record BusinessRule(
        @JsonPropertyDescription("Unique identifier, e.g. BR-001")
        String id,
        @JsonPropertyDescription("The business rule statement")
        String rule,
        @JsonPropertyDescription("Why the rule exists")
        String rationale
) {}
