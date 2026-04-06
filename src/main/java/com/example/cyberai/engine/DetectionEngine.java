package com.example.cyberai.engine;

import org.springframework.stereotype.Component;

@Component
public class DetectionEngine {

    public String[] analyze(String log) {

        log = log.toLowerCase();

        int score = 0;
        String attack = "No major threat";

        // 🔴 Brute Force
        if (log.contains("failed login") || log.contains("brute force")) {
            score += 3;
            attack = "Brute Force Attack Detected";
        }

        // 🔴 SQL Injection
        if (log.contains("select *") || log.contains("'1'='1") || log.contains("sql injection")) {
            score += 3;
            attack = "SQL Injection Attempt Detected";
        }

        // 🔴 Port Scan
        if (log.contains("port scan")) {
            score += 2;
            attack = "Port Scanning Detected";
        }

        // 🔴 Malware Upload
        if (log.contains("malware") || log.contains(".exe")) {
            score += 3;
            attack = "Malicious File Upload Detected";
        }

        // 🔴 Unauthorized Access
        if (log.contains("unauthorized") || log.contains("/admin")) {
            score += 2;
            attack = "Unauthorized Access Attempt";
        }

        // 🔴 XSS Attack
        if (log.contains("<script>")) {
            score += 3;
            attack = "Cross-Site Scripting (XSS) Attack";
        }

        // 🔴 Data Exfiltration
        if (log.contains("data exfiltration") || log.contains("outbound traffic")) {
            score += 4;
            attack = "Data Exfiltration Attempt Detected";
        }

        // 🔴 Suspicious Activity
        if (log.contains("suspicious")) {
            score += 1;
        }

        // 🎯 FINAL RISK LEVEL
        String risk;

        if (score >= 7) {
            risk = "HIGH";
        } else if (score >= 4) {
            risk = "MEDIUM";
        } else if (score > 0) {
            risk = "LOW";
        } else {
            risk = "LOW";
        }

        return new String[]{attack, risk};
    }
}