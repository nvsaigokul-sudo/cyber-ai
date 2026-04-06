package com.example.cyberai.controller;

import com.example.cyberai.dto.AnalysisResponse;
import com.example.cyberai.service.LogService;
import com.example.cyberai.service.AIService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class LogController {

    @Autowired
    private LogService logService;

    @Autowired
    private AIService aiService;

    @PostMapping("/upload")
    public AnalysisResponse uploadLog(@RequestParam("file") MultipartFile file) {
        return logService.processLog(file);
    }

    // 🔥 SUPER SAFE VERSION (NO JSON, NO MAP)
    @PostMapping("/chat")
    public String chat(@RequestBody(required = false) String message) {
        if (message == null || message.trim().isEmpty()) {
            return "Please enter a message.";
        }
        return aiService.chatWithContext(message);
    }
}