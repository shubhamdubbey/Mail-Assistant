package com.email.generator.repository;

import com.email.generator.entity.EmailPromptHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailPromptHistoryRepository
        extends JpaRepository<EmailPromptHistory, Long> {
}