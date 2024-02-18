package com.github.milomarten.taisharangers.image.sources;

import com.github.milomarten.taisharangers.image.Color;

import java.util.OptionalInt;

/**
 * An abstraction of a read-only pixel field, for composing together.
 * Java's native implementations here are...clunky. BufferedImage comes in many different modes,
 * and there is no support for mathematically-created images, like gradients or solid color fields. This
 * abstraction allows me to mix both of them without the heavy overhead of using BufferedImages.
 */
public interface ImageSource {
    /**
     * Get the color at the specific position.
     * Implementors should be able to handle unexpected X/Y coordinates, such as out of bounds. Color.TRANSPARENT
     * is usually the best to return for out-of-bounds points.
     * @param x The horizontal X position
     * @param y The vertical Y position
     * @return The color at that position.
     */
    Color getPixel(int x, int y);

    /**
     * Get the width of this image.
     * This value returns optionally to support infinitely-sized images, such as solid color fields.
     * @return The width, or OptionalInt.empty if not applicable
     */
    OptionalInt getWidth();

    /**
     * Get the height of this image.
     * This value returns optionally to support infinitely-sized images, such as solid color fields.
     * @return The height, or OptionalInt.empty if not applicable
     */
    OptionalInt getHeight();

    /**
     * Check if a point is within the bounds of this image.
     * Infinite bounds are appropriately compensated for. It likely doesn't make sense to
     * overwrite this method, but this check is common enough to include it by default
     * @param x The horizontal X position
     * @param y The horizontal Y position
     * @return True if the point is within the image's bounds
     */
    default boolean isInBounds(int x, int y) {
        boolean inWidth = getWidth().stream().noneMatch(w -> x < 0 || x >= w);
        boolean inHeight = getHeight().stream().noneMatch(h -> y < 0 || y >= h);
        return inWidth && inHeight;
    }
}
