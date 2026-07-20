package org.aiassistant.ai.dtos.codegen;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

@JsonClassDescription("A single file to generate, including where it lives and which other files it depends on. Dependencies drive generation order via topological sort.")
public record PlannedFile(
        @JsonPropertyDescription("Full file path from project root, e.g. 'src/main/java/com/app/config/SecurityConfig.java'")
        String path,

        @JsonPropertyDescription("Java package name, e.g. 'com.app.config'. Empty string for non-Java files.")
        String packageName,

        @JsonPropertyDescription("File type: class, interface, record, enum, config, html, css, js, ts, or tsx")
        String fileType,

        @JsonPropertyDescription("One line describing the purpose of this file")
        String purpose,

        @JsonPropertyDescription("Exact 'path' values of other planned files this file depends on. Dependencies are generated first. Empty list if none.")
        List<String> dependsOn,

        @JsonPropertyDescription(
                "Generation order within this file's list. Lower ranks are generated first. " +
                        "Assign ranks so that a file's dependencies come before it: " +
                        "build/config files first, then data models/entities, then data access, " +
                        "then business logic, then the API/controller layer. For frontend: config first, " +
                        "then types, then API client, then components, then app wiring. " +
                        "Ranks start at 1 and increase. Files that can be generated at the same stage " +
                        "may share the same rank."
        )
        Integer rank
) {}
