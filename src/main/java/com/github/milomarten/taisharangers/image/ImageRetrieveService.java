package com.github.milomarten.taisharangers.image;

import org.springframework.stereotype.Service;
import skaro.pokeapi.resource.pokemon.Pokemon;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

@Service
public class ImageRetrieveService {

    public BufferedImage get(Pokemon pkmn, Gender gender, boolean shiny) throws IOException {
        String url = gender.getSprite(pkmn.getSprites(), shiny);
        return ImageIO.read(new URL(url));
    }
}
