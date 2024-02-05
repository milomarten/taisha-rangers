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

@RequiredArgsConstructor
@Service
public class RandomPokemonService {
    private final PokemonQueryService queryService;
    private final PokeApiClient client;
    private final RandomGenerator rng = new Random();

    public Flux<Pokemon> getRandomPokemon(PokemonSearchParams params, int qty) {
        return queryService.searchPokemon(params)
                .flatMapMany(i -> {
                    if (i.isEmpty()) {
                        return Flux.empty();
                    }
                    return Flux.range(0, qty)
                            .map(x -> i.get(rng.nextInt(0, i.size())));
                })
                .flatMap(i -> client.getResource(Pokemon.class, String.valueOf(i)));
    }

    public Mono<Pokemon> getRandomPokemon(PokemonSearchParams params) {
        return getRandomPokemon(params, 1)
                .next();
    }
}
