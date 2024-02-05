package com.github.milomarten.taisharangers.endpoints;

import com.github.milomarten.taisharangers.models.PokemonSearchParams;
import com.github.milomarten.taisharangers.services.RandomPokemonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import skaro.pokeapi.resource.pokemon.Pokemon;

@Component
@RequiredArgsConstructor
public class RandomHandler implements HandlerFunction<ServerResponse>  {
    private final RandomPokemonService randomPokemonService;

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        return handleNoResponse(request)
                .flatMap(pkmn -> ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(pkmn));
    }

    Mono<Pokemon> handleNoResponse(ServerRequest request) {
        return randomPokemonService.getRandomPokemon(PokemonSearchParams.builder().build());
    }
}
