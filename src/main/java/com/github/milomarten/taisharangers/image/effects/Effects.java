package com.github.milomarten.taisharangers.image.effects;

import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.models.graphql.operations.In;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Effects {
    NONE(new NoopEffect(), true, new NoopEffect()),
    SHADOW(new ShadowEffect(), false, ColorMapEffect.builder()
            .colorMapping(new Color(38, 50, 56), Color.WHITE)
            .colorMapping(Color.WHITE, new Color(30, 30, 30))
            .build()),
    INVERT(new InvertEffect(), true, new InvertEffect());

    private final ImageEffect spriteEffect;
    private final boolean showGradient;
    private final ImageEffect frameEffect;
}
