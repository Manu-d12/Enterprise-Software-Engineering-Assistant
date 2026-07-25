package org.aiassistant.ai.controllers;

import lombok.AllArgsConstructor;
import org.aiassistant.ai.agents.PlanningAgent;
import org.aiassistant.ai.dtos.RequirementsAnalysisDTO;
import org.aiassistant.ai.dtos.codegen.Blueprint;
import org.aiassistant.ai.services.RequirementAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/requirement-analysis")
public class RequirementAnalysisController {

    private final PlanningAgent planningAgent;
    private final RequirementAnalysisService requirementAnalysisService;

    @GetMapping
    public ResponseEntity<RequirementsAnalysisDTO> getProjectRequirements(
            @RequestParam String q
    ) {
        return ResponseEntity.ok(requirementAnalysisService.getRequirements(q));
    }

    @GetMapping("/blueprint")
    public ResponseEntity<Blueprint> blueprint(
            @RequestParam String projectId
    ) {
        final Blueprint plan = planningAgent.plan(projectId);
        return ResponseEntity.ok(plan);
    }
}
