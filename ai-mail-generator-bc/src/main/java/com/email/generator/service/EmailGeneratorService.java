package com.email.generator.service;

import com.email.generator.dto.EmailRequest;
import com.email.generator.dto.PromptHistoryResponse;

import java.util.List;

public interface EmailGeneratorService {
    String generateEmail(EmailRequest emailRequest);
    List<PromptHistoryResponse> getPromptHistoryResponse();
}
