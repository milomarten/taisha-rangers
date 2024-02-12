package com.github.milomarten.taisharangers.image.layers;

import com.github.milomarten.taisharangers.image.BlendAlgorithm;
import com.github.milomarten.taisharangers.image.BlendMode;
import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.image.Point;
import com.github.milomarten.taisharangers.image.sources.ImageSource;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Layer {
    private final ImageSource image;
    @Builder.Default private BlendAlgorithm blendMode = BlendMode.NORMAL;
    @Builder.Default private Point offset = new Point(0, 0);
    @Builder.Default private double opacity = 1.0;

    private Mask mask;

    public Color getRawPixel(int x, int y) {
        int adjX = x - offset.x();
        int adjY = y - offset.y();
        if (image.isInBounds(adjX, adjY) && (mask == null || mask.shouldRender(adjX, adjY))) {
            var raw = image.getPixel(adjX, adjY);
            var computedOpacity = raw.alpha01() * opacity;
            return raw.withAlpha01(computedOpacity);
        } else {
            return Color.TRANSPARENT;
        }
    }
}
