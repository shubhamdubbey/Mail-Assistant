package com.email.generator.controller;

import com.email.generator.dto.EmailRequest;
import com.email.generator.service.EmailGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")
public class EmailGeneratorController {

    @Autowired
    EmailGeneratorService emailGeneratorService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest){
        return ResponseEntity.ok().body(emailGeneratorService.generateEmail(emailRequest));
    }
}
