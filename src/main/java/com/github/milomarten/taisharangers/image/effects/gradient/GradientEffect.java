package com.github.milomarten.taisharangers.image.effects.gradient;

import com.github.milomarten.taisharangers.image.Color;

public interface GradientEffect {
    default void init(Color first, Color second) {}
    Color getFirstColor(Color in);
    Color getSecondColor(Color in);
}
