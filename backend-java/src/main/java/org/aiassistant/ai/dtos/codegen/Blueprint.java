package org.aiassistant.ai.dtos.codegen;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.aiassistant.ai.enums.ProjectType;

import java.util.List;

@JsonClassDescription("Complete blueprint for the application to be generated: project type, dependencies, domain model, API contract, and the full file plan.")
public record Blueprint(
        @JsonPropertyDescription("Short project name in kebab-case, e.g. 'todo-app'")
        String projectName,

        @JsonPropertyDescription("One or two sentence summary of what the application does")
        String summary,

        @JsonPropertyDescription("Type of project: STATIC_WEB, FRONTEND_ONLY, or FULL_STACK")
        ProjectType projectType,

        @JsonPropertyDescription("Maven artifact IDs needed for the backend, e.g. 'spring-boot-starter-web'. Empty list if no backend.")
        List<String> backendDependencies,

        @JsonPropertyDescription("npm package names needed for the frontend, e.g. 'react', 'axios'. Empty list if no frontend build.")
        List<String> frontendDependencies,

        @JsonPropertyDescription("Domain entities and their data. Empty list for STATIC_WEB or FRONTEND_ONLY.")
        List<Entity> domainModel,

        @JsonPropertyDescription("REST API endpoints forming the frontend-backend contract. Empty list for STATIC_WEB or FRONTEND_ONLY.")
        List<Endpoint> apiContract,

        @JsonPropertyDescription("Every Backend file to generate, with its dependencies. Always present for Backend or Full-Stack Apps")
        List<PlannedFile> backendFiles,

        @JsonPropertyDescription("Every Frontend file to generate, with its dependencies. Always present for Frontend or Full-Stack Apps")
        List<PlannedFile> frontendFiles
) {}

