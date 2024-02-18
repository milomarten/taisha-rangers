package com.github.milomarten.taisharangers.image.sources;

import com.github.milomarten.taisharangers.image.Color;

/**
 * A subset of an ImageSource which also allows pixels to be overwritten.
 * Mathematically-defined ImageSources, like gradients, would typically *not*
 * implement this interface, since modification at this level would break their algorithms.
 * Thus, this interface is best for actual images, which just store each pixel in memory.
 */
public interface WriteableImageSource extends ImageSource {
    void setPixel(int x, int y, Color color);
}
