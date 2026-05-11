package org.example.lab1.llm;

import java.util.List;

/**
 * Enkel representation av OpenAI-kompatibel chat request/response.
 */
public class OpenAiTypes {
    public static class ChatRequestBody {
        public String model;
        public List<Message> messages;

        public ChatRequestBody() {}
        public ChatRequestBody(String model, List<Message> messages) {
            this.model = model;
            this.messages = messages;
        }
    }

    public static class Message {
        public String role; // system|user|assistant
        public String content;
        public Message() {}
        public Message(String role, String content) { this.role = role; this.content = content; }
    }

    public static class ChatResponseBody {
        public List<Choice> choices;
    }

    public static class Choice {
        public Message message;
    }
}
