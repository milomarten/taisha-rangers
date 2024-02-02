package com.github.milomarten.taisharangers.services.query.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.milomarten.taisharangers.services.query.Operation;
import com.github.milomarten.taisharangers.services.query.Where;
import lombok.Builder;

@Builder
public class PokemonByTypeWhere extends Where {
    @JsonProperty("type_id")
    private Operation<Integer> typeId;
}
