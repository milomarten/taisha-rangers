package com.github.milomarten.taisharangers.discord.commands;

import com.github.milomarten.taisharangers.discord.StandardParams;
import com.github.milomarten.taisharangers.models.PokemonSearchParams;
import com.github.milomarten.taisharangers.services.PokemonQueryService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.InteractionReplyEditSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchCommand extends AsyncResponseCommand<PokemonSearchParams, List<PokemonQueryService.QLResult>> {
    private final PokemonQueryService service;

    private static final int PAGE_SIZE = 10;

    @Override
    public String getName() {
        return "search";
    }

    @Override
    public ApplicationCommandRequest getDiscordSpec() {
        return ApplicationCommandRequest.builder()
                .name(getName())
                .description("Search for Pokemon matching the given criteria")
                .addAllOptions(StandardParams.pokemonSearchParameters())
                .addOption(StandardParams.shareParameter())
                .build();
    }

    @Override
    protected boolean isEphemeral(ChatInputInteractionEvent event) {
        return !StandardParams.isShare(event);
    }

    @Override
    protected Try<PokemonSearchParams> parseParameters(ChatInputInteractionEvent event) {
        return Try.success(StandardParams.getSearchParams(event));
    }

    @Override
    protected Mono<List<PokemonQueryService.QLResult>> doAsyncOperations(PokemonSearchParams parameters) {
        return service.searchPokemon(parameters);
    }

    @Override
    protected InteractionReplyEditSpec formatResponse(List<PokemonQueryService.QLResult> results) {
        var spec = InteractionReplyEditSpec.builder();
        if (results.size() > PAGE_SIZE) {
            spec.contentOrNull(String.format("%d Pokemon found. Since there are so many, results are attached in a file.", results.size()));
            var copy = String.join("\n", buildDisplayResults(results));
            spec.addFile("results.txt", new ByteArrayInputStream(copy.getBytes()));
        } else {
            var copy = String.format("%d Pokemon found.\n%s", results.size(), String.join("\n", buildDisplayResults(results)));
            spec.contentOrNull(copy);
        }
        return spec.build();
    }

    private static List<String> buildDisplayResults(List<PokemonQueryService.QLResult> results) {
        results.sort(Comparator.comparing(PokemonQueryService.QLResult::getId));
        List<Tuple2<Integer, String>> display = new ArrayList<>();
        for (var result : results) {
            display.add(Tuples.of(result.getId(), WordUtils.capitalize(result.getName(), ' ', '-')));
        }
        return display.stream()
                .map(tuple -> {
                    return String.format("* %03d - %s", tuple.getT1(), tuple.getT2());
                })
                .toList();
    }
}
