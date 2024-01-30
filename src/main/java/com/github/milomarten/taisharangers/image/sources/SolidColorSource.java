package com.github.milomarten.taisharangers.image.sources;

import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.image.sources.ImageSource;
import lombok.RequiredArgsConstructor;

import java.util.OptionalInt;

@RequiredArgsConstructor
public
class SolidColorSource implements ImageSource {
    private final Color field;

    @Override
    public Color getPixel(int x, int y) {
        return field;
    }

    @Override
    public OptionalInt getWidth() {
        return OptionalInt.empty();
    }

    @Override
    public OptionalInt getHeight() {
        return OptionalInt.empty();
    }
}
