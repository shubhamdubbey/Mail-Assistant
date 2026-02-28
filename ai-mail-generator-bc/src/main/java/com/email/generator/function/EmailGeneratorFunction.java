package com.email.generator.function;

import com.email.generator.dto.EmailRequest;
import com.email.generator.dto.PromptHistoryResponse;
import com.email.generator.service.EmailGeneratorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Configuration
public class EmailGeneratorFunction {

    @Bean
    public Function<EmailRequest, String> generateEmail(
            EmailGeneratorService emailGeneratorService) {

        return emailGeneratorService::generateEmail;
    }

    @Bean
    public Supplier<List<PromptHistoryResponse>> listAllPrompts(
            EmailGeneratorService emailGeneratorService) {

        return emailGeneratorService::getPromptHistoryResponse;
    }
}
