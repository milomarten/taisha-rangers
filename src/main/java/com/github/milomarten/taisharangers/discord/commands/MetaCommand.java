package com.github.milomarten.taisharangers.discord.commands;

import com.github.milomarten.taisharangers.discord.CommandPool;
import com.github.milomarten.taisharangers.discord.DiscordCommandService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.spec.InteractionReplyEditSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

@Component
@Slf4j
public class MetaCommand extends AsyncResponseMultiCommand implements SupportsAutocomplete {
    public static final String INIT_COMMAND = "init";
    public static final String UPDATE_COMMAND = "update";
    public static final String DELETE_COMMAND = "delete";
    public static final String PATCH_COMMAND = "patch";

    private final DiscordCommandService discordCommandService;

    private final CommandPool commandPool;

    public MetaCommand(@Lazy DiscordCommandService discordCommandService, @Lazy CommandPool commandPool) {
        super(new HashMap<>());
        this.discordCommandService = discordCommandService;
        this.commandPool = commandPool;

        this.branches.put(INIT_COMMAND, new SingleIdOperationBranch(discordCommandService::initializeCommand, "initialized"));
        this.branches.put(UPDATE_COMMAND, new SingleIdOperationBranch(discordCommandService::updateCommand, "updated"));
        this.branches.put(DELETE_COMMAND, new SingleIdOperationBranch(discordCommandService::deleteCommand, "deleted"));
        this.branches.put(PATCH_COMMAND, new PatchBranch());
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
                .addOption(ApplicationCommandOptionData.builder()
                        .name(PATCH_COMMAND)
                        .description("Update every command in Discord")
                        .type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
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
    public boolean supportsCommand(String commandName) {
        return commandName.equals(getName());
    }

    @Override
    public List<Choice> getCandidates(String paramName) {
        return commandPool.getCommands()
                .stream()
                .map(Command::getName)
                .map(Choice::fromString)
                .toList();
    }
    record SingleIdResponse(String id, boolean success) {}

    @RequiredArgsConstructor
    private static class SingleIdOperationBranch extends Branch<String, SingleIdResponse> {
        private final Function<String, Mono<Boolean>> operation;
        private final String verb;

        @Override
        protected Try<String> parseParameters(ChatInputInteractionEvent event, ApplicationCommandInteractionOption options) {
            return options.getOption("id")
                    .flatMap(a -> a.getValue())
                    .map(a -> a.asString())
                    .map(Try::success)
                    .orElseGet(() -> Try.failure("ID not present"));
        }

        @Override
        protected Mono<SingleIdResponse> doAsyncOperations(String parameters) {
            return operation.apply(parameters).map(b -> new SingleIdResponse(parameters, b));
        }

        @Override
        protected InteractionReplyEditSpec formatResponse(SingleIdResponse response) {
            return InteractionReplyEditSpec.builder()
                    .contentOrNull(response.success ?
                            String.format("Command `%s` was successfully %s.", response.id, this.verb) :
                            String.format("Command `%s` could not be %s.", response.id, this.verb))
                    .build();
        }

        @Override
        protected boolean isShare(ApplicationCommandInteractionOption options) {
            return false;
        }
    }

    private class PatchBranch extends Branch<Void, Void> {
        @Override
        protected Try<Void> parseParameters(ChatInputInteractionEvent event, ApplicationCommandInteractionOption options) {
            return Try.success(null);
        }

        @Override
        protected Mono<Void> doAsyncOperations(Void parameters) {
            return discordCommandService.patch();
        }

        @Override
        protected InteractionReplyEditSpec formatResponse(Void response) {
            return InteractionReplyEditSpec.builder().contentOrNull("Commands were patched.").build();
        }

        @Override
        protected boolean isShare(ApplicationCommandInteractionOption options) {
            return false;
        }
    }
}
