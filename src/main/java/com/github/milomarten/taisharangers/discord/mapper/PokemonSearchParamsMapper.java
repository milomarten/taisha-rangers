package com.github.milomarten.taisharangers.discord.mapper;

import com.github.milomarten.taisharangers.models.PokemonSearchParams;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
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

    public PokemonSearchParams fromSubGroup(ApplicationCommandInteractionOption event) {
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
