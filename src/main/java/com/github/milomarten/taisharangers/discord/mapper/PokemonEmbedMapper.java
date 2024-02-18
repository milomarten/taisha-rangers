package com.github.milomarten.taisharangers.discord.mapper;

import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Service;
import skaro.pokeapi.resource.Name;
import skaro.pokeapi.resource.NamedApiResource;
import skaro.pokeapi.resource.pokemon.Pokemon;
import skaro.pokeapi.resource.pokemon.PokemonAbility;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpeciesVariety;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

/**
 * Mapper which generates a standard Discord embed for a Pokemon.
 */
@Service
public class PokemonEmbedMapper {
    /**
     * Create an Embed for the provided Pokemon species
     * @param pkmn The Pokemon object from PokeAPI
     * @param species The Species object from PokeAPI
     * @return The EmbedSpec to be put in bot responses.
     */
    public EmbedCreateSpec createEmbedForPokemon(Pokemon pkmn, PokemonSpecies species) {
        return EmbedCreateSpec.builder()
                .title(String.format("#%03d %s", pkmn.getId(), getName(species)))
                .description(getFlavorText(pkmn.getName(), species))
                .author("PokéAPI", "https://pokeapi.co/", "https://pokeapi.co/static/pokeapi_256.3fa72200.png")
                .addField("Types", formatMulti(pkmn.getTypes(), t -> t.getType().getName()), true)
                .addField("Height", formatDecaUnits(pkmn.getHeight(), PokemonEmbedMapper.Unit.METERS), true)
                .addField("Weight", formatDecaUnits(pkmn.getWeight(), PokemonEmbedMapper.Unit.KILOGRAMS), true)
                .addField("Abilities", formatAbilities(pkmn.getAbilities()), true)
                .addField("Egg Groups", formatMulti(species.getEggGroups(), NamedApiResource::getName), true)
                .addAllFields(makeStatFields(pkmn))
                .thumbnail(String.format("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/%d.png", pkmn.getId()))
                .color(extractColor(species.getColor().getName()))
                .url(String.format("https://pokeapi.co/api/v2/pokemon/%d", pkmn.getId()))
                .build();
    }

    /**
     * Get the (English) name of the Pokemon Species provided.
     * @param species The species object from PokeAPI
     * @return The name.
     */
    public static String getName(PokemonSpecies species) {
        return species.getNames()
                .stream()
                .filter(n -> "en".equals(n.getLanguage().getName()))
                .findFirst()
                .map(Name::getName)
                .orElseGet(() -> StringUtils.capitalize(species.getName()));
    }

    private static <T> String formatMulti(List<T> list, Function<T, String> extract) {
        if (list.size() == 1) {
            return capitalize(extract.apply(list.get(0)));
        } else {
            return list.stream()
                    .map(extract)
                    .map(PokemonEmbedMapper::capitalize)
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

    private static String formatDecaUnits(int deca, PokemonEmbedMapper.Unit unit) {
        return String.format("%2.1f %s (%s)", deca / 10.0, unit.symbol, unit.toImperial(deca));
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

    private static String getFlavorText(String name, PokemonSpecies species) {
        var pool = species.getFlavorTextEntries()
                .stream()
                .filter(ft -> "en".equals(ft.getLanguage().getName()))
                .toList();

        var text = new StringBuilder(pool.get(pool.size() - 1).getFlavorText());
        if (species.getVarieties().size() > 1) {
            var partitioned = species.getVarieties()
                    .stream()
                    .filter(variety -> !StringUtils.equals(name, variety.getPokemon().getName()))
                    .collect(Collectors.partitioningBy(PokemonSpeciesVariety::getIsDefault));

            text.append("\n\n");
            if (partitioned.get(true).size() > 0) {
                        text.append("This is a Pokemon variant. To view the base form, search `")
                        .append(partitioned.get(true).get(0).getPokemon().getName())
                        .append("`. ");
            }
            if (partitioned.get(false).size() > 0) {
                        text.append("This Pokemon has other forms. To view them, search one of: ")
                        .append(partitioned.get(false).stream()
                                .map(v -> "`" + v.getPokemon().getName() + "`")
                                .collect(Collectors.joining(", ", "", ".")));
            }
        }
        return text.toString();
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

    @RequiredArgsConstructor
    private enum Unit {
        METERS("m", i -> {
            var inches = i * 4; // True factor is 3.937
            return String.format("%d' %d\"", inches / 12, inches % 12);
        }),
        KILOGRAMS("kg", i -> {
            var pounds = i * 0.2204; // True pounds, rather than going through some other unit. More accurate
            return String.format("%2.1f lbs", pounds);
        })
        ;
        private final String symbol;
        private final IntFunction<String> converted;

        public String toImperial(int deca) {
            return this.converted.apply(deca);
        }
    }
}
