package org.aiassistant.ai.enums;

import com.fasterxml.jackson.annotation.JsonClassDescription;

@JsonClassDescription("The kind of application being generated, which determines which phases run.")
public enum ProjectType {
    STATIC_WEB,        // plain HTML/CSS/JS
    FRONTEND_ONLY,     // React SPA, no backend
    FULL_STACK         // React + Spring Boot
}
