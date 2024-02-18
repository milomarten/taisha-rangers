package com.github.milomarten.taisharangers.image;

/**
 * An algorithm to determine how two colors are mixed.
 */
public interface BlendAlgorithm {
    /**
     * Blend the two colors.
     * @param top The color on the upper layer
     * @param bottom The color on the lower layer
     * @return The mixed color
     */
    Color blend(Color top, Color bottom);
}
