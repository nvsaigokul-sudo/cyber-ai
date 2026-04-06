package com.example.cyberai.dto;

public class AnalysisResponse {

    private String attack;
    private String risk;
    private String aiExplanation;

    // ✅ Default constructor
    public AnalysisResponse() {}

    // ✅ Parameterized constructor
    public AnalysisResponse(String attack, String risk, String aiExplanation) {
        this.attack = attack;
        this.risk = risk;
        this.aiExplanation = aiExplanation;
    }

    // ✅ Getters
    public String getAttack() {
        return attack;
    }

    public String getRisk() {
        return risk;
    }

    public String getAiExplanation() {
        return aiExplanation;
    }

    // ✅ Setters
    public void setAttack(String attack) {
        this.attack = attack;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public void setAiExplanation(String aiExplanation) {
        this.aiExplanation = aiExplanation;
    }
}