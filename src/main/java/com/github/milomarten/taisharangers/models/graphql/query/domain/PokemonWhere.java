package com.github.milomarten.taisharangers.models.graphql.query.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.milomarten.taisharangers.models.graphql.operations.Operation;
import com.github.milomarten.taisharangers.models.graphql.query.Where;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class PokemonWhere extends Where {
    @JsonProperty("is_default")
    private Operation<Boolean> isDefault;

    @JsonProperty("pokemon_v2_pokemontypes")
    private PokemonByTypeWhere type;

    @JsonProperty("pokemon_v2_pokemonabilities")
    private PokemonByAbilityWhere ability;

    @JsonProperty("pokemon_v2_pokemonspecy")
    private PokemonBySpeciesWhere specy;
}
