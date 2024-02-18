package com.github.milomarten.taisharangers.services;

import com.github.milomarten.taisharangers.models.PokemonSearchParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.pokemon.Pokemon;

import java.util.Random;
import java.util.random.RandomGenerator;

/**
 * A service built off of the PokemonQueryService to serve random Pokemon.
 */
@RequiredArgsConstructor
@Service
public class RandomPokemonService {
    private final PokemonQueryService queryService;
    private final PokeApiClient client;
    private final RandomGenerator rng = new Random();

    /**
     * Generate some quantity of random Pokemon matching a criteria
     * If qty is < 0, things might break idk.
     * Note that this method may return duplicates, particularly if qty is less than the actual
     * amount of Pokemon matching the query.
     * Due to caching, these responses will be faster over time. Running the same query multiple times
     * is also fast.
     * @param params The criteria to match
     * @param qty The number to generate.
     * @return A Flux which emits random Pokemon. The number of elements is equal to the qty.
     */
    public Flux<Pokemon> getRandomPokemon(PokemonSearchParams params, int qty) {
        return queryService.searchPokemon(params)
                .flatMapMany(i -> {
                    if (i.isEmpty()) {
                        return Flux.empty();
                    }
                    return Flux.range(0, qty)
                            .map(x -> i.get(rng.nextInt(0, i.size())))
                            .map(q -> q.id);
                })
                .flatMap(i -> client.getResource(Pokemon.class, String.valueOf(i)));
    }

    /**
     * Specialization of getRandomPokemon when only one is requested
     * @param params The criteria to match
     * @return A Mono which emits one random Pokemon.
     */
    public Mono<Pokemon> getRandomPokemon(PokemonSearchParams params) {
        return getRandomPokemon(params, 1)
                .next();
    }
}
