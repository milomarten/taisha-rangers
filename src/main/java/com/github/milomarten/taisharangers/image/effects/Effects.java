package com.github.milomarten.taisharangers.image.effects;

import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.image.effects.gradient.BlankEffect;
import com.github.milomarten.taisharangers.image.effects.gradient.GradientEffect;
import com.github.milomarten.taisharangers.image.effects.gradient.NoopGradientEffect;
import com.github.milomarten.taisharangers.image.effects.gradient.SicknessEffect;
import com.github.milomarten.taisharangers.image.sources.GradientSource;
import com.github.milomarten.taisharangers.models.graphql.operations.In;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import skaro.pokeapi.resource.pokemon.Pokemon;

@RequiredArgsConstructor
@Getter
public enum Effects {
    NONE(new NoopEffect(), NoopGradientEffect.INSTANCE, new NoopEffect()),
    SHADOW(new ShadowEffect(), BlankEffect.INSTANCE, new FrameRecolorEffect(Color.WHITE, new Color(30, 30, 30))),
    INVERT(new InvertEffect(), NoopGradientEffect.INSTANCE, new InvertEffect()),
    CORRUPTED(new GrayscaleEffect(), new SicknessEffect(), new GrayscaleEffect());
    ;

    private final ImageEffect spriteEffect;
    private final GradientEffect showGradient;
    private final ImageEffect frameEffect;
}
