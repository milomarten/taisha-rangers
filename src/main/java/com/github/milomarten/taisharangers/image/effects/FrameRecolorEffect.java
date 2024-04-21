package com.github.milomarten.taisharangers.image.effects;

import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.image.sources.ImageSource;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public class FrameRecolorEffect implements ImageEffect {
    private static final Color FRAME_DEFAULT = new Color(38, 50, 56);
    private final Color frame;
    private final Color interior;

    @Override
    public Color getColor(ImageSource base, int x, int y) {
        Color toMap = base.getPixel(x, y);
        if (toMap.alpha() == 0) {
            return toMap;
        } else if (toMap.equals(FRAME_DEFAULT) && frame != null) {
            return frame;
        } else {
            return Objects.requireNonNullElse(interior, toMap);
        }
    }
}
