package com.github.milomarten.taisharangers.image.layers;

import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.image.sources.ImageSource;
import lombok.RequiredArgsConstructor;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

@RequiredArgsConstructor
public class LayeredImage implements ImageSource {
    private final int width;
    private final int height;
    private final List<Layer> layers = new ArrayList<>();

    @Override
    public Color getPixel(int x, int y) {
        if (layers.isEmpty()) { return Color.TRANSPARENT; }
        Color top = layers.get(layers.size() - 1).getRawPixel(x, y);
        for (int i = layers.size() - 2; i >= 0; i--) {
            if (top.alpha01() == 1.0) {
                return top;
            }
            var thisLayer = layers.get(i);
            top = thisLayer.getBlendMode().blend(top, thisLayer.getRawPixel(x, y));
        }
        return top;
    }

    public void addLayer(Layer layer) {
        this.layers.add(layer);
    }

    public void addLayer(ImageSource image) {
        this.layers.add(Layer.builder().image(image).build());
    }

    @Override
    public OptionalInt getWidth() {
        return OptionalInt.of(width);
    }

    @Override
    public OptionalInt getHeight() {
        return OptionalInt.of(height);
    }

    public BufferedImage toImage() {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bi.setRGB(x, y, getPixel(x, y).rgba());
            }
        }
        return bi;
    }
}
