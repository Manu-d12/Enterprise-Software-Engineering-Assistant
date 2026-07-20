package org.aiassistant.ai.dtos.codegen;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

@JsonClassDescription("A domain entity: a business object with fields and relationships, typically mapped to a database table.")
public record Entity(
        @JsonPropertyDescription("Entity name in PascalCase, e.g. 'User'")
        String name,

        @JsonPropertyDescription("One line describing what this entity represents")
        String description,

        @JsonPropertyDescription("The fields (columns) of this entity")
        List<Field> fields,

        @JsonPropertyDescription("Relationships to other entities. Empty list if none.")
        List<Relationship> relationships
) {}
