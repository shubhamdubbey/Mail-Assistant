package com.email.generator.service;

import com.email.generator.dto.EmailRequest;

public interface EmailGeneratorService {
    String generateEmail(EmailRequest emailRequest);
}
