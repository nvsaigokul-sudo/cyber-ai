package com.example.cyberai.service;

import com.example.cyberai.dto.AnalysisResponse;
import com.example.cyberai.engine.DetectionEngine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@Service
public class LogService {

    @Autowired
    private DetectionEngine detectionEngine;

    @Autowired
    private AIService aiService;

    public AnalysisResponse processLog(MultipartFile file) {

        try {
            // 📄 Read log file
            String logText = new String(file.getBytes(), StandardCharsets.UTF_8);

            // 🔍 Detect attack + risk
            String[] result = detectionEngine.analyze(logText);

            String attack = result[0];
            String risk = result[1];

            // 🤖 AI explanation
            String aiExplanation = aiService.getExplanation(attack, risk);

            // ✅ RETURN USING CONSTRUCTOR (SAFE)
            return new AnalysisResponse(attack, risk, aiExplanation);

        } catch (Exception e) {
            e.printStackTrace();

            return new AnalysisResponse(
                    "Error",
                    "UNKNOWN",
                    "Error processing log file"
            );
        }
    }
}