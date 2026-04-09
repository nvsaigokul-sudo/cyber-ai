package com.example.cyberai.controller;

import com.example.cyberai.model.ChatRequest;
import com.example.cyberai.model.ChatResponse;
import com.example.cyberai.service.GroqAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private GroqAiService groqAiService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        ChatResponse response = groqAiService.getChatResponse(
                request.getMessage(),
                request.getContext()
        );
        return ResponseEntity.ok(response);
    }
}