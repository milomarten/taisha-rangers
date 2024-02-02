package com.github.milomarten.taisharangers.services.query.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.milomarten.taisharangers.services.query.Where;
import lombok.Builder;

@Builder
public class PokemonWhere extends Where {
    @JsonProperty("pokemon_v2_pokemontypes")
    private PokemonByTypeWhere type;
}
