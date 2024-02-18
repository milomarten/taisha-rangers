package com.github.milomarten.taisharangers.image.layers;

/**
 * A region where pixels should not be rendered
 */
public interface Mask {
    /**
     * Determine if this point should be rendered, or transparent
     * @param x The horizontal X position
     * @param y The vertical Y position
     * @return True, if the point should be rendered or not
     */
    boolean shouldRender(int x, int y);
}
