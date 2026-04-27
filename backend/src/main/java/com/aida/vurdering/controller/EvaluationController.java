package com.aida.vurdering.controller;

import com.aida.vurdering.dto.EvaluationRequest;
import com.aida.vurdering.service.OpenAIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class EvaluationController {

    private final OpenAIService openAIService;

    public EvaluationController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @PostMapping("/evaluations")
    public ResponseEntity<?> evaluate(@RequestBody EvaluationRequest request) {
        if (request.getText() == null || request.getText().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Opgavetekst må ikke være tom"));
        }
        try {
            Object result = openAIService.evaluate(request.getText());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Fejl ved kald til AI: " + e.getMessage()));
        }
    }
}
