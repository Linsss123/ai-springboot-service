package org.example.lab1.service;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PersonalityPrompts {
    private static final Map<String, String> MAP = Map.of(
            "helper", "You are a friendly, concise assistant. Answer helpfully in Swedish when appropriate.",
            "pirate", "Speak like a pirate. Be witty but still helpful. Keep answers understandable.",
            "coder", "You are a senior software engineer. Provide clear, idiomatic code examples and explanations."
    );

    public String systemPromptFor(String personality) {
        if (personality == null) return MAP.get("helper");
        return MAP.getOrDefault(personality.toLowerCase(), MAP.get("helper"));
    }
}
