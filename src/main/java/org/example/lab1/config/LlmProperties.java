package org.example.lab1.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "llm")
public class LlmProperties {
    /**
     * Bas-URL till LLM-leverantören. För OpenRouter t.ex. https://openrouter.ai/api/v1/chat/completions
     */
    private String baseUrl;

    /**
     * API-nyckel. Anges via miljövariabel eller application.properties, ej hårdkodad.
     */
    private String apiKey;

    /**
     * Modellnamn hos leverantören. Ex: openrouter/auto, meta-llama/Meta-Llama-3.1-8B-Instruct:free
     */
    private String model = "openrouter/auto";

    /**
     * Timeout för TCP-anslutning till LLM (ms). Standard 2000 ms.
     */
    private int connectTimeoutMs = 2000;

    /**
     * Timeout för att läsa svar från LLM (ms). Standard 60000 ms.
     */
    private int readTimeoutMs = 60000;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public void setConnectTimeoutMs(int connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
    }

    public int getReadTimeoutMs() {
        return readTimeoutMs;
    }

    public void setReadTimeoutMs(int readTimeoutMs) {
        this.readTimeoutMs = readTimeoutMs;
    }
}
