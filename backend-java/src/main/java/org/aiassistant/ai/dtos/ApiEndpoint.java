package org.aiassistant.ai.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record ApiEndpoint(
        @JsonPropertyDescription("HTTP method, e.g. GET, POST")
        String method,
        @JsonPropertyDescription("Endpoint path, e.g. /api/games/{gameId}/roll")
        String path,
        @JsonPropertyDescription("What the endpoint does")
        String description,
        @JsonPropertyDescription("Summary of the request payload/params")
        String requestSummary,
        @JsonPropertyDescription("Summary of the response payload")
        String responseSummary
) {}
