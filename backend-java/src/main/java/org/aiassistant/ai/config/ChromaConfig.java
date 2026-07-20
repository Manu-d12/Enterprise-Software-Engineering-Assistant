package org.aiassistant.ai.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chroma.vectorstore.ChromaApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ReactorClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class ChromaConfig {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(60);

    /**
     * Long code-generation LLM responses can take much longer than the framework default.
     * {@link ReactorClientHttpRequestFactory} otherwise applies a hard 10-second response
     * timeout, which made the OpenAI blueprint/file-generation calls fail ~10s in with a
     * reactor-netty ReadTimeoutException (surfacing as ResourceAccessException "I/O error").
     */
    private static final Duration RESPONSE_TIMEOUT = Duration.ofMinutes(5);

    /**
     * The single {@code RestClient.Builder} bean in the app. It is shared by the Chroma
     * vector store AND resolved by Spring AI's OpenAI model (via {@code getIfAvailable}),
     * so the generous timeout configured here is what actually governs the OpenAI chat calls.
     */
    @Bean
    public RestClient.Builder restClientBuilder() {
        ReactorClientHttpRequestFactory factory = new ReactorClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT);
        // Maps to reactor-netty's responseTimeout, overriding the factory's 10s default.
        factory.setReadTimeout(RESPONSE_TIMEOUT);
        return RestClient.builder().requestFactory(factory);
    }

    @Bean
    public ChromaApi chromaApi(RestClient.Builder builder) {
        return new ChromaApi("http://localhost:8000", builder, new ObjectMapper());
    }
}
