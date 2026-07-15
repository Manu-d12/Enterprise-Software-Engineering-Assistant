package org.aiassistant.ai.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aiassistant.ai.dtos.codegen.Blueprint;
import org.springframework.ai.tool.annotation.Tool;

public class ContractTool {

    private final Blueprint blueprint;
    private final ObjectMapper objectMapper;

    public ContractTool(Blueprint blueprint) {
        this.blueprint = blueprint;
        this.objectMapper = new ObjectMapper();
    }

    @Tool(description = "Retrieve the API contract: the REST endpoints (method, path, "
            + "request shape, response shape) that the frontend and backend must "
            + "agree on. Call this when generating any file that calls or implements "
            + "an API endpoint — e.g. controllers, DTOs, frontend API clients, or "
            + "type definitions.")
    public String get_api_contract() throws JsonProcessingException {
        System.out.println("get_api_contract called");
        return this.objectMapper.writeValueAsString(blueprint.apiContract());
    }
}
