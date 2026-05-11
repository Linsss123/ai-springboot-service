package org.example.lab1.service;

import org.example.lab1.api.dto.ChatRequest;
import org.example.lab1.api.dto.ChatResponse;
import org.example.lab1.memory.InMemoryChatMemory;
import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.UUID;

@Service
public class ChatServiceImpl implements ChatService {

    private final InMemoryChatMemory memory;
    private final PersonalityPrompts prompts;

    public ChatServiceImpl(InMemoryChatMemory memory, PersonalityPrompts prompts) {
        this.memory = memory;
        this.prompts = prompts;
    }

    @Override
    public ChatResponse handleChat(ChatRequest request) {
        String sessionId = request.getSessionId();
        if (sessionId == null || sessionId.isBlank()) {
            sessionId = UUID.randomUUID().toString();
        }

        String systemPrompt = prompts.systemPromptFor(request.getPersonality());

        // Lagra användarens meddelande
        memory.addUserMessage(sessionId, request.getMessage());

        // Hämta lite historik för att visa att minnet fungerar (stub-logik)
        Deque<String> history = memory.getHistory(sessionId);

        // Stubbsvar: eko + personlighet. Här kommer senare anrop till extern LLM att ligga.
        String reply = "[" + request.getPersonality() + "] " +
                "System: " + systemPrompt + " | Du skrev: " + request.getMessage();

        // Lagra assistentens svar
        memory.addAssistantMessage(sessionId, reply);

        return new ChatResponse(sessionId, request.getPersonality(), reply);
    }
}
