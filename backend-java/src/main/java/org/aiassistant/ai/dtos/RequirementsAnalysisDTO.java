package org.aiassistant.ai.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.util.List;

public record RequirementsAnalysisDTO(

        @JsonPropertyDescription("Concise name of the project being analyzed")
        String projectName,

        @JsonPropertyDescription("2-3 sentence high-level summary of the system")
        String summary,

        @JsonPropertyDescription("List of functional requirements; what the system must do")
        List<FunctionalRequirement> functionalRequirements,

        @JsonPropertyDescription("List of non-functional requirements; quality attributes and constraints")
        List<NonFunctionalRequirement> nonFunctionalRequirements,

        @JsonPropertyDescription("Actors that interact with the system")
        List<Actor> actors,

        @JsonPropertyDescription("Domain entities and their relationships")
        List<Entity> entities,

        @JsonPropertyDescription("REST API endpoints the system should expose")
        List<ApiEndpoint> apiEndpoints,

        @JsonPropertyDescription("Edge cases and how the system should handle them")
        List<EdgeCase> edgeCases,

        @JsonPropertyDescription("Business rules governing the system")
        List<BusinessRule> businessRules

) {}
