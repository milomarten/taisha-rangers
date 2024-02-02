package com.github.milomarten.taisharangers.models.graphql.query.domain;

import com.github.milomarten.taisharangers.models.graphql.query.Query;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class PokemonQuery extends Query<PokemonWhere> {
    @Override
    public String getLabel() {
        return "pokemon_v2_pokemon";
    }
}
