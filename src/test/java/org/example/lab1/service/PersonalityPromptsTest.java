package org.example.lab1.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonalityPromptsTest {

    @Test
    void returnsHelperByDefault() {
        PersonalityPrompts p = new PersonalityPrompts();
        String prompt = p.systemPromptFor(null);
        assertNotNull(prompt);
        assertTrue(prompt.toLowerCase().contains("helpful") || prompt.toLowerCase().contains("friendly"));
    }

    @Test
    void mapsKnownPersonalitiesCaseInsensitive() {
        PersonalityPrompts p = new PersonalityPrompts();
        String pirate = p.systemPromptFor("PIRATE");
        assertTrue(pirate.toLowerCase().contains("pirate"));

        String coder = p.systemPromptFor("coder");
        assertTrue(coder.toLowerCase().contains("engineer") || coder.toLowerCase().contains("code"));
    }

    @Test
    void unknownFallsBackToHelper() {
        PersonalityPrompts p = new PersonalityPrompts();
        String prompt = p.systemPromptFor("unknown-value");
        assertNotNull(prompt);
        assertTrue(prompt.toLowerCase().contains("helpful") || prompt.toLowerCase().contains("friendly"));
    }
}
