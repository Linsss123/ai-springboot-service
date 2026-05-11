package org.example.lab1.llm;

import org.example.lab1.config.LlmProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static org.example.lab1.llm.OpenAiTypes.*;

@Component
@ConditionalOnBean(RestClient.class)
public class LlmClient {
    private static final Logger log = LoggerFactory.getLogger(LlmClient.class);

    private final RestClient restClient;
    private final LlmProperties props;

    public LlmClient(RestClient restClient, LlmProperties props) {
        this.restClient = restClient;
        this.props = props;
    }

    /**
     * Anropar en OpenAI-kompatibel endpoint. Retry på 429/503 med exponentiell backoff.
     */
    @Retryable(
            include = { RestClientResponseException.class },
            exceptionExpression = "#root.cause != null && (#root.cause instanceof T(org.springframework.web.client.RestClientResponseException)) ? ((org.springframework.web.client.RestClientResponseException)#root.cause).getStatusCode().value() == 429 || ((org.springframework.web.client.RestClientResponseException)#root.cause).getStatusCode().value() == 503 : false",
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, multiplier = 2.0)
    )
    public String complete(String systemPrompt, Deque<String> history, String userMessage) {
        List<Message> messages = new ArrayList<>();
        if (systemPrompt != null && !systemPrompt.isBlank()) {
            messages.add(new Message("system", systemPrompt));
        }
        if (history != null) {
            for (String h : history) {
                if (h.startsWith("user: ")) {
                    messages.add(new Message("user", h.substring(6)));
                } else if (h.startsWith("assistant: ")) {
                    messages.add(new Message("assistant", h.substring(11)));
                }
            }
        }
        messages.add(new Message("user", userMessage));

        ChatRequestBody body = new ChatRequestBody(props.getModel(), messages);

        try {
            ChatResponseBody resp = restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(ChatResponseBody.class);

            if (resp == null || resp.choices == null || resp.choices.isEmpty() || resp.choices.get(0).message == null) {
                throw new AiServiceException("Tomt svar från LLM");
            }
            return resp.choices.get(0).message.content;
        } catch (RestClientResponseException e) {
            int code = e.getStatusCode().value();
            log.warn("LLM fel {}: {}", code, e.getResponseBodyAsString());
            // kasta vidare för ev. retry/global handler
            throw e;
        } catch (Exception e) {
            throw new AiServiceException("Fel vid anrop till LLM", e);
        }
    }
}
