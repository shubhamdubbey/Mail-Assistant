package com.email.generator.service;

import com.email.generator.dto.EmailRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EmailGeneratorServiceImpl implements EmailGeneratorService {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient;

    public EmailGeneratorServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public String generateEmail(EmailRequest emailRequest) {
        // Build the prompt
        String prompt = buildPrompt(emailRequest);

        // Craft request
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[] {
                        Map.of("parts", new Object[] {
                                Map.of("text", prompt)
                        })
                }
        );

        // Send request
        String response = webClient.post()
                .uri(geminiApiUrl + geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Return response
        return extractResponseContent(response);
    }

    private String extractResponseContent(String response) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            return rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

        } catch(Exception e){
            return "Error processing response" + e.getMessage();
        }
    }

    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a professional email reply for the following email content. Don't generate th subject line.");
        if(emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
            prompt.append(" Use ").append(emailRequest.getTone()).append(" tone.");
        }
        prompt.append("\nOriginal Email : \n").append(emailRequest.getEmailContent());

        return prompt.toString();
    }
}
