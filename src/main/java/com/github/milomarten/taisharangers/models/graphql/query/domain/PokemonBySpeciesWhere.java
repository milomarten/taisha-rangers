package com.github.milomarten.taisharangers.models.graphql.query.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.milomarten.taisharangers.models.graphql.operations.Operation;
import com.github.milomarten.taisharangers.models.graphql.query.Where;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter @Setter
public class PokemonBySpeciesWhere extends Where {
    @JsonProperty("is_baby")
    private Operation<Boolean> isBaby;

    @JsonProperty("is_legendary")
    private Operation<Boolean> isLegendary;

    @JsonProperty("is_mythical")
    private Operation<Boolean> isMythical;

    @JsonProperty("evolves_from_species_id")
    private Operation<Integer> evolvesFromSpeciesId;

    @JsonProperty("pokemon_v2_evolutionchain")
    private EvolutionChainWhere evolutionChain;

    @JsonProperty("generation_id")
    private Operation<Integer> generationId;
}
