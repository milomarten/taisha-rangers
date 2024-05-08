package com.github.milomarten.taisharangers.image.effects.gradient;

import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.image.effects.ImageEffect;
import com.github.milomarten.taisharangers.image.sources.ImageSource;

public class SicknessEffect implements ImageEffect, GradientEffect {
    private static final double K = 0.4;

    private Color first;
    private Color second;

    @Override
    public void init(Color first, Color second) {
        this.first = second;
        this.second = first;
    }

    @Override
    public Color getFirstColor(Color in) {
        return desaturate(this.first);
    }

    @Override
    public Color getSecondColor(Color in) {
        return desaturate(this.second);
    }

    private Color desaturate(Color in) {
        var components = in.components();
        var red = components[0] / 255.0;
        var green = components[1] / 255.0;
        var blue = components[2] / 255.0;
        var alpha = components[3] / 255.0;

        var intensity = (0.3 * red) + (0.59 * green) + (0.11 * blue);
        var intensityK = intensity * K;
        var newRed = intensityK + (red * (1 - K));
        var newGreen = intensityK + (green * (1 - K));
        var newBlue = intensityK + (blue * (1 - K));

        return new Color(newRed, newGreen, newBlue, alpha);
    }

    @Override
    public Color getColor(ImageSource base, int x, int y) {
        return desaturate(base.getPixel(x, y));
    }
}
