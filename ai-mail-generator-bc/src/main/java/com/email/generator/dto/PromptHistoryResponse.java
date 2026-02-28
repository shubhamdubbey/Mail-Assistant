package com.email.generator.dto;

import java.time.Instant;

public class PromptHistoryResponse{
    private Long id;
    private String prompt;
    private String response;
    private Instant createdAt;

    public PromptHistoryResponse(Long id, String prompt, String response, Instant createdAt) {
        this.id = id;
        this.prompt = prompt;
        this.response = response;
        this.createdAt = createdAt;
    }
}