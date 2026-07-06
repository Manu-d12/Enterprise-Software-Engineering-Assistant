package org.aiassistant.ai.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.util.List;

public record Entity(
        @JsonPropertyDescription("Name of the domain entity")
        String name,
        @JsonPropertyDescription("Key attributes/fields of the entity")
        List<String> attributes,
        @JsonPropertyDescription("Relationships to other entities")
        List<String> relationships
) {}
