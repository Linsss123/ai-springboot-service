package org.example.lab1.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    @ConditionalOnProperty(prefix = "llm", name = {"base-url"})
    public RestClient llmRestClient(LlmProperties props) {
        RestClient.Builder builder = RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory())
                .baseUrl(props.getBaseUrl());

        if (props.getApiKey() != null && !props.getApiKey().isBlank()) {
            builder = builder.defaultHeader("Authorization", "Bearer " + props.getApiKey());
        }

        return builder.build();
    }
}
