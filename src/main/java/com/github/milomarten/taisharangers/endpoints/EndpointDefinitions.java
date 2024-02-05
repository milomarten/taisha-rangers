package com.github.milomarten.taisharangers.endpoints;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class EndpointDefinitions {
    @Bean
    public RouterFunction<ServerResponse> routes(TokenHandler handler, RandomHandler randomHandler) {
        return route()
                .GET("/token/{id}", handler)
                .GET("/pokemon/random", randomHandler)
                .onError(t -> true, (thrown, req) -> {
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .bodyValue("Error: " + thrown.getMessage());
                })
                .build();
    }
}
