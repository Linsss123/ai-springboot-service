package org.example.lab1.api.dto;

public class ChatResponse {
    private String sessionId;
    private String personality;
    private String reply;

    public ChatResponse() {}

    public ChatResponse(String sessionId, String personality, String reply) {
        this.sessionId = sessionId;
        this.personality = personality;
        this.reply = reply;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
