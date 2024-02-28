package com.github.milomarten.taisharangers.image.effects;

import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.image.sources.ImageSource;
import lombok.Builder;
import lombok.Singular;

import java.util.Map;

@Builder
public class ColorMapEffect implements ImageEffect {
    @Singular private final Map<Color, Color> colorMappings;

    @Override
    public Color getColor(ImageSource base, int x, int y) {
        var baseColor = base.getPixel(x, y);
        return this.colorMappings.getOrDefault(baseColor, baseColor);
    }
}
