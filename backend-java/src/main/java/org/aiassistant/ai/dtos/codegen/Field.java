package org.aiassistant.ai.dtos.codegen;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@JsonClassDescription("A single field: a named, typed piece of data.")
public record Field(
        @JsonPropertyDescription("Field name in camelCase, e.g. 'email'")
        String name,

        @JsonPropertyDescription("Java type name, e.g. 'String', 'Long', 'Instant', 'BigDecimal'")
        String type,

        @JsonPropertyDescription("Whether this field is required (non-null)")
        boolean required
) {}
