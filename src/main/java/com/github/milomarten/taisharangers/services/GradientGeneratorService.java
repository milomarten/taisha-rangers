package com.github.milomarten.taisharangers.services;

import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.image.gradients.TypeGradient;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;
import skaro.pokeapi.resource.pokemon.Pokemon;

@Service
public class GradientGeneratorService {
    public Result byType(Pokemon pokemon) {
        var types = pokemon.getTypes();
        if (types.size() == 0) {
            return new Result(Color.TRANSPARENT, Color.TRANSPARENT);
        }
        else if (types.size() == 1) {
            var monotype = EnumUtils.getEnumIgnoreCase(TypeGradient.class, types.get(0).getType().getName());
            return new Result(monotype.getLighter(), monotype.getDarker());
        } else {
            var t1 = EnumUtils.getEnumIgnoreCase(TypeGradient.class, types.get(0).getType().getName());
            var t2 = EnumUtils.getEnumIgnoreCase(TypeGradient.class, types.get(1).getType().getName());
            return new Result(t1.getLighter(), t2.getDarker());
        }
    }

    public record Result(Color start, Color end) {}
}
