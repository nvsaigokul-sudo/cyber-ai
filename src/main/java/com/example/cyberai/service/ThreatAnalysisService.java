package com.example.cyberai.service;

import com.example.cyberai.model.ThreatReport;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class ThreatAnalysisService {

    private static final Map<String, Integer> THREAT_WEIGHTS = Map.of(
            "critical", 10,
            "high", 5,
            "medium", 2,
            "low", 1
    );

    private static final List<String> CRITICAL_PATTERNS = Arrays.asList(
            "sql injection", "xss", "cross-site scripting", "buffer overflow", "ransomware",
            "rootkit", "backdoor", "exploit", "cve-", "zero-day", "privilege escalation",
            "remote code execution", "rce", "dll injection", "process injection",
            "403", "401", "500", "502", "503", "segmentation fault", "core dumped"
    );

    private static final List<String> HIGH_PATTERNS = Arrays.asList(
            "attack", "intrusion", "malware", "unauthorized", "breach", "injection",
            "overflow", "bypass", "evasion", "persistence", "lateral movement",
            "data exfiltration", "command injection", "path traversal", "xxe"
    );

    private static final List<String> MEDIUM_PATTERNS = Arrays.asList(
            "error", "failed", "timeout", "denied", "invalid", "exception",
            "suspicious", "anomaly", "unusual", "rate limiting", "brute force"
    );

    private static final List<String> LOW_PATTERNS = Arrays.asList(
            "warning", "notice", "debug", "info", "verbose", "trace"
    );

    public ThreatReport analyzeLog(String logContent) {
        if (logContent == null || logContent.isEmpty()) {
            return createEmptyReport();
        }

        String[] lines = logContent.split("\n");
        int critical = 0, high = 0, medium = 0, low = 0;
        List<String> detectedThreats = new ArrayList<>();

        for (String line : lines) {
            String lowerLine = line.toLowerCase();

            if (containsAny(lowerLine, CRITICAL_PATTERNS)) {
                critical++;
                if (detectedThreats.size() < 20) {
                    extractThreatName(line, detectedThreats);
                }
            } else if (containsAny(lowerLine, HIGH_PATTERNS)) {
                high++;
            } else if (containsAny(lowerLine, MEDIUM_PATTERNS)) {
                medium++;
            } else if (containsAny(lowerLine, LOW_PATTERNS)) {
                low++;
            }
        }

        int threatScore = calculateThreatScore(critical, high, medium, lines.length);
        List<Integer> threatDistribution = calculateThreatDistribution(lines);

        ThreatReport report = new ThreatReport(critical, high, medium, low, threatScore, threatDistribution, lines.length);
        report.setDetectedThreats(detectedThreats);
        report.setSummary(generateSummary(critical, high, medium, threatScore, lines.length));

        return report;
    }

    private boolean containsAny(String text, List<String> patterns) {
        for (String pattern : patterns) {
            if (text.contains(pattern)) {
                return true;
            }
        }
        return false;
    }

    private void extractThreatName(String line, List<String> threats) {
        for (String pattern : CRITICAL_PATTERNS) {
            if (line.toLowerCase().contains(pattern) && !threats.contains(pattern.toUpperCase())) {
                threats.add(pattern.toUpperCase());
                break;
            }
        }
    }

    private int calculateThreatScore(int critical, int high, int medium, int totalLines) {
        if (totalLines == 0) return 0;
        double weightedScore = (critical * 10.0 + high * 5.0 + medium * 2.0);
        double maxPossibleScore = (totalLines * 10.0) / 10;
        double score = (weightedScore / maxPossibleScore) * 100;
        return (int) Math.min(100, Math.max(0, score));
    }

    private List<Integer> calculateThreatDistribution(String[] lines) {
        List<Integer> distribution = new ArrayList<>();
        int segments = 10;
        int segmentSize = Math.max(1, lines.length / segments);

        Pattern threatPattern = Pattern.compile("(error|fail|attack|malware|injection|xss|exploit)", Pattern.CASE_INSENSITIVE);

        for (int i = 0; i < segments; i++) {
            int start = i * segmentSize;
            int end = Math.min(start + segmentSize, lines.length);
            int threatCount = 0;

            for (int j = start; j < end; j++) {
                if (threatPattern.matcher(lines[j]).find()) {
                    threatCount++;
                }
            }
            distribution.add(threatCount);
        }
        return distribution;
    }

    private String generateSummary(int critical, int high, int medium, int score, int totalLines) {
        if (critical == 0 && high == 0) {
            return "✅ No critical threats detected. System appears secure.";
        }

        StringBuilder summary = new StringBuilder();
        summary.append("⚠️ Security Analysis Complete:\n");
        summary.append(String.format("• %d critical threats detected\n", critical));
        summary.append(String.format("• %d high-risk issues found\n", high));
        summary.append(String.format("• Threat Score: %d%%\n", score));

        if (score > 70) {
            summary.append("🚨 IMMEDIATE ACTION REQUIRED! Your system is under active threat.");
        } else if (score > 40) {
            summary.append("⚠️ Significant risks detected. Review and remediate soon.");
        } else {
            summary.append("ℹ️ Minor issues found. Monitor regularly.");
        }

        return summary.toString();
    }

    private ThreatReport createEmptyReport() {
        return new ThreatReport(0, 0, 0, 0, 0, Arrays.asList(0,0,0,0,0,0,0,0,0,0), 0);
    }
}