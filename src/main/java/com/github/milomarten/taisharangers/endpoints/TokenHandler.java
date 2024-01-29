package com.github.milomarten.taisharangers.endpoints;

import com.github.milomarten.taisharangers.image.Gender;
import com.github.milomarten.taisharangers.image.ImageRetrieveService;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.pokemon.Pokemon;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class TokenHandler implements HandlerFunction<ServerResponse> {
    @Autowired
    private ImageRetrieveService imageRetrieveService;

    @Autowired
    private PokeApiClient client;

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        String id = request.pathVariable("id");
        Gender gender = request.queryParam("gender")
                .map(value -> EnumUtils.getEnum(Gender.class, value, Gender.MALE))
                .orElse(Gender.MALE);
        boolean shiny = request.queryParam("shiny")
                .map(Boolean::parseBoolean)
                .orElse(false);

        return client.getResource(Pokemon.class, id)
                .flatMap(pkmn -> {
                    try {
                        return Mono.just(imageRetrieveService.get(pkmn, gender, shiny));
                    } catch (IOException e) {
                        return Mono.error(e);
                    }
                })
                .flatMap(bi -> {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    try {
                        ImageIO.write(bi, "png", os);
                        return ServerResponse.ok()
                                .contentType(MediaType.IMAGE_PNG)
                                .bodyValue(os.toByteArray());
                    } catch (IOException e) {
                        return Mono.error(e);
                    }
                });
    }
}
