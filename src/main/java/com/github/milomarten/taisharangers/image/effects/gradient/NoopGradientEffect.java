package com.github.milomarten.taisharangers.image.effects.gradient;

import com.github.milomarten.taisharangers.image.Color;

public enum NoopGradientEffect implements GradientEffect {
    INSTANCE;

    @Override
    public Color getFirstColor(Color in) {
        return in;
    }

    @Override
    public Color getSecondColor(Color in) {
        return in;
    }
}
