package com.github.milomarten.taisharangers.endpoints;

import com.github.milomarten.taisharangers.image.Point;
import com.github.milomarten.taisharangers.image.layers.Layer;
import com.github.milomarten.taisharangers.image.layers.LayeredImage;
import com.github.milomarten.taisharangers.image.layers.MaskFromImage;
import com.github.milomarten.taisharangers.image.sources.GradientSource;
import com.github.milomarten.taisharangers.models.Gender;
import com.github.milomarten.taisharangers.services.FrameGeneratorService;
import com.github.milomarten.taisharangers.services.GradientGeneratorService;
import com.github.milomarten.taisharangers.services.ImageRetrieveService;
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
    private static final Point GRADIENT_START_POINT = new Point(35, 105);
    private static final Point GRADIENT_END_POINT = new Point(105, 35);
    private static final String RANDOM_ID = "RANDOM";

    @Autowired
    private ImageRetrieveService imageRetrieveService;

    @Autowired
    private FrameGeneratorService frameGeneratorService;

    @Autowired
    private GradientGeneratorService gradientGeneratorService;

    @Autowired
    private PokeApiClient client;

    @Autowired
    private RandomHandler randomHandler;

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        String id = request.pathVariable("id");
        Gender gender = request.queryParam("gender")
                .map(value -> EnumUtils.getEnum(Gender.class, value, Gender.MALE))
                .orElse(Gender.MALE);
        boolean shiny = request.queryParam("shiny")
                .map(Boolean::parseBoolean)
                .orElse(false);

        return (RANDOM_ID.equalsIgnoreCase(id) ?
                randomHandler.handleNoResponse(request) :
                client.getResource(Pokemon.class, id))
                .flatMap(pkmn -> {
                    try {
                        var image = new LayeredImage(140, 140);
                        var frame = frameGeneratorService.createFrame();
                        var colors = gradientGeneratorService.byType(pkmn);
                        image.addLayer(frame);
                        image.addLayer(Layer.builder()
                                .image(new GradientSource(
                                    GRADIENT_START_POINT,
                                    GRADIENT_END_POINT,
                                    colors.start(),
                                    colors.end()
                                ))
                                .opacity(0.75)
                                .mask(new MaskFromImage(frame))
                                .build());
                        image.addLayer(Layer.builder()
                                .image(imageRetrieveService.getSprite(pkmn, gender, shiny))
                                .offset(new Point(22, 22))
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
