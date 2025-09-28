package com.email.generator.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequest {
    private String emailContent;
    private String tone;
}
