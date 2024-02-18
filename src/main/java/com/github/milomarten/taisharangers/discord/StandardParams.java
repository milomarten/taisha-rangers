package com.github.milomarten.taisharangers.discord;

import com.github.milomarten.taisharangers.models.PokemonSearchParams;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Common parameters and methods to extract them.
 */
@UtilityClass
public class StandardParams {
    public static final String SHARE_PARAMETER = "share";
    public static final String TYPE_PARAMETER = "type";
    public static final String ABILITY_PARAMETER = "ability";
    public static final String MIN_GENERATION_PARAMETER = "min-generation";
    public static final String MAX_GENERATION_PARAMETER = "max-generation";
    public static final String IS_EVOLVED_PARAMETER = "is-evolved";
    public static final String EVO_CHAIN_PARAMETER = "evo-chain";
    public static final String INCLUDE_FORMS_PARAMETER = "include-forms";

    /**
     * Create a "share" parameter.
     * If true, the response of this command should be visible to all. If false, the command
     * should be ephemeral, so the user only can view and dismiss it.
     * @return The created parameter.
     */
    public ApplicationCommandOptionData shareParameter() {
        return ApplicationCommandOptionData.builder()
                .name(SHARE_PARAMETER)
                .description("If true, output is visible to all. By default, will only be seen by you.")
                .type(ApplicationCommandOption.Type.BOOLEAN.getValue())
                .required(false)
                .build();
    }

    /**
     * Determine if the interaction should be shared or not.
     * @param event The event to parse from
     * @return True, if the output should be shared
     */
    public boolean isShare(ChatInputInteractionEvent event) {
        return event.getOption(SHARE_PARAMETER)
                .flatMap(a -> a.getValue())
                .map(a -> a.asBoolean())
                .orElse(false);
    }

    /**
     * Determine if the interaction should be shared or not.
     * @param subCommand The subcommand to parse from
     * @return True, if the output should be shared
     */
    public boolean isShare(ApplicationCommandInteractionOption subCommand) {
        return subCommand.getOption(SHARE_PARAMETER)
                .flatMap(a -> a.getValue())
                .map(a -> a.asBoolean())
                .orElse(false);
    }

    /**
     * Create all parameters for Pokemon Searching
     * @return All the options required for Pokemon searching
     */
    public List<ApplicationCommandOptionData> pokemonSearchParameters() {
        var opts = new ArrayList<ApplicationCommandOptionData>();
        opts.add(ApplicationCommandOptionData.builder()
                .name(TYPE_PARAMETER)
                .description("A type that the Pokemon has to be")
//                        .autocomplete(true)
                .type(ApplicationCommandOption.Type.STRING.getValue())
                .required(false)
                .build());
        opts.add(ApplicationCommandOptionData.builder()
                .name(ABILITY_PARAMETER)
                .description("An ability that the Pokemon must have. Note that this includes hidden abilities.")
                .type(ApplicationCommandOption.Type.STRING.getValue())
                .required(false)
                .build());
        opts.add(ApplicationCommandOptionData.builder()
                .name(MIN_GENERATION_PARAMETER)
                .description("The minimum generation this Pokemon must belong to.")
                .type(ApplicationCommandOption.Type.INTEGER.getValue())
                .required(false)
                .build());
        opts.add(ApplicationCommandOptionData.builder()
                .name(MAX_GENERATION_PARAMETER)
                .description("The maximum generation this Pokemon must belong to.")
                .type(ApplicationCommandOption.Type.INTEGER.getValue())
                .required(false)
                .build());
        opts.add(ApplicationCommandOptionData.builder()
                .name(IS_EVOLVED_PARAMETER)
                .description("Whether the Pokemon is evolved or not.")
                .type(ApplicationCommandOption.Type.BOOLEAN.getValue())
                .required(false)
                .build());
        opts.add(ApplicationCommandOptionData.builder()
                .name(EVO_CHAIN_PARAMETER)
                .description("The number of Pokemon in the evolution chain.")
                .type(ApplicationCommandOption.Type.INTEGER.getValue())
                .required(false)
                .build());
        opts.add(ApplicationCommandOptionData.builder()
                .name(INCLUDE_FORMS_PARAMETER)
                .description("If true, output will include forms, including Mega Evolutions, Gigantamax, and regionals.")
                .type(ApplicationCommandOption.Type.BOOLEAN.getValue())
                .required(false)
                .build());
        return opts;
    }

    /**
     * Parse the options from an interaction event into a PokemonSearchParams object
     * @param event The event to parse from
     * @return The parsed query
     */
    public PokemonSearchParams getSearchParams(ChatInputInteractionEvent event) {
        var params = PokemonSearchParams.builder()
                .includeUnusual(true);
        event.getOption(TYPE_PARAMETER).flatMap(a -> a.getValue()).map(a -> a.asString()).ifPresent(params::type);
        event.getOption(ABILITY_PARAMETER).flatMap(a -> a.getValue()).map(a -> a.asString()).ifPresent(params::ability);
        event.getOption(MIN_GENERATION_PARAMETER).flatMap(a -> a.getValue()).map(a -> a.asLong()).ifPresent(l -> params.minGeneration(l.intValue()));
        event.getOption(MAX_GENERATION_PARAMETER).flatMap(a -> a.getValue()).map(a -> a.asLong()).ifPresent(l -> params.maxGeneration(l.intValue()));
        event.getOption(IS_EVOLVED_PARAMETER).flatMap(a -> a.getValue()).map(a -> a.asBoolean()).ifPresent(params::isEvolved);
        event.getOption(EVO_CHAIN_PARAMETER).flatMap(a -> a.getValue()).map(a -> a.asLong()).ifPresent(l -> params.evolutionChain(l.intValue()));
        event.getOption(INCLUDE_FORMS_PARAMETER).flatMap(a -> a.getValue()).map(a -> a.asBoolean()).ifPresent(params::includeUnusual);

        return params.build();
    }

    /**
     * Parse the options from an interaction subcommand into a PokemonSearchParams object
     * @param event The event to parse from
     * @return The parsed query
     */
    public PokemonSearchParams getSearchParams(ApplicationCommandInteractionOption event) {
        var params = PokemonSearchParams.builder()
                .includeUnusual(true);
        event.getOption(TYPE_PARAMETER).flatMap(a -> a.getValue()).map(a -> a.asString()).ifPresent(params::type);
        event.getOption(ABILITY_PARAMETER).flatMap(a -> a.getValue()).map(a -> a.asString()).ifPresent(params::ability);
        event.getOption(MIN_GENERATION_PARAMETER).flatMap(a -> a.getValue()).map(a -> a.asLong()).ifPresent(l -> params.minGeneration(l.intValue()));
        event.getOption(MAX_GENERATION_PARAMETER).flatMap(a -> a.getValue()).map(a -> a.asLong()).ifPresent(l -> params.maxGeneration(l.intValue()));
        event.getOption(IS_EVOLVED_PARAMETER).flatMap(a -> a.getValue()).map(a -> a.asBoolean()).ifPresent(params::isEvolved);
        event.getOption(EVO_CHAIN_PARAMETER).flatMap(a -> a.getValue()).map(a -> a.asLong()).ifPresent(l -> params.evolutionChain(l.intValue()));
        event.getOption(INCLUDE_FORMS_PARAMETER).flatMap(a -> a.getValue()).map(a -> a.asBoolean()).ifPresent(params::includeUnusual);

        return params.build();
    }
}
