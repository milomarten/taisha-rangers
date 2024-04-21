package com.github.milomarten.taisharangers.image.layers;

import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.image.sources.ImageSource;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FrameMask implements Mask {
    private static final Color FRAME_DEFAULT = new Color(38, 50, 56);
    private final ImageSource imageSource;

    @Override
    public boolean shouldRender(int x, int y) {
        var color = imageSource.getPixel(x, y);
        return color.alpha() != 0 && !color.equals(FRAME_DEFAULT);
    }
}
