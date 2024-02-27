package com.github.milomarten.taisharangers.image.layers;

import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.image.sources.ImageSource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

/**
 * An image composed of zero or more layers.
 * Each layer can be moved and shifted at runtime, independently of the others. Dimensions can
 * also be adjusted easily. A final image is only
 * rendered when toImage() is called.
 */
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
            top = layers.get(i + 1).getBlendMode().blend(top, thisLayer.getRawPixel(x, y));
        }
        return top;
    }

    /**
     * Add a layer to the top of the stack
     * @param layer The layer to add
     */
    public void addLayer(Layer layer) {
        this.layers.add(layer);
    }

    /**
     * Convenience method to add an ImageSource to the top of the layer stack.
     * All other layer attributes are set to default
     * @param image The image to add
     */
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

    /**
     * Render this LayeredImage into a final BufferedImage.
     * Each layer uses the above layer's blending mode, the above layer's
     * pixel, and the layer's pixel, to calculate its color. This calculation goes from
     * top to bottom, until one final color is left.
     * @return The rendered image.
     */
    public BufferedImage toImage() {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bi.setRGB(x, y, getPixel(x, y).rgba());
            }
        }
        return bi;
    }

    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(toImage(), "png", os);
        return os.toByteArray();
    }

    public OutputStream toStream() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(toImage(), "png", os);
        return os;
    }
}
