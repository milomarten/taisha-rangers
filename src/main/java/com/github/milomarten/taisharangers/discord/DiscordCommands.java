package com.github.milomarten.taisharangers.discord;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.command.ApplicationCommandEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.Embed;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionReplyEditSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.util.Color;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.NamedApiResource;
import skaro.pokeapi.resource.ability.Ability;
import skaro.pokeapi.resource.pokemon.Pokemon;
import skaro.pokeapi.resource.pokemon.PokemonAbility;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@ConditionalOnBean(GatewayDiscordClient.class)
public class DiscordCommands implements ApplicationRunner {
    private final GatewayDiscordClient gateway;

    private final PokeApiClient client;

    @PreDestroy
    private void spinDown() {
        gateway.logout().block();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        setupCommand();

        //Code goes here I suppose!
        gateway.on(ChatInputInteractionEvent.class, chat -> {
                    var name = chat.getCommandName();

                    if (name.equals("pokedex")) {
                        return pokedex(chat);
                    }
                    return chat.reply("No idea what that means").withEphemeral(true);
                })
                .onErrorResume(t -> {
                    t.printStackTrace();
                    return Mono.empty();
                })
                .subscribe();
    }

    private void setupCommand() {
        var appId = gateway.getRestClient().getApplicationId().block();

        var request = ApplicationCommandRequest.builder()
                .name("pokedex")
                .description("Look up any Pokemon in the Pokedex, powered by PokeAPI")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("name")
                        .required(true)
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .description("The Pokemon's name or Dex Number")
                        .build()
                )
                .build();

        gateway.getRestClient()
                .getApplicationService()
                .createGuildApplicationCommand(appId, 902681369405173840L, request)
                .block();
    }

    private Mono<Void> pokedex(ChatInputInteractionEvent event) {
        var name = event.getOption("name")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString);
        if (name.isEmpty()) {
            return event.reply()
                    .withEphemeral(true)
                    .withContent("Need a Pokemon's name or ID number!");
        }

        return event.deferReply()
                .then(client.getResource(Pokemon.class, name.get()))
                .zipWhen(pkmn -> client.followResource(pkmn::getSpecies, PokemonSpecies.class))
                .flatMap(tuple -> {
                    var pkmn = tuple.getT1();
                    var species = tuple.getT2();
                    return event.editReply(InteractionReplyEditSpec.builder()
                                    .addEmbed(EmbedCreateSpec.builder()
                                    .title(String.format("#%03d %s", pkmn.getId(), capitalize(pkmn.getName())))
                                    .description(getFlavorText(species))
                                    .author("PokéAPI", "https://pokeapi.co/", "https://pokeapi.co/static/pokeapi_256.3fa72200.png")
                                    .addField("Types", formatMulti(pkmn.getTypes(), t -> t.getType().getName()), true)
                                    .addField("Height", formatDecaUnits(pkmn.getHeight(), "m"), true)
                                    .addField("Weight", formatDecaUnits(pkmn.getWeight(), "kg"), true)
                                    .addField("Abilities", formatMulti(pkmn.getAbilities(), DiscordCommands::formatAbility), true)
                                    .addField("Egg Groups", formatMulti(species.getEggGroups(), NamedApiResource::getName), true)
                                    .addAllFields(makeStatFields(pkmn))
                                    .thumbnail(String.format("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/%d.png", pkmn.getId()))
                                    .color(extractColor(species.getColor().getName()))
                                    .build()).build());
                }).then();
    }

    private static <T> String formatMulti(List<T> list, Function<T, String> extract) {
        if (list.size() == 1) {
            return capitalize(extract.apply(list.get(0)));
        } else {
            return list.stream()
                    .map(extract)
                    .map(DiscordCommands::capitalize)
                    .collect(Collectors.joining(" / "));
        }
    }

    private static String formatAbility(PokemonAbility a) {
        return a.getIsHidden() ? String.format("*%s*", capitalize(a.getAbility().getName())) : a.getAbility().getName();
    }

    private static String formatDecaUnits(int height, String unit) {
        int whole = height / 10;
        int decimal = height % 10;
        return String.format("%d.%d %s", whole, decimal, unit);
    }

    private static List<EmbedCreateFields.Field> makeStatFields(Pokemon pkmn) {
        return pkmn.getStats().stream()
                .map(ps -> {
                    return EmbedCreateFields.Field.of(
                            capitalize(ps.getStat().getName()),
                            String.valueOf(ps.getBaseStat()),
                            false);
                })
                .collect(Collectors.toList());
    }

    private static String capitalize(String in) {
        return Arrays.stream(in.split("-"))
                .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
                .collect(Collectors.joining(" "));
    }

    private static String getFlavorText(PokemonSpecies species) {
        var pool = species.getFlavorTextEntries()
                .stream()
                .filter(ft -> "en".equals(ft.getLanguage().getName()))
                .toList();

        return pool.get(pool.size() - 1).getFlavorText();
    }

    private static Color extractColor(String name) {
        return switch (name) {
            case "black" -> Color.BLACK;
            case "blue" -> Color.BLUE;
            case "brown" -> Color.BROWN;
            case "gray" -> Color.GRAY;
            case "green" -> Color.GREEN;
            case "pink" -> Color.PINK;
            case "purple" -> Color.DEEP_LILAC;
            case "red" -> Color.RED;
            case "white" -> Color.WHITE;
            case "yellow" -> Color.YELLOW;
            default -> Color.ORANGE;
        };
    }
}
