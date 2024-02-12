package com.github.milomarten.taisharangers.discord.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionReplyEditSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Color;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.NamedApiResource;
import skaro.pokeapi.resource.pokemon.Pokemon;
import skaro.pokeapi.resource.pokemon.PokemonAbility;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PokedexCommand implements Command {
    private final PokeApiClient client;

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
                .addOption(ApplicationCommandOptionData.builder()
                        .name("share")
                        .required(false)
                        .type(ApplicationCommandOption.Type.BOOLEAN.getValue())
                        .description("If true, output is visible to all. By default, will only be seen by you.")
                        .build()
                )
                .build();
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        var name = event.getOption("name")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString);
        var share = event.getOption("share")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asBoolean)
                .orElse(false);

        if (name.isEmpty()) {
            return event.reply()
                    .withEphemeral(true)
                    .withContent("Need a Pokemon's name or ID number!");
        }

        return event.deferReply().withEphemeral(!share)
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
                                    .addField("Abilities", formatAbilities(pkmn.getAbilities()), true)
                                    .addField("Egg Groups", formatMulti(species.getEggGroups(), NamedApiResource::getName), true)
                                    .addAllFields(makeStatFields(pkmn))
                                    .thumbnail(String.format("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/%d.png", pkmn.getId()))
                                    .color(extractColor(species.getColor().getName()))
                                    .build()).build());
                })
                .onErrorResume(t -> event.editReply("Error finding that Pokemon. Are you sure you spelled it right?"))
                .then();
    }

    private static <T> String formatMulti(List<T> list, Function<T, String> extract) {
        if (list.size() == 1) {
            return capitalize(extract.apply(list.get(0)));
        } else {
            return list.stream()
                    .map(extract)
                    .map(PokedexCommand::capitalize)
                    .collect(Collectors.joining(" / "));
        }
    }

    private static String formatAbilities(List<PokemonAbility> abilities) {
        // create a list of all regular and hidden abilities, removing any hidden abilities identical to normal ones.
        var regularandHidden = abilities.stream()
                .collect(Collectors.partitioningBy(PokemonAbility::getIsHidden,
                        Collectors.mapping(a -> capitalize(a.getAbility().getName()), Collectors.toList())));
        var normalizedList = new ArrayList<>(regularandHidden.get(false));
        regularandHidden.get(true)
                .forEach(a -> {
                    if (!normalizedList.contains(a)) {
                        normalizedList.add("*" + a + "*");
                    }
                });
        return formatMulti(normalizedList, Function.identity());
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
                .map(WordUtils::capitalize)
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
