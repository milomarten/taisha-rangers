package com.github.milomarten.taisharangers.image.effects.gradient;

import com.github.milomarten.taisharangers.image.Color;

public enum BlankEffect implements GradientEffect {
    INSTANCE;

    @Override
    public Color getFirstColor(Color in) {
        return Color.TRANSPARENT;
    }

    @Override
    public Color getSecondColor(Color in) {
        return Color.TRANSPARENT;
    }
}
