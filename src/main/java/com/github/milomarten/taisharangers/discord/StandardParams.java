package com.github.milomarten.taisharangers.discord;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

import static com.github.milomarten.taisharangers.discord.mapper.PokemonSearchParamsMapper.*;

@UtilityClass
public class StandardParams {
    public static final String SHARE_PARAMETER = "share";

    public ApplicationCommandOptionData shareParameter() {
        return ApplicationCommandOptionData.builder()
                .name(SHARE_PARAMETER)
                .description("If true, output is visible to all. By default, will only be seen by you.")
                .type(ApplicationCommandOption.Type.BOOLEAN.getValue())
                .required(false)
                .build();
    }

    public boolean isShare(ChatInputInteractionEvent event) {
        return event.getOption(SHARE_PARAMETER)
                .flatMap(a -> a.getValue())
                .map(a -> a.asBoolean())
                .orElse(false);
    }

    public boolean isShare(ApplicationCommandInteractionOption subCommand) {
        return subCommand.getOption(SHARE_PARAMETER)
                .flatMap(a -> a.getValue())
                .map(a -> a.asBoolean())
                .orElse(false);
    }

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
}
