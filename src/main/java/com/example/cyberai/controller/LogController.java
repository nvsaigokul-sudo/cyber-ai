package com.example.cyberai.controller;

import com.example.cyberai.model.ThreatReport;
import com.example.cyberai.service.ThreatAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LogController {

    @Autowired
    private ThreatAnalysisService analysisService;

    @PostMapping("/analyze")
    public ResponseEntity<ThreatReport> analyzeLog(@RequestBody Map<String, String> request) {
        String logContent = request.get("logContent");
        String fileName = request.get("fileName");

        System.out.println("Analyzing file: " + fileName);

        ThreatReport report = analysisService.analyzeLog(logContent);
        return ResponseEntity.ok(report);
    }
}