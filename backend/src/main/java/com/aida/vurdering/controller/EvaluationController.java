package com.aida.vurdering.controller;

import com.aida.vurdering.dto.EvaluationRequest;
import com.aida.vurdering.service.OpenAIService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
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

    @PostMapping(value = "/evaluations/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> evaluateFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Filen er tom"));
        }
        String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        if (!filename.endsWith(".pdf") && !filename.endsWith(".md")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Kun .pdf og .md filer er understøttet"));
        }
        try {
            String text = filename.endsWith(".pdf")
                    ? extractPdfText(file)
                    : new String(file.getBytes(), StandardCharsets.UTF_8);

            if (text.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Ingen tekst fundet i filen"));
            }
            Object result = openAIService.evaluate(text);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Fejl ved behandling af fil: " + e.getMessage()));
        }
    }

    private String extractPdfText(MultipartFile file) throws Exception {
        try (PDDocument doc = Loader.loadPDF(file.getBytes())) {
            return new PDFTextStripper().getText(doc);
        }
    }
}
