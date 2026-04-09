package com.example.cyberai.model;

import java.util.List;
import java.util.Date;

public class ThreatReport {
    private int critical;
    private int high;
    private int medium;
    private int low;
    private int threatScore;
    private List<Integer> threatDistribution;
    private int totalLines;
    private String timestamp;
    private List<String> detectedThreats;
    private String summary;

    // Constructors
    public ThreatReport() {}

    public ThreatReport(int critical, int high, int medium, int low, int threatScore,
                        List<Integer> threatDistribution, int totalLines) {
        this.critical = critical;
        this.high = high;
        this.medium = medium;
        this.low = low;
        this.threatScore = threatScore;
        this.threatDistribution = threatDistribution;
        this.totalLines = totalLines;
        this.timestamp = new Date().toString();
    }

    // Getters and Setters
    public int getCritical() { return critical; }
    public void setCritical(int critical) { this.critical = critical; }

    public int getHigh() { return high; }
    public void setHigh(int high) { this.high = high; }

    public int getMedium() { return medium; }
    public void setMedium(int medium) { this.medium = medium; }

    public int getLow() { return low; }
    public void setLow(int low) { this.low = low; }

    public int getThreatScore() { return threatScore; }
    public void setThreatScore(int threatScore) { this.threatScore = threatScore; }

    public List<Integer> getThreatDistribution() { return threatDistribution; }
    public void setThreatDistribution(List<Integer> threatDistribution) { this.threatDistribution = threatDistribution; }

    public int getTotalLines() { return totalLines; }
    public void setTotalLines(int totalLines) { this.totalLines = totalLines; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public List<String> getDetectedThreats() { return detectedThreats; }
    public void setDetectedThreats(List<String> detectedThreats) { this.detectedThreats = detectedThreats; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}