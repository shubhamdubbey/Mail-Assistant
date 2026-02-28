package com.email.generator.service;

import com.email.generator.dto.EmailRequest;
import com.email.generator.dto.PromptHistoryResponse;
import com.email.generator.entity.EmailPromptHistory;
import com.email.generator.repository.EmailPromptHistoryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmailGeneratorServiceImpl implements EmailGeneratorService {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Autowired
    private EmailPromptHistoryRepository repository;

    private static final Logger logger =
            LoggerFactory.getLogger(EmailGeneratorServiceImpl.class);

    private final WebClient webClient;

    public EmailGeneratorServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public String generateEmail(EmailRequest emailRequest) {

        String prompt = buildPrompt(emailRequest);
        logger.info("Here is the prompt in line 41: " + prompt);

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt)
                        })
                }
        );

        logger.info("Here is the requestBody for gemini in line 51: " + requestBody);

        String response = webClient.post()
                .uri(geminiApiUrl + geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        logger.info("Here is the response from gemini in line 61: " + response);

        String extractedResponse = extractResponseContent(response);

        logger.info("Here is the extracted response in line 65: " + extractedResponse);

        repository.save(
                new EmailPromptHistory(prompt, extractedResponse, Instant.now())
        );
        logger.info("After saving to repository. " + prompt);
        return extractedResponse;
    }

    @Override
    public List<PromptHistoryResponse> getPromptHistoryResponse() {
        List<EmailPromptHistory> list = repository.findAll();
        return list.stream().map(x -> new PromptHistoryResponse(x.getId(), x.getPrompt(), x.getResponse(), x.getCreatedAt())).collect(Collectors.toList());
    }

    private String extractResponseContent(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            return rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        } catch (Exception e) {
            return "Error processing response: " + e.getMessage();
        }
    }

    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a professional email reply for the following email content. Don't generate the subject line.");
        if (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
            prompt.append(" Use ").append(emailRequest.getTone()).append(" tone.");
        }
        prompt.append("\nOriginal Email:\n").append(emailRequest.getEmailContent());
        return prompt.toString();
    }
}
