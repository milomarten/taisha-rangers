package com.github.milomarten.taisharangers.image.effects;

import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.models.graphql.operations.In;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Effects {
    NONE(new NoopEffect(), true, new NoopEffect()),
    SHADOW(new ShadowEffect(), false, new FrameRecolorEffect(Color.WHITE, new Color(30, 30, 30))),
    INVERT(new InvertEffect(), true, new InvertEffect()),
    POLICE(new NoopEffect(), true, new FrameRecolorEffect(new Color(64, 72, 204), null))
    ;

    private final ImageEffect spriteEffect;
    private final boolean showGradient;
    private final ImageEffect frameEffect;
}
