package org.example.lab1.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    @ConditionalOnProperty(prefix = "llm", name = {"base-url"})
    public RestClient llmRestClient(LlmProperties props, RestClient.Builder builder) {
        // Sätt rimliga timeouts så att Swagger/anrop inte "hänger" om LLM inte svarar
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(2000); // ms
        // Första svaret från lokala modeller kan ta tid ("cold start"), ge generös read-timeout
        requestFactory.setReadTimeout(60000);   // ms

        // Använd Spring Boots auto-konfigurerade RestClient.Builder (injicerad)
        // så att korrekta HttpMessageConverters (Jackson) finns → JSON-body skickas rätt.
        RestClient.Builder b = builder
                .requestFactory(requestFactory)
                .baseUrl(props.getBaseUrl());

        if (props.getApiKey() != null && !props.getApiKey().isBlank()) {
            b = b.defaultHeader("Authorization", "Bearer " + props.getApiKey());
        }

        return b.build();
    }
}
