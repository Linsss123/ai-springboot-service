package org.example.lab1.memory;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enkel in-memory lagring av chatt-historik per sessionId.
 */
@Component
public class InMemoryChatMemory {
    private final Map<String, Deque<String>> historyBySession = new ConcurrentHashMap<>();
    private final int maxMessagesPerSession = 20; // enkel begränsning

    public Deque<String> getHistory(String sessionId) {
        return historyBySession.computeIfAbsent(sessionId, k -> new ArrayDeque<>());
    }

    public void addUserMessage(String sessionId, String message) {
        append(sessionId, "user: " + message);
    }

    public void addAssistantMessage(String sessionId, String message) {
        append(sessionId, "assistant: " + message);
    }

    private void append(String sessionId, String entry) {
        Deque<String> deque = getHistory(sessionId);
        if (deque.size() >= maxMessagesPerSession) {
            deque.removeFirst();
        }
        deque.addLast(entry);
    }
}
