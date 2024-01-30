package com.github.milomarten.taisharangers.image.sources;

import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.image.ImageUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.image.BufferedImage;
import java.util.OptionalInt;

@AllArgsConstructor
public
class BufferedImageSource implements WriteableImageSource {
    @Getter private final BufferedImage wrapped;

    @Override
    public Color getPixel(int x, int y) {
        return isInBounds(x, y) ? new Color(wrapped.getRGB(x, y)) : Color.TRANSPARENT;
    }

    @Override
    public OptionalInt getWidth() {
        return OptionalInt.of(wrapped.getWidth());
    }

    @Override
    public OptionalInt getHeight() {
        return OptionalInt.of(wrapped.getHeight());
    }

    @Override
    public void setPixel(int x, int y, Color color) {
        if (isInBounds(x, y)) {
            wrapped.setRGB(x, y, color.rgba());
        }
    }
}
