package com.github.milomarten.taisharangers.image.layers;

import com.github.milomarten.taisharangers.image.sources.ImageSource;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MaskFromImage implements Mask {
    private final ImageSource imageSource;

    @Override
    public boolean shouldRender(int x, int y) {
        return imageSource.getPixel(x, y).alpha() > 0;
    }
}
