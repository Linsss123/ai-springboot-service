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
}
