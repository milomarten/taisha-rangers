package com.github.milomarten.taisharangers.models;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PokemonSearchParamsMapper {
    public static final String TYPE_PARAMETER = "type";
    public static final String ABILITY_PARAMETER = "ability";
    public static final String MIN_GENERATION_PARAMETER = "min-generation";
    public static final String MAX_GENERATION_PARAMETER = "max-generation";
    public static final String IS_EVOLVED_PARAMETER = "is-evolved";
    public static final String EVO_CHAIN_PARAMETER = "evo-chain";
    public static final String INCLUDE_FORMS_PARAMETER = "include-forms";

    public List<ApplicationCommandOptionData> makeCommandOptionsForSearching() {
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
    
    public PokemonSearchParams fromChatInputInteractionEvent(ChatInputInteractionEvent event) {
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
