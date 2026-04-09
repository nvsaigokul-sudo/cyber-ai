package com.example.cyberai.service;

import com.example.cyberai.model.ChatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GroqAiService {

    private final ObjectMapper objectMapper;

    @Value("${groq.api.key:}")
    private String apiKey;

    @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String apiUrl;

    public GroqAiService() {
        this.objectMapper = new ObjectMapper();
    }

    public ChatResponse getChatResponse(String userMessage, String context) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your_groq_api_key_here")) {
            return getFallbackResponse(userMessage, context);
        }

        try {
            String systemPrompt = buildSystemPrompt(context);
            String response = callGroqApi(systemPrompt, userMessage);
            return parseGroqResponse(response, userMessage);
        } catch (Exception e) {
            System.err.println("Groq API error: " + e.getMessage());
            return getFallbackResponse(userMessage, context);
        }
    }

    private String buildSystemPrompt(String context) {
        return "You are Cyber-AI Sentinel, an expert cybersecurity analyst. " +
                "Provide concise, actionable security advice. " +
                "Include command-line solutions when relevant. " +
                "Current context: " + context;
    }

    private String callGroqApi(String systemPrompt, String userMessage) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        Map<String, Object> requestBody = Map.of(
                "model", "mixtral-8x7b-32768",
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userMessage)
                ),
                "temperature", 0.7,
                "max_tokens", 1000
        );

        String jsonInput = objectMapper.writeValueAsString(requestBody);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonInput.getBytes());
            os.flush();
        }

        int responseCode = conn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
            return response.toString();
        } else {
            throw new RuntimeException("API call failed with code: " + responseCode);
        }
    }

    private ChatResponse parseGroqResponse(String response, String originalMessage) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            String reply = jsonNode.path("choices").path(0).path("message").path("content").asText();
            ChatResponse chatResponse = new ChatResponse(reply, true);

            if (containsRemediationKeywords(originalMessage)) {
                chatResponse.setRemediation(getSmartRemediation(originalMessage));
            }
            return chatResponse;
        } catch (Exception e) {
            return getFallbackResponse(originalMessage, "");
        }
    }

    private ChatResponse getFallbackResponse(String message, String context) {
        String reply = generateSmartFallback(message, context);
        ChatResponse response = new ChatResponse(reply, true);

        if (containsRemediationKeywords(message)) {
            response.setRemediation(getSmartRemediation(message));
        }
        return response;
    }

    private String generateSmartFallback(String message, String context) {
        String lowerMsg = message.toLowerCase();

        if (lowerMsg.contains("threat") || lowerMsg.contains("risk")) {
            return "🛡️ Based on analysis: " + context +
                    "\n\nRecommended actions:\n" +
                    "1. Isolate affected systems immediately\n" +
                    "2. Run full antivirus scan\n" +
                    "3. Review access logs\n" +
                    "4. Update security patches\n" +
                    "5. Enable enhanced monitoring";
        }

        if (lowerMsg.contains("protect") || lowerMsg.contains("secure")) {
            return "🔒 Protection Best Practices:\n\n" +
                    "• Enable firewall: `sudo ufw enable` (Linux)\n" +
                    "• Update system: `sudo apt update && sudo apt upgrade`\n" +
                    "• Install antivirus: `sudo apt install clamav`\n" +
                    "• Backup data: `rsync -av /source /backup`\n" +
                    "• Monitor logs: `journalctl -f`";
        }

        if (lowerMsg.contains("fix") || lowerMsg.contains("remediate")) {
            return "🔧 Remediation Steps:\n\n" +
                    "1. Kill suspicious processes: `sudo kill -9 [PID]`\n" +
                    "2. Remove malware: `sudo clamscan --remove --recursive /`\n" +
                    "3. Restore from backup\n" +
                    "4. Reset compromised credentials\n" +
                    "5. Apply security patches";
        }

        return "I'm your AI security analyst. You can ask me about:\n" +
                "• Threat analysis results\n" +
                "• Protection measures\n" +
                "• Remediation commands\n" +
                "• Security best practices\n\n" +
                "What specific help do you need?";
    }

    private boolean containsRemediationKeywords(String message) {
        String lower = message.toLowerCase();
        return lower.contains("fix") || lower.contains("remediate") ||
                lower.contains("remove") || lower.contains("clean") ||
                lower.contains("solution") || lower.contains("how to");
    }

    private List<ChatResponse.RemediationStep> getSmartRemediation(String message) {
        List<ChatResponse.RemediationStep> steps = new ArrayList<>();
        String lowerMsg = message.toLowerCase();

        if (lowerMsg.contains("malware") || lowerMsg.contains("virus")) {
            steps.add(new ChatResponse.RemediationStep(
                    "Scan and remove malware (Linux)",
                    "sudo clamscan --remove --recursive /home",
                    "Linux"
            ));
            steps.add(new ChatResponse.RemediationStep(
                    "Windows Defender Scan",
                    "Start-MpScan -ScanType FullScan",
                    "Windows PowerShell"
            ));
        }

        if (lowerMsg.contains("unauthorized") || lowerMsg.contains("breach")) {
            steps.add(new ChatResponse.RemediationStep(
                    "Check active connections",
                    "netstat -tunap | grep ESTABLISHED",
                    "Linux"
            ));
        }

        // Generic protection steps
        if (steps.isEmpty()) {
            steps.add(new ChatResponse.RemediationStep(
                    "Update system packages",
                    "sudo apt update && sudo apt upgrade -y",
                    "Linux (Debian/Ubuntu)"
            ));
            steps.add(new ChatResponse.RemediationStep(
                    "Enable firewall",
                    "sudo ufw enable",
                    "Linux"
            ));
            steps.add(new ChatResponse.RemediationStep(
                    "Check system logs",
                    "journalctl -xe | tail -50",
                    "Linux"
            ));
        }

        return steps;
    }
}