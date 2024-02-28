package com.github.milomarten.taisharangers.image.effects;

import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.image.sources.ImageSource;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ShadowEffect implements ImageEffect {
    private static final Color BACKLIGHT_COLOR = new Color(210, 210, 210);
    private static final Color SHADOW_COLOR = new Color(30, 30, 30);

    private final Color backlightColor;
    private final Color shadowColor;

    public ShadowEffect() {
        this(BACKLIGHT_COLOR, SHADOW_COLOR);
    }

    @Override
    public Color getColor(ImageSource base, int x, int y) {
        var baseColor = base.getPixel(x, y);
        if (baseColor.alpha() == 0) {
            return baseColor;
        }

        for (int dX = -1; dX <= 1; dX++) {
            for (int dY = -1; dY <= 1; dY++) {
                int mX = x + dX;
                int mY = y + dY;
                if (base.isInBounds(mX, mY)) {
                    if (base.getPixel(mX, mY).alpha() == 0) {
                        return this.backlightColor;
                    } else {
                        return this.shadowColor;
                    }
                }
            }
        }
        return baseColor;
    }
}
