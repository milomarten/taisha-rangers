package com.github.milomarten.taisharangers.models.graphql.query.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.milomarten.taisharangers.models.graphql.operations.Operation;
import com.github.milomarten.taisharangers.models.graphql.query.TypeWhere;
import com.github.milomarten.taisharangers.models.graphql.query.Where;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class PokemonByTypeWhere extends Where {
    @JsonProperty("type_id")
    private Operation<Integer> typeId;

    @JsonProperty("pokemon_v2_type")
    private TypeWhere type;
}
