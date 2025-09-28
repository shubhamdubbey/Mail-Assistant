package com.email.generator.service;

import com.email.generator.dto.EmailRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

public interface EmailGeneratorService {
    public String generateEmail(EmailRequest emailRequest);
}
