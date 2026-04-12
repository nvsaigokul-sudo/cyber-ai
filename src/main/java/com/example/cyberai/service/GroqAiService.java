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

    private static final String CURRENT_MODEL = "llama-3.1-8b-instant";

    public GroqAiService() {
        this.objectMapper = new ObjectMapper();
        System.out.println("GroqAiService initialized");
    }

    public ChatResponse getChatResponse(String userMessage, String context) {
        System.out.println("Processing: " + userMessage);

        // Try API if available
        if (apiKey != null && !apiKey.isEmpty() && !apiKey.equals("your_groq_api_key_here")) {
            try {
                String response = callGroqApi(userMessage, context);
                if (response != null && !response.isEmpty()) {
                    return parseGroqResponse(response, userMessage);
                }
            } catch (Exception e) {
                System.err.println("API error: " + e.getMessage());
            }
        }

        // Return clean, relevant response
        return getCleanRelevantResponse(userMessage, context);
    }

    private String callGroqApi(String userMessage, String context) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);

        String systemPrompt = "You are Cyber-AI Sentinel, a cybersecurity analyst. " +
                "Give ONLY practical, actionable advice. " +
                "Don't repeat commands unnecessarily. " +
                "Be concise but helpful. " +
                "Context: " + context;

        Map<String, Object> requestBody = Map.of(
                "model", CURRENT_MODEL,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userMessage)
                ),
                "temperature", 0.7,
                "max_tokens", 800
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
        }
        return null;
    }

    private ChatResponse parseGroqResponse(String response, String originalMessage) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            String reply = jsonNode.path("choices").path(0).path("message").path("content").asText();
            ChatResponse chatResponse = new ChatResponse(reply, true);
            chatResponse.setRemediation(getSimpleRemediation(originalMessage));
            return chatResponse;
        } catch (Exception e) {
            return getCleanRelevantResponse(originalMessage, "");
        }
    }

    // ========== CLEAN, RELEVANT RESPONSES (No unnecessary commands) ==========

    private ChatResponse getCleanRelevantResponse(String message, String context) {
        String lowerMsg = message.toLowerCase();
        String reply = "";
        List<ChatResponse.RemediationStep> steps = new ArrayList<>();

        int criticalCount = extractCriticalCount(context);
        int threatScore = extractScore(context);

        // Question: "can we solve this error through the cmd"
        if (lowerMsg.contains("solve") || (lowerMsg.contains("cmd") && lowerMsg.contains("error"))) {
            reply = getPracticalSolution(criticalCount, threatScore);
            steps = getPracticalSteps();
        }
        // Question about threats
        else if (lowerMsg.contains("threat") || lowerMsg.contains("attack")) {
            reply = getThreatSummary(criticalCount, threatScore);
            steps = getBasicSecuritySteps();
        }
        // Question about protection
        else if (lowerMsg.contains("protect") || lowerMsg.contains("secure")) {
            reply = getSimpleProtectionGuide();
            steps = getProtectionCommands();
        }
        // General help
        else {
            reply = getSimpleWelcomeMessage();
            steps = getQuickCommands();
        }

        ChatResponse response = new ChatResponse(reply, true);
        response.setRemediation(steps);
        return response;
    }

    private String getPracticalSolution(int criticalCount, int threatScore) {
        if (criticalCount == 0 && threatScore == 0) {
            return "✅ No active threats detected in your logs. Your system appears secure.\n\n" +
                    "To stay protected:\n" +
                    "• Keep your system updated\n" +
                    "• Enable firewall\n" +
                    "• Regular security scans";
        }

        return "🔧 **PRACTICAL SOLUTION FOR YOUR SITUATION**\n\n" +
                "Based on your log analysis showing " + criticalCount + " critical threats:\n\n" +
                "**What these threats mean:**\n" +
                "• SQL Injection: Attackers trying to hack your database\n" +
                "• XSS: Malicious scripts trying to run on your site\n" +
                "• Brute Force: Repeated login attempts from " + (criticalCount > 5 ? "multiple IPs" : "suspicious IPs") + "\n\n" +
                "**What you can do RIGHT NOW:**\n" +
                "1. Block the attacking IPs using your firewall\n" +
                "2. Update your web application security rules\n" +
                "3. Enable a Web Application Firewall (WAF)\n\n" +
                "**Do you want specific commands for your setup?** Tell me if you're using Apache or Nginx, and I'll give you the exact commands.";
    }

    private String getThreatSummary(int criticalCount, int threatScore) {
        return "📊 **THREAT SUMMARY**\n\n" +
                "• Critical Threats: " + criticalCount + "\n" +
                "• Threat Score: " + threatScore + "%\n" +
                "• Risk Level: " + (threatScore > 70 ? "HIGH - Take action today" :
                threatScore > 40 ? "MEDIUM - Plan to fix this week" :
                "LOW - Monitor regularly") + "\n\n" +
                "**Main issues found:**\n" +
                "• SQL Injection attempts\n" +
                "• Cross-site scripting (XSS) attacks\n" +
                "• Unauthorized access attempts\n\n" +
                "**Want step-by-step fixes?** Ask me \"How do I fix SQL injection?\" or \"Show me protection commands\"";
    }

    private String getSimpleProtectionGuide() {
        return "🛡️ **SIMPLE PROTECTION STEPS**\n\n" +
                "**1. Keep everything updated**\n" +
                "Run this weekly: `sudo apt update && sudo apt upgrade`\n\n" +
                "**2. Enable firewall**\n" +
                "`sudo ufw enable`\n\n" +
                "**3. Block suspicious IPs**\n" +
                "`sudo ufw deny from [IP_ADDRESS]`\n\n" +
                "**4. Monitor logs regularly**\n" +
                "`sudo tail -f /var/log/apache2/access.log`\n\n" +
                "**Need help with a specific step? Just ask!**";
    }

    private String getSimpleWelcomeMessage() {
        return "🤖 **Cyber-AI Security Assistant**\n\n" +
                "I can help you with:\n" +
                "• Understanding your threat report\n" +
                "• Getting step-by-step fixes\n" +
                "• Protection commands\n" +
                "• Security best practices\n\n" +
                "**Try asking:**\n" +
                "• \"What threats did you find?\"\n" +
                "• \"How do I fix SQL injection?\"\n" +
                "• \"Show me protection commands\"\n" +
                "• \"How to block attacking IPs?\"";
    }

    // ========== SIMPLE, RELEVANT REMEDIATION STEPS ==========

    private List<ChatResponse.RemediationStep> getPracticalSteps() {
        List<ChatResponse.RemediationStep> steps = new ArrayList<>();
        steps.add(new ChatResponse.RemediationStep(
                "Block attacking IPs",
                "sudo ufw deny from [IP_ADDRESS]",
                "Linux"
        ));
        steps.add(new ChatResponse.RemediationStep(
                "Enable Web Application Firewall",
                "sudo apt install libapache2-mod-security2",
                "Linux (Apache)"
        ));
        steps.add(new ChatResponse.RemediationStep(
                "Check for suspicious processes",
                "ps aux | grep -i suspicious",
                "Linux"
        ));
        return steps;
    }

    private List<ChatResponse.RemediationStep> getBasicSecuritySteps() {
        List<ChatResponse.RemediationStep> steps = new ArrayList<>();
        steps.add(new ChatResponse.RemediationStep(
                "Update system",
                "sudo apt update && sudo apt upgrade -y",
                "Linux"
        ));
        steps.add(new ChatResponse.RemediationStep(
                "Enable firewall",
                "sudo ufw enable",
                "Linux"
        ));
        return steps;
    }

    private List<ChatResponse.RemediationStep> getProtectionCommands() {
        List<ChatResponse.RemediationStep> steps = new ArrayList<>();
        steps.add(new ChatResponse.RemediationStep(
                "Install security tools",
                "sudo apt install fail2ban clamav -y",
                "Linux"
        ));
        steps.add(new ChatResponse.RemediationStep(
                "Run security scan",
                "sudo clamscan -r /home",
                "Linux"
        ));
        return steps;
    }

    private List<ChatResponse.RemediationStep> getQuickCommands() {
        List<ChatResponse.RemediationStep> steps = new ArrayList<>();
        steps.add(new ChatResponse.RemediationStep(
                "Check system logs",
                "sudo journalctl -xe | tail -50",
                "Linux"
        ));
        steps.add(new ChatResponse.RemediationStep(
                "Check active connections",
                "sudo netstat -tunap",
                "Linux"
        ));
        return steps;
    }

    private List<ChatResponse.RemediationStep> getSimpleRemediation(String message) {
        List<ChatResponse.RemediationStep> steps = new ArrayList<>();
        String lower = message.toLowerCase();

        if (lower.contains("sql") || lower.contains("injection")) {
            steps.add(new ChatResponse.RemediationStep(
                    "Protect against SQL Injection",
                    "Use parameterized queries and input validation",
                    "Application Level"
            ));
        } else if (lower.contains("xss")) {
            steps.add(new ChatResponse.RemediationStep(
                    "Protect against XSS",
                    "Add CSP headers: Header set Content-Security-Policy \"default-src 'self'\"",
                    "Web Server"
            ));
        } else {
            steps.add(new ChatResponse.RemediationStep(
                    "Basic security check",
                    "sudo apt update && sudo apt upgrade",
                    "Linux"
            ));
        }
        return steps;
    }

    private int extractCriticalCount(String context) {
        if (context == null || context.isEmpty()) return 0;
        try {
            if (context.contains("\"critical\":")) {
                int idx = context.indexOf("\"critical\":");
                int start = idx + 11;
                int end = context.indexOf(",", start);
                if (end == -1) end = context.indexOf("}", start);
                String num = context.substring(start, end).trim();
                return Integer.parseInt(num);
            }
        } catch (Exception e) {}
        return 0;
    }

    private int extractScore(String context) {
        if (context == null || context.isEmpty()) return 0;
        try {
            if (context.contains("\"threatScore\":")) {
                int idx = context.indexOf("\"threatScore\":");
                int start = idx + 14;
                int end = context.indexOf(",", start);
                if (end == -1) end = context.indexOf("}", start);
                String num = context.substring(start, end).trim();
                return Integer.parseInt(num);
            }
        } catch (Exception e) {}
        return 0;
    }
}