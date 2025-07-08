package br.dev.leandro.spring.event.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class WireMockTestConfig {

    private final WireMockServer wireMockServer = new WireMockServer(
            WireMockConfiguration.options().dynamicPort()
    );

    @Bean
    public WireMockServer wireMockServer() {
        wireMockServer.start();
        return wireMockServer;
    }

    @PreDestroy
    public void stopServer() {
        wireMockServer.stop();
    }
}

