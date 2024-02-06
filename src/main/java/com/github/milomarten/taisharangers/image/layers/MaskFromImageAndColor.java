package com.github.milomarten.taisharangers.image.layers;

import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.image.sources.ImageSource;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MaskFromImageAndColor implements Mask {
    private final ImageSource image;
    private final Color allow;

    @Override
    public boolean shouldRender(int x, int y) {
        return image.isInBounds(x, y) && image.getPixel(x, y).equals(allow);
    }
}
