package com.github.milomarten.taisharangers.endpoints;

import com.github.milomarten.taisharangers.image.Point;
import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.image.gradients.TypeGradient;
import com.github.milomarten.taisharangers.image.layers.Layer;
import com.github.milomarten.taisharangers.image.layers.LayeredImage;
import com.github.milomarten.taisharangers.image.layers.MaskFromImage;
import com.github.milomarten.taisharangers.image.sources.GradientSource;
import com.github.milomarten.taisharangers.models.Gender;
import com.github.milomarten.taisharangers.services.FrameGeneratorService;
import com.github.milomarten.taisharangers.services.ColorGeneratorService;
import com.github.milomarten.taisharangers.services.ImageRetrieveService;
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
    private static final Point GRADIENT_START_POINT = new Point(35, 105);
    private static final Point GRADIENT_END_POINT = new Point(105, 35);
    private static final int TOKEN_WIDTH = 140;
    private static final int TOKEN_HEIGHT = 140;
    private static final double GRADIENT_OPACITY = 0.75;
    private static final Point SPRITE_OFFSET = new Point((TOKEN_WIDTH - 96) / 2, (TOKEN_HEIGHT - 96) / 2);
    private static final String RANDOM_ID = "RANDOM";

    private final ImageRetrieveService imageRetrieveService;

    private final PokeApiClient client;

    private final ImageRetrieveService imageRetrieveService;

    private final FrameGeneratorService frameGeneratorService;

    private final ColorGeneratorService colorGeneratorService;

    private final RandomHandler randomHandler;

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

        return (RANDOM_ID.equalsIgnoreCase(id) ?
                randomHandler.handleNoResponse(request) :
                client.getResource(Pokemon.class, id))
                .flatMap(pkmn -> {
                    try {
                        var image = new LayeredImage(TOKEN_WIDTH, TOKEN_HEIGHT);
                        var frame = frameGeneratorService.createFrame();
                        image.addLayer(frame);
                        image.addLayer(Layer.builder()
                                .image(new GradientSource(
                                    GRADIENT_START_POINT,
                                    GRADIENT_END_POINT,
                                    firstOverride.orElseGet(() -> colorGeneratorService.getPrimaryForType(pkmn)),
                                    secondOverride.orElseGet(() -> colorGeneratorService.getSecondaryForType(pkmn))
                                ))
                                .opacity(GRADIENT_OPACITY)
                                .mask(new MaskFromImage(frame))
                                .build());
                        image.addLayer(Layer.builder()
                                .image(imageRetrieveService.getSprite(pkmn, gender, shiny))
                                .offset(SPRITE_OFFSET)
                                .build()
                        );
                        return Mono.just(image.toImage());
                    } catch (RuntimeException | IOException e) {
                        e.printStackTrace();
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
