package org.aiassistant.ai.dtos.codegen;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@JsonClassDescription("A relationship between two entities, e.g. one-to-many.")
public record Relationship(
        @JsonPropertyDescription("Relationship type: ONE_TO_MANY, MANY_TO_ONE, ONE_TO_ONE, or MANY_TO_MANY")
        String type,

        @JsonPropertyDescription("Name of the related entity in PascalCase, e.g. 'Order'")
        String targetEntity,

        @JsonPropertyDescription("Field name holding the relationship, e.g. 'orders'")
        String fieldName
) {}