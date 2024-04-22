package com.github.milomarten.taisharangers.services;

import com.github.milomarten.taisharangers.image.effects.Faction;
import com.github.milomarten.taisharangers.image.sources.BufferedImageSource;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/**
 * Factory which generates a token's frame (border)
 * Note that the frame is loaded from frame.png in the resources folder at runtime. Changing it will require
 * the app to be restarted.
 */
@Service
@Slf4j
public class FrameGeneratorService {
    private Map<Faction, BufferedImage> factionFrames;

    @PostConstruct
    private void setUp() throws IOException {
        factionFrames = new EnumMap<>(Faction.class);
        for (var f : Faction.values()) {
            factionFrames.put(f, ImageIO.read(new ClassPathResource(f.getFilename()).getInputStream()));
        }
    }

    /**
     * Create a frame for a token.
     * @return The frame, wrapped in a BufferedImageSource for composition
     */
    public BufferedImageSource createFrame() {
        return createFrame(Faction.NONE);
    }

    /**
     * Create a frame for a token in a given faction
     * @param faction The faction to use
     * @return The generated frame, or a null if an error occured creating.
     */
    public BufferedImageSource createFrame(Faction faction) {
        var original = factionFrames.get(faction);
        var copy = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());
        original.copyData(copy.getRaster());
        return new BufferedImageSource(copy);
    }
}
