package org.aiassistant.ai.dtos.codegen;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

@JsonClassDescription("A single REST endpoint: its HTTP method, path, and request/response shapes. This is the contract both frontend and backend must honor.")
public record Endpoint(
        @JsonPropertyDescription("HTTP method: GET, POST, PUT, DELETE, or PATCH")
        String method,

        @JsonPropertyDescription("URL path with path variables in braces, e.g. '/api/users/{id}'")
        String path,

        @JsonPropertyDescription("One line describing what this endpoint does")
        String description,

        @JsonPropertyDescription("Fields in the request body. Empty list if none, e.g. for GET.")
        List<Field> requestFields,

        @JsonPropertyDescription("Fields in the response body. Empty list if none, e.g. for 204 No Content.")
        List<Field> responseFields,

        @JsonPropertyDescription("True if the response is a list/array of the response fields rather than a single object")
        boolean returnsList
) {}