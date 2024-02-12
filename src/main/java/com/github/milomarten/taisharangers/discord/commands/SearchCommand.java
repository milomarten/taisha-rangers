package com.github.milomarten.taisharangers.discord.commands;

import com.github.milomarten.taisharangers.models.PokemonSearchParams;
import com.github.milomarten.taisharangers.services.PokemonQueryService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.command.Interaction;
import discord4j.core.object.component.LayoutComponent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.spec.InteractionReplyEditSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SearchCommand implements Command {
    public static final String TYPE_PARAMETER = "type";
    public static final String ABILITY_PARAMETER = "ability";
    public static final String MIN_GENERATION_PARAMETER = "min-generation";
    public static final String MAX_GENERATION_PARAMETER = "max-generation";
    public static final String IS_EVOLVED_PARAMETER = "is-evolved";
    public static final String EVO_CHAIN_PARAMETER = "evo-chain";
    public static final String SHARE_PARAMETER = "share";
    public static final String INCLUDE_FORMS_PARAMETER = "include-forms";
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
                .addOption(ApplicationCommandOptionData.builder()
                        .name(TYPE_PARAMETER)
                        .description("A type that the Pokemon has to be")
//                        .autocomplete(true)
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .required(false)
                        .build())
                .addOption(ApplicationCommandOptionData.builder()
                        .name(ABILITY_PARAMETER)
                        .description("An ability that the Pokemon must have. Note that this includes hidden abilities.")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .required(false)
                        .build())
                .addOption(ApplicationCommandOptionData.builder()
                        .name(MIN_GENERATION_PARAMETER)
                        .description("The minimum generation this Pokemon must belong to.")
                        .type(ApplicationCommandOption.Type.INTEGER.getValue())
                        .required(false)
                        .build())
                .addOption(ApplicationCommandOptionData.builder()
                        .name(MAX_GENERATION_PARAMETER)
                        .description("The maximum generation this Pokemon must belong to.")
                        .type(ApplicationCommandOption.Type.INTEGER.getValue())
                        .required(false)
                        .build())
                .addOption(ApplicationCommandOptionData.builder()
                        .name(IS_EVOLVED_PARAMETER)
                        .description("Whether the Pokemon is evolved or not.")
                        .type(ApplicationCommandOption.Type.BOOLEAN.getValue())
                        .required(false)
                        .build())
                .addOption(ApplicationCommandOptionData.builder()
                        .name(EVO_CHAIN_PARAMETER)
                        .description("The number of Pokemon in the evolution chain.")
                        .type(ApplicationCommandOption.Type.INTEGER.getValue())
                        .required(false)
                        .build())
                .addOption(ApplicationCommandOptionData.builder()
                        .name(SHARE_PARAMETER)
                        .description("If true, output is visible to all. By default, will only be seen by you.")
                        .type(ApplicationCommandOption.Type.BOOLEAN.getValue())
                        .required(false)
                        .build())
                .addOption(ApplicationCommandOptionData.builder()
                        .name(INCLUDE_FORMS_PARAMETER)
                        .description("If true, output will include forms, including Mega Evolutions, Gigantamax, and regionals.")
                        .type(ApplicationCommandOption.Type.BOOLEAN.getValue())
                        .required(false)
                        .build()
                )
                .build();
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        var params = PokemonSearchParams.builder()
                        .includeUnusual(true);
        event.getOption(TYPE_PARAMETER).flatMap(a -> a.getValue()).map(a -> a.asString()).ifPresent(params::type);
        event.getOption(ABILITY_PARAMETER).flatMap(a -> a.getValue()).map(a -> a.asString()).ifPresent(params::ability);
        event.getOption(MIN_GENERATION_PARAMETER).flatMap(a -> a.getValue()).map(a -> a.asLong()).ifPresent(l -> params.minGeneration(l.intValue()));
        event.getOption(MAX_GENERATION_PARAMETER).flatMap(a -> a.getValue()).map(a -> a.asLong()).ifPresent(l -> params.maxGeneration(l.intValue()));
        event.getOption(IS_EVOLVED_PARAMETER).flatMap(a -> a.getValue()).map(a -> a.asBoolean()).ifPresent(params::isEvolved);
        event.getOption(EVO_CHAIN_PARAMETER).flatMap(a -> a.getValue()).map(a -> a.asLong()).ifPresent(l -> params.evolutionChain(l.intValue()));
        event.getOption(INCLUDE_FORMS_PARAMETER).flatMap(a -> a.getValue()).map(a -> a.asBoolean()).ifPresent(params::includeUnusual);

        var share = event.getOption(SHARE_PARAMETER)
                .flatMap(a -> a.getValue())
                .map(a -> a.asBoolean())
                .orElse(false);

        return event.deferReply()
                .withEphemeral(!share)
                .then(service.searchPokemon(params.build()))
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
