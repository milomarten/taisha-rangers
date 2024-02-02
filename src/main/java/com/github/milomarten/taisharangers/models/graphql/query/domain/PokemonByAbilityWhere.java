package com.github.milomarten.taisharangers.models.graphql.query.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.milomarten.taisharangers.models.graphql.operations.Operation;
import com.github.milomarten.taisharangers.models.graphql.query.Where;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class PokemonByAbilityWhere extends Where {
    @JsonProperty("is_hidden")
    private Operation<Boolean> isHidden;

    @JsonProperty("pokemon_v2_ability")
    private AbilityWhere ability;
}
