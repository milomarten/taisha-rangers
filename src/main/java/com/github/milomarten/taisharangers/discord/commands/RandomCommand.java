package com.github.milomarten.taisharangers.discord.commands;

import com.github.milomarten.taisharangers.discord.StandardParams;
import com.github.milomarten.taisharangers.discord.mapper.PokemonEmbedMapper;
import com.github.milomarten.taisharangers.models.PokemonSearchParams;
import com.github.milomarten.taisharangers.services.RandomPokemonService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.spec.InteractionReplyEditSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.pokemon.Pokemon;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class RandomCommand extends AsyncResponseCommand<RandomCommand.Parameters, List<Tuple2<Pokemon, PokemonSpecies>>> {
    private final RandomPokemonService randomPokemonService;

    private final PokeApiClient client;

    private final PokemonEmbedMapper embedMapper;

    public static final String COUNT_PARAM = "count";

    private static final int MAX_REQUEST = 20;

    @Override
    public String getName() {
        return "random";
    }

    @Override
    public ApplicationCommandRequest getDiscordSpec() {
        return ApplicationCommandRequest.builder()
                .name(getName())
                .description("Get one or more random items matching a specific set of criteria.")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("pokemon")
                        .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
                        .description("For selecting random Pokemon")
                        .addAllOptions(StandardParams.pokemonSearchParameters())
                        .addOption(StandardParams.shareParameter())
                        .addOption(ApplicationCommandOptionData.builder()
                                .name(COUNT_PARAM)
                                .description("The number of Pokemon that match. Default is 1")
                                .required(false)
                                .type(ApplicationCommandOption.Type.NUMBER.getValue())
                                .build())
                        .build()
                )
                .build();
    }

    @Override
    protected boolean isEphemeral(ChatInputInteractionEvent event) {
        return !StandardParams.isShare(event.getOption("pokemon").orElseThrow());
    }

    @Override
    protected Try<Parameters> parseParameters(ChatInputInteractionEvent event) {
        var pkmn = event.getOption("pokemon");
        if (pkmn.isEmpty()) {
            return Try.failure("Can only generate Pokemon.");
        }

        var options = pkmn.get();
        long count = options.getOption(COUNT_PARAM)
                .flatMap(a -> a.getValue())
                .map(a -> a.asLong())
                .orElse(1L);
        if (count <= 0) {
            return Try.failure("Count must be 1 or more!");
        } else if (count > MAX_REQUEST) {
            return Try.failure("Count must be 20 or less.");
        }
        var query = StandardParams.getSearchParams(options);

        return Try.success(new Parameters((int) count, query));
    }

    @Override
    protected Mono<List<Tuple2<Pokemon, PokemonSpecies>>> doAsyncOperations(Parameters parameters) {
        return randomPokemonService.getRandomPokemon(parameters.query(), parameters.count())
                .flatMap(p -> {
                    return client.followResource(p::getSpecies, PokemonSpecies.class)
                            .map(spec -> Tuples.of(p, spec));
                })
                .collectList();
    }

    @Override
    protected InteractionReplyEditSpec formatResponse(List<Tuple2<Pokemon, PokemonSpecies>> response) {
        if (response.isEmpty()) {
            return InteractionReplyEditSpec.builder().contentOrNull("No Pokemon found. Try a less restrictive query.")
                    .build();
        } else if (response.size() == 1) {
            var toEmbed = response.get(0);
            return InteractionReplyEditSpec.builder()
                    .contentOrNull("One fresh random Pokemon, coming up!")
                    .addEmbed(embedMapper.createEmbedForPokemon(toEmbed.getT1(), toEmbed.getT2()))
                    .build();
        } else {
            var results = response.stream()
                    .map(tuple -> PokemonEmbedMapper.getName(tuple.getT2()))
                    .map(s -> "* " + s)
                    .collect(Collectors.joining("\n", "Sure thing! Here are my selections:\n", ""));

            return InteractionReplyEditSpec.builder()
                    .contentOrNull(results)
                    .build();
        }
    }

    record Parameters(int count, PokemonSearchParams query) {}
}