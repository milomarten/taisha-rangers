package com.github.milomarten.taisharangers.services;

import com.github.milomarten.taisharangers.image.sources.BufferedImageSource;
import com.github.milomarten.taisharangers.models.Gender;
import org.springframework.stereotype.Service;
import skaro.pokeapi.resource.pokemon.Pokemon;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.net.URL;

/**
 * A service to generate a sprite image for a Pokemon.
 */
@Service
public class ImageRetrieveService {

    /**
     * Get the sprite for this Pokemon.
     * Note that this call does block, since it uses ImageIO.read. Perhaps in the future, I will wrap this in a
     * Mono to make this network call more clear.
     * @param pkmn The Pokemon to retrieve
     * @param gender The gender of the Pokemon
     * @param shiny Whether this Pokemon should be shown as shiny
     * @return The image, wrapped in a BufferedImageSource for composition
     * @throws IOException If the retrieval fails for some reason
     */
    public BufferedImageSource getSprite(Pokemon pkmn, Gender gender, boolean shiny) throws IOException {
        String url = gender.getSprite(pkmn.getSprites(), shiny);
        BufferedImage source = ImageIO.read(new URL(url));

        if (source.getColorModel() instanceof IndexColorModel icm) {
            // If the source image is indexed (PokeAPI returns this usually), attempts to set a pixel to a certain
            // RGBA value will instead pick a color "similar" to one already in the image. No good.

            // This will instead normalize to a standard RGBA format.
            return new BufferedImageSource(icm.convertToIntDiscrete(source.getRaster(), true));
        } else {
            return new BufferedImageSource(source);
        }
    }
}
