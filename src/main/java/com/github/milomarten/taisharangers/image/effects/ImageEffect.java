package com.github.milomarten.taisharangers.image.effects;

import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.image.sources.ImageSource;

public interface ImageEffect {
    Color getColor(ImageSource base, int x, int y);
}
