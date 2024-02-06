package com.github.milomarten.taisharangers.services;

import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.image.gradients.TypeGradient;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;
import skaro.pokeapi.resource.pokemon.Pokemon;

@Service
public class ColorGeneratorService {
    public Color getPrimaryForType(Pokemon pokemon) {
        var types = pokemon.getTypes();
        if (types.isEmpty()) { return Color.WHITE; }
        var opt = EnumUtils.getEnumIgnoreCase(TypeGradient.class, types.get(0).getType().getName());
        return opt.getLighter();
    }

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
