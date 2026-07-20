package org.aiassistant.ai.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.util.List;

public record Actor(
        @JsonPropertyDescription("Name of the actor")
        String name,
        @JsonPropertyDescription("Type of actor")
        ActorType type,
        @JsonPropertyDescription("Key responsibilities of this actor")
        List<String> responsibilities
) {}
