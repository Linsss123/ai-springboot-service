package org.example.lab1.api;

import jakarta.validation.Valid;
import org.example.lab1.api.dto.ChatRequest;
import org.example.lab1.api.dto.ChatResponse;
import org.example.lab1.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        ChatResponse response = chatService.handleChat(request);
        return ResponseEntity.ok(response);
    }
}
