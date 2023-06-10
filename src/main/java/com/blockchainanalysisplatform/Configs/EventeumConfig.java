package com.blockchainanalysisplatform.Configs;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class EventeumConfig {

    @Bean
    WebClient webclientToEventeum(@Value("${eventeumUrl}")String url) {
        return WebClient.create(url);
    }
}
