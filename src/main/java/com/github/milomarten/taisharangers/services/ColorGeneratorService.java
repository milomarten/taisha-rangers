package com.github.milomarten.taisharangers.services;

import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.image.gradients.TypeGradient;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;
import skaro.pokeapi.resource.pokemon.Pokemon;

/**
 * Create colors based on a Pokemon's attributes
 * For type-based colors, there are two: a less saturated one, and a more saturated one. Whichever one
 * is used depends on the method in question, and the type combinations.
 * As a fallback, a typeless Pokemon would return Color.WHITE for either of these methods.
 */
@Service
public class ColorGeneratorService {
    /**
     * Get the color based on a Pokemon's primary type.
     * This is equal to the desaturated color associated with the first type.
     * @param pokemon The Pokemon to retrieve for
     * @return The calculated color
     */
    public Color getPrimaryForType(Pokemon pokemon) {
        var types = pokemon.getTypes();
        if (types.isEmpty()) { return Color.WHITE; }
        var opt = EnumUtils.getEnumIgnoreCase(TypeGradient.class, types.get(0).getType().getName());
        return opt.getLighter();
    }

    /**
     * Get the color based on a Pokemon's secondary type.
     * For Pokemon with only one type, this returns the more saturated color associated with the first type.
     * For Pokemon with two types, this returns the more saturated color associated with the secondary type.
     * @param pokemon The Pokemon to retrieve for
     * @return The calculated color.
     */
    public Color getSecondaryForType(Pokemon pokemon) {
        var types = pokemon.getTypes();
        if (types.isEmpty()) { return Color.WHITE; }
        if (types.size() == 1) {
            var opt = EnumUtils.getEnumIgnoreCase(TypeGradient.class, pokemon.getTypes().get(0).getType().getName());
            return opt.getDarker();
        } else {
            var opt = EnumUtils.getEnumIgnoreCase(TypeGradient.class, pokemon.getTypes().get(1).getType().getName());
            return opt.getDarker();
        }
    }
}
