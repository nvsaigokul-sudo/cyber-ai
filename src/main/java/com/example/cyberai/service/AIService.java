package com.example.cyberai.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@Service
public class AIService {

    private final String API_KEY = System.getenv("GROQ_API_KEY");

    // 🔹 LOG ANALYSIS (UPLOAD FILE)
    public String getExplanation(String attack, String risk) {

        // 🔥 DEBUG LINE (CHECK API KEY)
        System.out.println("API KEY = " + System.getenv("GROQ_API_KEY"));

        try {
            String url = "https://api.groq.com/openai/v1/chat/completions";

            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> request = new HashMap<>();
            request.put("model", "llama-3.3-70b-versatile");

            List<Map<String, String>> messages = new ArrayList<>();

            String prompt =
                    "You are a professional cybersecurity expert AI assistant.\n\n" +
                            "Analyze the system log results below:\n" +
                            "Attack: " + attack + "\n" +
                            "Risk Level: " + risk + "\n\n" +
                            "Respond like a chatbot in clear English.\n\n" +
                            "Your response must include:\n" +
                            "1. What happened\n" +
                            "2. Attack type\n" +
                            "3. Risk level explanation\n" +
                            "4. Prevention steps\n" +
                            "5. Safety advice\n\n" +
                            "Keep it friendly and conversational.";

            Map<String, String> msg = new HashMap<>();
            msg.put("role", "user");
            msg.put("content", prompt);

            messages.add(msg);
            request.put("messages", messages);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + API_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(request, headers);

            Map response = restTemplate.postForObject(url, entity, Map.class);

            return extractText(response);

        } catch (Exception e) {
            e.printStackTrace();
            return "AI error";
        }
    }

    // 🔹 CHAT FUNCTION (USER CHAT)
    public String chatWithContext(String messageText) {

        // 🔥 DEBUG LINE (CHECK API KEY)
        System.out.println("API KEY = " + System.getenv("GROQ_API_KEY"));

        try {
            String url = "https://api.groq.com/openai/v1/chat/completions";

            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> request = new HashMap<>();
            request.put("model", "llama-3.3-70b-versatile");

            List<Map<String, String>> messages = new ArrayList<>();

            String prompt =
                    "You are a cybersecurity AI chatbot.\n" +
                            "Answer in simple English.\n" +
                            "Give helpful security advice.\n\n" +
                            "User: " + messageText;

            Map<String, String> msg = new HashMap<>();
            msg.put("role", "user");
            msg.put("content", prompt);

            messages.add(msg);
            request.put("messages", messages);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + API_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(request, headers);

            Map response = restTemplate.postForObject(url, entity, Map.class);

            return extractText(response);

        } catch (Exception e) {
            e.printStackTrace();
            return "Chat error";
        }
    }

    // 🔥 EXTRACT RESPONSE TEXT
    private String extractText(Map response) {
        try {
            List choices = (List) response.get("choices");

            if (choices != null && !choices.isEmpty()) {
                Map first = (Map) choices.get(0);
                Map message = (Map) first.get("message");
                return message.get("content").toString();
            }
        } catch (Exception e) {
            return response.toString();
        }

        return "No AI response";
    }
}