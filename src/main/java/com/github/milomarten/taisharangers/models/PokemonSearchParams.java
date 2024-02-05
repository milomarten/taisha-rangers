package com.github.milomarten.taisharangers.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.OptionalInt;

@Data
@Builder
public class PokemonSearchParams {
    /**
     * Return a Pokemon that has one of these types.
     * Null or empty indicates indifference
     */
    private List<String> types;
    /**
     * Return a Pokemon that has one of these abilities.
     * Null or empty indicates indifference
     */
    private List<String> ability;
    /**
     * Return a legendary Pokemon.
     * null indicates indifference.
     */
    private Boolean legendary;
    /**
     * Return an evolved Pokemon.
     * null indicates indifference.
     */
    private Boolean isEvolved;
    /**
     * Return a Pokemon with at least this many species in the evolution chain.
     * As such, 1 will indicate a Pokemon that doesn't evolve, 2 indicates one evolution,
     * and 3 indicates two. Any other value indicates indifference.
     */
    private int evolutionChain;
    /**
     * Include unusual Pokemon in the response, such as mega evolutions or gigantamax forms.
     */
    private boolean includeUnusual;
    /**
     * Includes only Pokemon greater than or equal to this generation.
     */
    private Integer minGeneration;
    /**
     * Includes only Pokemon less than or equal to this generation.
     */
    private Integer maxGeneration;

    public OptionalInt getEvolutionChain() {
        return switch (this.evolutionChain) {
            case 1, 2, 3 -> OptionalInt.of(this.evolutionChain);
            default -> OptionalInt.empty();
        };
    }
}
