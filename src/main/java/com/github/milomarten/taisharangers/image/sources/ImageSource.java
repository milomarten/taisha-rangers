package com.github.milomarten.taisharangers.image.sources;

import com.github.milomarten.taisharangers.image.Color;

import java.util.OptionalInt;

public interface ImageSource {
    Color getPixel(int x, int y);

    OptionalInt getWidth();

    OptionalInt getHeight();

    default boolean isInBounds(int x, int y) {
        boolean inWidth = getWidth().stream().noneMatch(w -> x < 0 || x >= w);
        boolean inHeight = getHeight().stream().noneMatch(h -> y < 0 || y >= h);
        return inWidth && inHeight;
    }
}
