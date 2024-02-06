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
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.pokemon.Pokemon;

import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PokemonHandler implements HandlerFunction<ServerResponse>  {
    private final RandomPokemonService randomPokemonService;

    private final PokeApiClient client;

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        var id = request.pathVariable("id");
        return ("RANDOM".equalsIgnoreCase(id) ? handleNoResponse(request) : client.getResource(Pokemon.class, id))
                .flatMap(pkmn -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(pkmn));
    }

    Mono<Pokemon> handleNoResponse(ServerRequest request) {
        var builder = PokemonSearchParams.builder()
                .types(request.queryParams().get("type"))
                .ability(request.queryParams().get("ability"));
        request.queryParam("legendary").map("true"::equals).ifPresent(builder::legendary);
        request.queryParam("evolved").map("true"::equals).ifPresent(builder::isEvolved);
        request.queryParam("evolutionchain").map(Integer::parseInt).ifPresent(builder::evolutionChain);
        request.queryParam("unusual").map("true"::equals).ifPresent(builder::includeUnusual);
        request.queryParam("mingen").map(Integer::parseInt).ifPresent(builder::minGeneration);
        request.queryParam("maxgen").map(Integer::parseInt).ifPresent(builder::maxGeneration);

        return randomPokemonService.getRandomPokemon(builder.build());
    }
}
