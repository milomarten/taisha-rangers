package com.github.milomarten.taisharangers.endpoints;

import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.image.Point;
import com.github.milomarten.taisharangers.image.gradients.TypeGradient;
import com.github.milomarten.taisharangers.image.layers.Layer;
import com.github.milomarten.taisharangers.image.layers.LayeredImage;
import com.github.milomarten.taisharangers.image.layers.MaskFromImage;
import com.github.milomarten.taisharangers.image.sources.GradientSource;
import com.github.milomarten.taisharangers.models.Gender;
import com.github.milomarten.taisharangers.services.ColorGeneratorService;
import com.github.milomarten.taisharangers.services.FrameGeneratorService;
import com.github.milomarten.taisharangers.services.ImageRetrieveService;
import com.github.milomarten.taisharangers.services.TokenGeneratorService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
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
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TokenHandler implements HandlerFunction<ServerResponse> {
    private static final String RANDOM_ID = "RANDOM";

    private final PokeApiClient client;

    private final PokemonHandler pokemonHandler;

    private final TokenGeneratorService tokenGeneratorService;

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        String id = request.pathVariable("id");
        Gender gender = request.queryParam("gender")
                .map(value -> EnumUtils.getEnumIgnoreCase(Gender.class, value, Gender.MALE))
                .orElse(Gender.MALE);
        boolean shiny = request.queryParam("shiny")
                .map(Boolean::parseBoolean)
                .orElse(false);
        Optional<Color> firstOverride = request.queryParam("type1")
                .map(s -> EnumUtils.getEnumIgnoreCase(TypeGradient.class, s))
                .map(TypeGradient::getLighter);
        Optional<Color> secondOverride = request.queryParam("type2")
                .map(s -> EnumUtils.getEnumIgnoreCase(TypeGradient.class, s))
                .map(TypeGradient::getDarker);

        var customization = TokenGeneratorService.CustomizationOptions.builder()
                .gender(gender)
                .shiny(shiny)
                .firstColor(firstOverride.orElse(null))
                .secondColor(secondOverride.orElse(null))
                .build();

        return (RANDOM_ID.equalsIgnoreCase(id) ?
                pokemonHandler.handleNoResponse(request) :
                client.getResource(Pokemon.class, id))
                .flatMap(pkmn -> {
                    var token = tokenGeneratorService.generateToken(pkmn, customization);
                    if (token == null) {
                        return Mono.error(() -> new IOException("Error retrieving token"));
                    } else {
                        return Mono.just(token);
                    }
                })
                .flatMap(token -> {
                    try {
                        return ServerResponse.ok()
                                .contentType(MediaType.IMAGE_PNG)
                                .bodyValue(token.toBytes());
                    } catch (IOException e) {
                        return Mono.error(e);
                    }
                });
    }
}
