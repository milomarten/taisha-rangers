package com.github.milomarten.taisharangers.image.layers;

import com.github.milomarten.taisharangers.image.sources.ImageSource;
import lombok.RequiredArgsConstructor;

/**
 * A Mask which uses an ImageSource's alpha value to determine the render area.
 * Anywhere the ImageSource is fully transparent, pixels won't be rendered. Any semitransparent
 * or fully opaque pixels will render.
 */
@RequiredArgsConstructor
public class MaskFromImage implements Mask {
    private final ImageSource imageSource;

    @Override
    public boolean shouldRender(int x, int y) {
        return imageSource.getPixel(x, y).alpha() > 0;
    }
}
