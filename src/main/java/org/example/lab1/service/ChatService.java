package org.example.lab1.service;

import org.example.lab1.api.dto.ChatRequest;
import org.example.lab1.api.dto.ChatResponse;

public interface ChatService {
    ChatResponse handleChat(ChatRequest request);
}
