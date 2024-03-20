package com.github.milomarten.taisharangers.discord.commands;

import com.github.milomarten.taisharangers.discord.StandardParams;
import com.github.milomarten.taisharangers.discord.mapper.PokemonEmbedMapper;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.spec.InteractionReplyEditSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.pokemon.Pokemon;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;

@Component
@RequiredArgsConstructor
public class PokedexCommand extends AsyncResponseCommand<String, Tuple2<Pokemon, PokemonSpecies>> {
    private final PokeApiClient client;

    private final PokemonEmbedMapper embedMapper;

    @Override
    public String getName() {
        return "pokedex";
    }

    @Override
    public ApplicationCommandRequest getDiscordSpec() {
        return ApplicationCommandRequest.builder()
                .name("pokedex")
                .description("Look up any Pokemon in the Pokedex, powered by PokeAPI")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("name")
                        .required(true)
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .description("The Pokemon's name or Dex Number")
                        .build()
                )
                .addOption(StandardParams.shareParameter())
                .build();
    }

    @Override
    protected Try<String> parseParameters(ChatInputInteractionEvent event) {
        return event.getOption("name")
                .flatMap(a -> a.getValue())
                .map(a -> a.asString())
                .map(Try::success)
                .orElseGet(() -> Try.failure("Need a Pokemon's name or ID!"));
    }

    @Override
    protected Mono<Tuple2<Pokemon, PokemonSpecies>> doAsyncOperations(String parameters) {
        return client.getResource(Pokemon.class, parameters)
                .zipWhen(pkmn -> client.followResource(pkmn::getSpecies, PokemonSpecies.class));
    }

    @Override
    protected InteractionReplyEditSpec formatResponse(Tuple2<Pokemon, PokemonSpecies> response) {
        return InteractionReplyEditSpec.builder()
                .addEmbed(embedMapper.createEmbedForPokemon(response.getT1(), response.getT2()))
                .build();
    }

    @Override
    protected String formatErrorResponse(Throwable err) {
        return "Error finding that Pokemon. Are you sure you spelled it right?";
    }

    //    @Override
//    public Mono<Void> handle(ChatInputInteractionEvent event) {
//        var name = event.getOption("name")
//                .flatMap(ApplicationCommandInteractionOption::getValue)
//                .map(ApplicationCommandInteractionOptionValue::asString);
//        var share = event.getOption(SHARE_PARAMETER)
//                .flatMap(ApplicationCommandInteractionOption::getValue)
//                .map(ApplicationCommandInteractionOptionValue::asBoolean)
//                .orElse(false);
//
//        if (name.isEmpty()) {
//            return event.reply()
//                    .withEphemeral(true)
//                    .withContent("Need a Pokemon's name or ID number!");
//        }
//
//        return event.deferReply().withEphemeral(!share)
//                .then(client.getResource(Pokemon.class, name.get()))
//                .zipWhen(pkmn -> client.followResource(pkmn::getSpecies, PokemonSpecies.class))
//                .flatMap(tuple -> {
//                    var pkmn = tuple.getT1();
//                    var species = tuple.getT2();
//                    return event.editReply(InteractionReplyEditSpec.builder()
//                            .addEmbed(embedMapper.createEmbedForPokemon(pkmn, species)).build());
//                })
//                .onErrorResume(t -> event.editReply("Error finding that Pokemon. Are you sure you spelled it right?"))
//                .then();
//    }
}
