package com.github.milomarten.taisharangers.discord.commands;

import com.github.milomarten.taisharangers.models.PokemonSearchParamsMapper;
import com.github.milomarten.taisharangers.services.PokemonQueryService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.spec.InteractionReplyEditSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.ByteArrayInputStream;
import java.util.*;

@Component
@RequiredArgsConstructor
public class SearchCommand implements Command {
    private final PokemonQueryService service;

    private final PokemonSearchParamsMapper mapper;

    private static final int PAGE_SIZE = 10;

    public static final String SHARE_PARAMETER = "share";

    @Override
    public String getName() {
        return "search";
    }

    @Override
    public ApplicationCommandRequest getDiscordSpec() {
        return ApplicationCommandRequest.builder()
                .name(getName())
                .description("Search for Pokemon matching the given criteria")
                .addAllOptions(mapper.makeCommandOptionsForSearching())
                .addOption(ApplicationCommandOptionData.builder()
                        .name(SHARE_PARAMETER)
                        .description("If true, output is visible to all. By default, will only be seen by you.")
                        .type(ApplicationCommandOption.Type.BOOLEAN.getValue())
                        .required(false)
                        .build())
                .build();
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        var params = mapper.fromChatInputInteractionEvent(event);

        var share = event.getOption(SHARE_PARAMETER)
                .flatMap(a -> a.getValue())
                .map(a -> a.asBoolean())
                .orElse(false);

        return event.deferReply()
                .withEphemeral(!share)
                .then(service.searchPokemon(params))
                .flatMap(results -> event.editReply(formatResults(results)))
                .then();
    }

    private InteractionReplyEditSpec formatResults(List<PokemonQueryService.QLResult> results) {
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
