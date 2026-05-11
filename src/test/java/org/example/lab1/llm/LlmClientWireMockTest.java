package org.example.lab1.llm;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.ArrayDeque;
import java.util.Deque;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * WireMock-integrationstester för LlmClient med riktig Spring-proxy (@Retryable).
 */
@SpringBootTest
class LlmClientWireMockTest {

    @RegisterExtension
    static WireMockExtension wm = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void dynamicProps(DynamicPropertyRegistry registry) {
        registry.add("llm.base-url", () -> wm.baseUrl() + "/v1/chat/completions");
        registry.add("llm.model", () -> "test-model");
        // api-key lämnas tom
    }

    @Autowired
    private LlmClient client;

    private static ResponseDefinitionBuilder okResp(String content) {
        String body = "{\n" +
                "  \"choices\": [ { \"message\": { \"role\": \"assistant\", \"content\": "
                + toJsonString(content) + " } } ]\n" +
                "}";
        return aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(body);
    }

    private static String toJsonString(String s) {
        return '"' + s.replace("\\", "\\\\").replace("\"", "\\\"") + '"';
    }

    private static Deque<String> sampleHistory() {
        Deque<String> h = new ArrayDeque<>();
        h.addLast("user: Hej");
        h.addLast("assistant: Hej! Hur kan jag hjälpa?");
        return h;
    }

    @Test
    @DisplayName("200 OK direkt utan retry")
    void ok_direct() {
        wm.stubFor(post(urlEqualTo("/v1/chat/completions"))
                .willReturn(okResp("Svar A")));

        String r = client.complete("sys", sampleHistory(), "Fråga");
        Assertions.assertEquals("Svar A", r);

        wm.verify(1, postRequestedFor(urlEqualTo("/v1/chat/completions")));
    }

    @Test
    @DisplayName("429 → 200 med retry")
    void retry_on_429_then_success() {
        wm.stubFor(post(urlEqualTo("/v1/chat/completions")).inScenario("429-then-200")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(429).withBody("{\"error\":\"rate limit\"}"))
                .willSetStateTo("OK"));

        wm.stubFor(post(urlEqualTo("/v1/chat/completions")).inScenario("429-then-200")
                .whenScenarioStateIs("OK")
                .willReturn(okResp("Efter retry")));

        String r = client.complete("sys", sampleHistory(), "Fråga");
        Assertions.assertEquals("Efter retry", r);

        wm.verify(2, postRequestedFor(urlEqualTo("/v1/chat/completions")));
    }

    @Test
    @DisplayName("503 → 503 → 200 med retrys")
    void retry_on_503_then_success() {
        wm.stubFor(post(urlEqualTo("/v1/chat/completions")).inScenario("503-chain")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(503).withBody("down"))
                .willSetStateTo("STEP2"));
        wm.stubFor(post(urlEqualTo("/v1/chat/completions")).inScenario("503-chain")
                .whenScenarioStateIs("STEP2")
                .willReturn(aResponse().withStatus(503).withBody("still down"))
                .willSetStateTo("OK"));
        wm.stubFor(post(urlEqualTo("/v1/chat/completions")).inScenario("503-chain")
                .whenScenarioStateIs("OK")
                .willReturn(okResp("Till slut OK")));

        String r = client.complete("sys", sampleHistory(), "Fråga");
        Assertions.assertEquals("Till slut OK", r);
        wm.verify(3, postRequestedFor(urlEqualTo("/v1/chat/completions")));
    }

    @Test
    @DisplayName("400 Bad Request → AiServiceException utan retry")
    void no_retry_on_400() {
        wm.stubFor(post(urlEqualTo("/v1/chat/completions")).willReturn(
                aResponse().withStatus(400).withBody("bad req")));

        AiServiceException ex = Assertions.assertThrows(AiServiceException.class,
                () -> client.complete("sys", sampleHistory(), "Fråga"));
        Assertions.assertTrue(ex.getMessage().contains("LLM fel 400"));
        wm.verify(1, postRequestedFor(urlEqualTo("/v1/chat/completions")));
    }

    @Test
    @DisplayName("500 Internal Server Error → AiServiceException utan retry")
    void no_retry_on_500() {
        wm.stubFor(post(urlEqualTo("/v1/chat/completions")).willReturn(
                aResponse().withStatus(500).withBody("oops")));

        AiServiceException ex = Assertions.assertThrows(AiServiceException.class,
                () -> client.complete("sys", sampleHistory(), "Fråga"));
        Assertions.assertTrue(ex.getMessage().contains("LLM fel 500"));
        wm.verify(1, postRequestedFor(urlEqualTo("/v1/chat/completions")));
    }

    @Test
    @DisplayName("Tomt/ogiltigt svar → AiServiceException")
    void empty_response_body() {
        wm.stubFor(post(urlEqualTo("/v1/chat/completions")).willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("{}")));

        Assertions.assertThrows(AiServiceException.class,
                () -> client.complete("sys", sampleHistory(), "Fråga"));
        wm.verify(1, postRequestedFor(urlEqualTo("/v1/chat/completions")));
    }
}
