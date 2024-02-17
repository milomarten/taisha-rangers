package com.github.milomarten.taisharangers.discord;

import com.github.milomarten.taisharangers.discord.commands.SupportsAutocomplete;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.Name;
import skaro.pokeapi.resource.NamedApiResourceList;
import skaro.pokeapi.resource.type.Type;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.milomarten.taisharangers.discord.mapper.PokemonSearchParamsMapper.TYPE_PARAMETER;

//@Service
@RequiredArgsConstructor
@Slf4j
public class PokemonDataAutocompleteHandler implements SupportsAutocomplete {
    private Map<String, Type> typeMap;

    private final PokeApiClient client;

    @PostConstruct
    private void setUp() {
        Mono.defer(this::populateTypeMap)
                .retryWhen(Retry.backoff(3, Duration.ofMinutes(1)))
                .subscribe();
    }

    private Mono<Void> populateTypeMap() {
        return client.getResource(Type.class)
                .flatMapIterable(NamedApiResourceList::getResults)
                .flatMap(nar -> client.followResource(() -> nar, Type.class))
                .collect(Collectors.toMap(Type::getName, t -> t))
                .doOnSuccess(t -> {
                    log.info("Populated type table with {} types", t.size());
                    this.typeMap = t;
                })
                .doOnError(t -> log.error("Unable to populate type table", t))
                .then();
    }

    @Override
    public boolean supportsCommand(String commandName) {
        return "search".equals(commandName);
    }

    @Override
    public List<Choice> getCandidates(String paramName) {
        return switch (paramName) {
            case TYPE_PARAMETER -> this.typeMap == null ? null :
                this.typeMap.values().stream().map(this::getChoiceForType).toList();
            default -> null;
        };
    }

    private Choice getChoiceForType(Type type) {
        String englishName = type.getNames()
                .stream()
                .filter(n -> "en".equals(n.getLanguage().getName()))
                .findFirst()
                .map(Name::getName)
                .orElse(type.getName());
        return new Choice(type.getName(), englishName);
    }
}
