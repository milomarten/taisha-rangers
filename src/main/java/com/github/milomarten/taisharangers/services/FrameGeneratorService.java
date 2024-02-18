package com.github.milomarten.taisharangers.services;

import com.github.milomarten.taisharangers.image.sources.BufferedImageSource;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Factory which generates a token's frame (border)
 * Note that the frame is loaded from frame.png in the resources folder at runtime. Changing it will require
 * the app to be restarted.
 */
@Service
public class FrameGeneratorService {
    private BufferedImage frameTemplate;

    @PostConstruct
    private void setUp() throws IOException {
        var stream = new ClassPathResource("frame.png").getInputStream();
        frameTemplate = ImageIO.read(stream);
    }

    /**
     * Create a frame for a token.
     * @return The frame, wrapped in a BufferedImageSource for composition
     */
    public BufferedImageSource createFrame() {
        var copy = new BufferedImage(frameTemplate.getWidth(), frameTemplate.getHeight(), frameTemplate.getType());
        frameTemplate.copyData(copy.getRaster());
        return new BufferedImageSource(copy);
    }
}
