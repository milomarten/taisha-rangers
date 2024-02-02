package com.github.milomarten.taisharangers.models.graphql.query.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.milomarten.taisharangers.models.graphql.query.Where;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class EvolutionChainWhere extends Where {
    @JsonProperty("pokemon_v2_pokemonspecies_aggregate")
    private PokemonBySpeciesAggregateWhere speciesAggregate;
}
