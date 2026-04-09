package com.example.cyberai.model;

import java.util.List;

public class ChatResponse {
    private String reply;
    private List<RemediationStep> remediation;
    private boolean success;

    public static class RemediationStep {
        private String description;
        private String command;
        private String os;

        public RemediationStep() {}

        public RemediationStep(String description, String command, String os) {
            this.description = description;
            this.command = command;
            this.os = os;
        }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCommand() { return command; }
        public void setCommand(String command) { this.command = command; }
        public String getOs() { return os; }
        public void setOs(String os) { this.os = os; }
    }

    public ChatResponse() {}

    public ChatResponse(String reply, boolean success) {
        this.reply = reply;
        this.success = success;
    }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }
    public List<RemediationStep> getRemediation() { return remediation; }
    public void setRemediation(List<RemediationStep> remediation) { this.remediation = remediation; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}