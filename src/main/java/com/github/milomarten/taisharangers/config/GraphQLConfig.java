package com.github.milomarten.taisharangers.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GraphQLConfig {
    @Bean
    public HttpGraphQlClient graphQlClient(@Value("${skaro.pokeapi.graphql-url}") String url) {
        return HttpGraphQlClient.builder()
                .url(url)
                .build();
    }
}
