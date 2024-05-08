package com.github.milomarten.taisharangers.image.effects;

import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.image.effects.gradient.GradientEffect;
import com.github.milomarten.taisharangers.image.sources.ImageSource;

public class GrayscaleEffect implements ImageEffect, GradientEffect {
    private static final double RED_WEIGHT = 0.2126;
    private static final double GREEN_WEIGHT = 0.7152;
    private static final double BLUE_WEIGHT = 0.0722;
    private static final double TOTAL = RED_WEIGHT + GREEN_WEIGHT + BLUE_WEIGHT;

    @Override
    public Color getColor(ImageSource base, int x, int y) {
        return grayscaleify(base.getPixel(x, y));
    }

    private Color grayscaleify(Color in) {
        var components = in.components();
        var red = components[0] / 255.0;
        var green = components[1] / 255.0;
        var blue = components[2] / 255.0;

        var composite = ((red * RED_WEIGHT) + (blue * BLUE_WEIGHT) + (green * GREEN_WEIGHT)) / TOTAL;
        return new Color(composite, composite, composite, components[3] / 255.0);
    }

    @Override
    public Color getFirstColor(Color in) {
        return grayscaleify(in);
    }

    @Override
    public Color getSecondColor(Color in) {
        return grayscaleify(in);
    }
}
