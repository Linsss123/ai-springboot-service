package org.example.lab1.api;

import org.example.lab1.api.dto.ChatResponse;
import org.example.lab1.service.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ChatController.class)
class ChatControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @Test
    @DisplayName("200 OK vid giltigt request")
    void chat_ok() throws Exception {
        ChatResponse resp = new ChatResponse("sess-1", "coder", "Hej från AI");
        Mockito.when(chatService.handleChat(Mockito.any())).thenReturn(resp);

        String body = "{\n" +
                "  \"personality\": \"coder\",\n" +
                "  \"message\": \"Hur skriver jag en for-loop i Java?\"\n" +
                "}";

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sessionId").value("sess-1"))
                .andExpect(jsonPath("$.personality").value("coder"))
                .andExpect(jsonPath("$.reply").value("Hej från AI"));
    }

    @Test
    @DisplayName("400 Bad Request vid valideringsfel")
    void chat_validation_error() throws Exception {
        String body = "{\n" +
                "  \"personality\": \"\",\n" +
                "  \"message\": \"\"\n" +
                "}";

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }
}
