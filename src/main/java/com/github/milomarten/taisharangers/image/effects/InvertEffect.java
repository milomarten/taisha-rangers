package com.github.milomarten.taisharangers.image.effects;

import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.image.sources.ImageSource;

public class InvertEffect implements ImageEffect {
    @Override
    public Color getColor(ImageSource base, int x, int y) {
        return base.getPixel(x, y).invert();
    }
}
