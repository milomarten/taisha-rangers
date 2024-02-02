package com.github.milomarten.taisharangers.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PokemonSearchParams {
    private List<String> types;
    private List<String> ability;
    private Boolean legendary;
    private Boolean isEvolved;
    private int evolutionChain;
}
