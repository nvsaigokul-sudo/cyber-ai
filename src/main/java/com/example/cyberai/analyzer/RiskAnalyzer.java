package com.example.cyberai.analyzer;

import org.springframework.stereotype.Component;

@Component
public class RiskAnalyzer {

    public String getRisk(String attack) {

        if (attack.equals("No major threat")) return "LOW";

        if (attack.contains("Brute") || attack.contains("Unauthorized"))
            return "MEDIUM";

        return "HIGH";
    }
}