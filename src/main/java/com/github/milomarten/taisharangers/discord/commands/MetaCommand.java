package com.github.milomarten.taisharangers.discord.commands;

import com.github.milomarten.taisharangers.discord.CommandPool;
import com.github.milomarten.taisharangers.discord.DiscordCommandService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class MetaCommand implements Command, SupportsAutocomplete {
    public static final String INIT_COMMAND = "init";
    public static final String UPDATE_COMMAND = "update";
    public static final String DELETE_COMMAND = "delete";
    private final DiscordCommandService discordCommandService;

    private final CommandPool commandPool;

    public MetaCommand(@Lazy DiscordCommandService discordCommandService, @Lazy CommandPool commandPool) {
        this.discordCommandService = discordCommandService;
        this.commandPool = commandPool;
    }

    @Override
    public String getName() {
        return "commands";
    }

    @Override
    public ApplicationCommandRequest getDiscordSpec() {
        return ApplicationCommandRequest.builder()
                .name(getName())
                .description("Command for syncing slash commands between code and Discord")
                .defaultMemberPermissions("0")
                .addOption(ApplicationCommandOptionData.builder()
                        .name(INIT_COMMAND)
                        .description("Create the command in Discord")
                        .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
                        .addOption(createIdOption(true))
                        .build())
                .addOption(ApplicationCommandOptionData.builder()
                        .name(UPDATE_COMMAND)
                        .description("Update the command in Discord")
                        .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
                        .addOption(createIdOption(true))
                        .build())
                .addOption(ApplicationCommandOptionData.builder()
                        .name(DELETE_COMMAND)
                        .description("Delete the command in Discord")
                        .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
                        .addOption(createIdOption(false))
                        .build())
                .build();
    }

    private ApplicationCommandOptionData createIdOption(boolean autocomplete) {
        return ApplicationCommandOptionData.builder()
                .name("id")
                .required(true)
                .description("The name of the command to work with")
                .type(ApplicationCommandOption.Type.STRING.getValue())
                .autocomplete(autocomplete)
                .build();
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        var options = event.getOptions();
        if (options.isEmpty()) {
            return event.reply("Need root parameter?").withEphemeral(true);
        }
        var op = options.get(0).getName();
        var idMaybe = options.get(0).getOption("id")
                .flatMap(a -> a.getValue())
                .map(a -> a.asString());
        if (idMaybe.isEmpty()) {
            return event.reply("Need id parameter?").withEphemeral(true);
        }

        var id = idMaybe.get();
        return switch (op) {
            case INIT_COMMAND -> makeReply(callBackend(
                    discordCommandService.initializeCommand(id),
                    "Command `" + id + " was created!",
                    "Command `" + id + "` was not able to be created. Is the name correct?",
                    "Encountered an error while creating. Check the logs."
            ), event);
            case UPDATE_COMMAND -> makeReply(callBackend(
                    discordCommandService.updateCommand(id),
                    "Command `" + id + "` was updated!",
                    "Command `" + id + "` was not able to be updated. Is the name correct?",
                    "Encountered an error while updating. Check the logs."
            ), event);
            case DELETE_COMMAND -> makeReply(callBackend(
                    discordCommandService.deleteCommand(id),
                    "Command `" + id + "` was deleted!",
                    "Command `" + id + "` was not able to be delted. Is the name correct?",
                    "Encountered an error while deleting. Check the logs."
            ), event);
            default -> event.reply(String.format("Unexpected subcommand. Expected one of: %s, %s, or %s", INIT_COMMAND, UPDATE_COMMAND, DELETE_COMMAND))
                    .withEphemeral(true);
        };
    }

    private Mono<String> callBackend(Mono<Boolean> result, String success, String neutral, String error) {
        return result.map(b -> b ? success : neutral)
                .onErrorResume(t -> {
                    log.error("Encountered error while adjusting command", t);
                    return Mono.just(error);
                });
    }

    private Mono<Void> makeReply(Mono<String> reply, ChatInputInteractionEvent event) {
        return event.deferReply()
                .withEphemeral(true)
                .then(reply)
                .flatMap(event::editReply)
                .then();
    }

    @Override
    public List<String> getCandidates(String paramName) {
        return commandPool.getCommands()
                .stream()
                .map(Command::getName)
                .toList();
    }
}
