package com.github.milomarten.taisharangers.image.layers;

import com.github.milomarten.taisharangers.image.BlendAlgorithm;
import com.github.milomarten.taisharangers.image.BlendMode;
import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.image.Point;
import com.github.milomarten.taisharangers.image.effects.ImageEffect;
import com.github.milomarten.taisharangers.image.sources.ImageSource;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * One layer, which can be combined with others via a LayeredImage
 */
@Getter
@Setter
@Builder
public class Layer {
    private final ImageSource image;
    @Builder.Default private BlendAlgorithm blendMode = BlendMode.NORMAL;
    /**
     * An x/y delta which moves the image on this layer some pixels
     * Offset is relative to the top-left corner of the layer
     */
    @Builder.Default private Point offset = new Point(0, 0);
    /**
     * The layer's opacity. This is multiplied with the calculated Color to make the final opacity.
     */
    @Builder.Default private double opacity = 1.0;

    /**
     * If present, defines an area where pixels should not be rendered.
     * Areas outside the mask are immediately marked as transparent; the ImageSource is not even queried.
     */
    private Mask mask;

    /**
     * If present, defines a pixel-by-pixel effect to apply to the image.
     */
    private ImageEffect effect;

    /**
     * Get the color of this layer at a position, taking into account its various attributes.
     * @param x The horizontal X position
     * @param y The vertical Y position
     * @return The color at that point
     */
    public Color getRawPixel(int x, int y) {
        int adjX = x - offset.x();
        int adjY = y - offset.y();
        if (image.isInBounds(adjX, adjY) && (mask == null || mask.shouldRender(adjX, adjY))) {
            var raw = effect == null ? image.getPixel(adjX, adjY) : effect.getColor(image, adjX, adjY);
            var computedOpacity = raw.alpha01() * opacity;
            return raw.withAlpha01(computedOpacity);
        } else {
            return Color.TRANSPARENT;
        }
    }
}
