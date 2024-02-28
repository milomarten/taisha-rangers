package com.github.milomarten.taisharangers.services;

import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.image.Point;
import com.github.milomarten.taisharangers.image.effects.Effects;
import com.github.milomarten.taisharangers.image.layers.Layer;
import com.github.milomarten.taisharangers.image.layers.LayeredImage;
import com.github.milomarten.taisharangers.image.layers.MaskFromImage;
import com.github.milomarten.taisharangers.image.sources.GradientSource;
import com.github.milomarten.taisharangers.models.Gender;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import skaro.pokeapi.resource.pokemon.Pokemon;

import java.io.IOException;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class TokenGeneratorService {
    private static final Point GRADIENT_START_POINT = new Point(35, 105);
    private static final Point GRADIENT_END_POINT = new Point(105, 35);
    private static final int TOKEN_WIDTH = 140;
    private static final int TOKEN_HEIGHT = 140;
    private static final double GRADIENT_OPACITY = 0.75;
    private static final Point SPRITE_OFFSET = new Point((TOKEN_WIDTH - 96) / 2, (TOKEN_HEIGHT - 96) / 2);

    private final FrameGeneratorService frameGeneratorService;
    private final ImageRetrieveService imageRetrieveService;
    private final ColorGeneratorService colorGeneratorService;

    public LayeredImage generateToken(Pokemon pokemon, CustomizationOptions options) {
        try {
            var image = new LayeredImage(TOKEN_WIDTH, TOKEN_HEIGHT);
            var frame = frameGeneratorService.createFrame();
            image.addLayer(Layer.builder()
                    .image(frame)
                    .effect(options.effect.getFrameEffect())
                    .build());
            if (options.effect.isShowGradient()) {
                image.addLayer(Layer.builder()
                        .image(new GradientSource(
                                GRADIENT_START_POINT,
                                GRADIENT_END_POINT,
                                Objects.requireNonNullElseGet(options.firstColor, () -> colorGeneratorService.getPrimaryForType(pokemon)),
                                Objects.requireNonNullElseGet(options.secondColor, () -> colorGeneratorService.getSecondaryForType(pokemon))
                        ))
                        .opacity(GRADIENT_OPACITY)
                        .mask(new MaskFromImage(frame))
                        .build());
            }
            image.addLayer(Layer.builder()
                    .image(imageRetrieveService.getSprite(pokemon, options.gender, options.shiny))
                    .offset(SPRITE_OFFSET)
                    .effect(options.effect.getSpriteEffect())
                    .build()
            );
            return image;
        } catch (RuntimeException | IOException e) {
            log.error("Unable to generate token", e);
            return null;
        }
    }

    @Builder
    public static class CustomizationOptions {
        private Color firstColor;
        private Color secondColor;
        @Builder.Default private Gender gender = Gender.MALE;
        private boolean shiny;
        @Builder.Default private Effects effect = Effects.NONE;
    }
}
