package com.github.milomarten.taisharangers.discord;

import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import lombok.experimental.UtilityClass;

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
}
