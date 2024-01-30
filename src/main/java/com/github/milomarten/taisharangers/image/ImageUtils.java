package com.github.milomarten.taisharangers.image;

import com.github.milomarten.taisharangers.image.sources.ImageSource;
import com.github.milomarten.taisharangers.image.sources.WriteableImageSource;
import lombok.experimental.UtilityClass;

import java.awt.image.BufferedImage;

@UtilityClass
public class ImageUtils {
    public void flatten(WriteableImageSource base, ImageSource top, BlendMode blend) {
        if (top == null) return;
        // Infinite width- and height- images are mathematical in nature, and could be composited.
        // But I don't want to do that right now.
        int width = base.getWidth().orElseThrow(() -> new IllegalArgumentException("Can't handle source with infinite width"));
        int height = base.getHeight().orElseThrow(() -> new IllegalArgumentException("Can't handle source with infinite height"));

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                var baseColor = base.getPixel(x, y);
                var topColor = top.getPixel(x, y);
                var newColor = blend.blend(topColor, baseColor);

                if (newColor != null) {
                    base.setPixel(x, y, newColor);
                }
            }
        }
    }
}
