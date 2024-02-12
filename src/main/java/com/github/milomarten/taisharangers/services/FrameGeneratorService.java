package com.github.milomarten.taisharangers.services;

import com.github.milomarten.taisharangers.image.sources.BufferedImageSource;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class FrameGeneratorService {
    private BufferedImage frameTemplate;

    @PostConstruct
    private void setUp() throws IOException {
        var stream = new ClassPathResource("frame.png").getInputStream();
        frameTemplate = ImageIO.read(stream);
    }

    public BufferedImageSource createFrame() {
        var copy = new BufferedImage(frameTemplate.getWidth(), frameTemplate.getHeight(), frameTemplate.getType());
        frameTemplate.copyData(copy.getRaster());
        return new BufferedImageSource(copy);
    }
}
