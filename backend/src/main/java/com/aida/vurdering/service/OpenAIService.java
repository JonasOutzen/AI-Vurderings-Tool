package com.aida.vurdering.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/responses";
    private static final String MODEL = "gpt-4.1-nano";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenAIService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10_000);
        factory.setReadTimeout(60_000);
        this.restTemplate = new RestTemplate(factory);
    }

    public Object evaluate(String reportText) throws Exception {
        String systemPrompt = loadResource("prompts/systemprompt.txt");
        String userPromptTemplate = loadResource("prompts/userprompt-template.txt");
        String userPrompt = userPromptTemplate.replace("{{REPORT_TEXT}}", reportText);

        Map<String, Object> requestBody = buildRequest(systemPrompt, userPrompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    OPENAI_URL, HttpMethod.POST, entity, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            String jsonText = root.path("output").get(0)
                    .path("content").get(0)
                    .path("text").asText();

            return objectMapper.readValue(jsonText, Object.class);

        } catch (HttpClientErrorException e) {
            throw new RuntimeException("OpenAI API klientfejl (" + e.getStatusCode() + "): " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("OpenAI API serverfejl (" + e.getStatusCode() + ")");
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Timeout ved kald til OpenAI API");
        }
    }

    private Map<String, Object> buildRequest(String systemPrompt, String userPrompt) {
        Map<String, Object> systemMessage = new LinkedHashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);

        Map<String, Object> userMessage = new LinkedHashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userPrompt);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", MODEL);
        body.put("input", List.of(systemMessage, userMessage));
        body.put("text", Map.of("format", buildJsonSchemaFormat()));

        return body;
    }

    private Map<String, Object> buildJsonSchemaFormat() {
        Map<String, Object> stringType = Map.of("type", "string");
        Map<String, Object> stringArray = Map.of("type", "array", "items", stringType);

        Map<String, Object> criterionItemProps = new LinkedHashMap<>();
        criterionItemProps.put("criterion", stringType);
        criterionItemProps.put("level", stringType);
        criterionItemProps.put("feedback", stringType);

        Map<String, Object> criterionItem = new LinkedHashMap<>();
        criterionItem.put("type", "object");
        criterionItem.put("properties", criterionItemProps);
        criterionItem.put("required", List.of("criterion", "level", "feedback"));
        criterionItem.put("additionalProperties", false);

        Map<String, Object> criteriaFeedbackType = new LinkedHashMap<>();
        criteriaFeedbackType.put("type", "array");
        criteriaFeedbackType.put("items", criterionItem);

        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("overallAssessment", stringType);
        properties.put("criteriaFeedback", criteriaFeedbackType);
        properties.put("strengths", stringArray);
        properties.put("weaknesses", stringArray);
        properties.put("improvements", stringArray);
        properties.put("questions", stringArray);

        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", properties);
        schema.put("required", List.of("overallAssessment", "criteriaFeedback", "strengths", "weaknesses", "improvements", "questions"));
        schema.put("additionalProperties", false);

        Map<String, Object> format = new LinkedHashMap<>();
        format.put("type", "json_schema");
        format.put("name", "evaluation");
        format.put("strict", true);
        format.put("schema", schema);

        return format;
    }

    private String loadResource(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}
