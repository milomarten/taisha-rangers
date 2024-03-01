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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class MetaCommand extends AsyncResponseCommand<MetaCommand.Parameters, MetaCommand.Response> implements SupportsAutocomplete {
    public static final String INIT_COMMAND = "init";
    public static final String UPDATE_COMMAND = "update";
    public static final String DELETE_COMMAND = "delete";
    public static final String PATCH_COMMAND = "patch";

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
    protected boolean isEphemeral(ChatInputInteractionEvent event) {
        return true;
    }

    @Override
    protected Try<Parameters> parseParameters(ChatInputInteractionEvent event) {
        var options = event.getOptions();
        if (options.isEmpty()) {
            return Try.failure("Need root parameter??");
        }
        var op = options.get(0);

        var opMaybe = switch (op.getName()) {
            case INIT_COMMAND -> MetaType.INITIALIZE;
            case UPDATE_COMMAND -> MetaType.UPDATE;
            case DELETE_COMMAND -> MetaType.DELETE;
            case PATCH_COMMAND -> MetaType.PATCH;
            default -> null;
        };
        if (opMaybe == null) {
            return Try.failure("Need correct subcommand");
        }

        var idMaybe = op.getOption("id")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString);
        if (idMaybe.isEmpty() && opMaybe != MetaType.PATCH) {
            return Try.failure("Need ID");
        }
        return Try.success(new Parameters(opMaybe, idMaybe.orElse(null)));
    }

    @Override
    protected Mono<Response> doAsyncOperations(Parameters parameters) {
        var cmd = switch (parameters.type()) {
            case INITIALIZE -> discordCommandService.initializeCommand(parameters.id());
            case UPDATE -> discordCommandService.updateCommand(parameters.id());
            case DELETE -> discordCommandService.deleteCommand(parameters.id());
            case PATCH -> discordCommandService.patch().thenReturn(true);
        };
        return cmd.map(success -> new Response(parameters.type(), parameters.id(), success));
    }

    @Override
    protected InteractionReplyEditSpec formatResponse(Response response) {
        var verb = switch (response.type()) {
            case INITIALIZE -> "created";
            case UPDATE -> "updated";
            case DELETE -> "deleted";
            case PATCH -> "patched";
        };
        var string = response.success() ?
                String.format("Command `%s` was successfully %s!", response.id(), verb) :
                String.format("Command `%s` wasn't %s. Is the name correct?", response.id(), verb);
        return InteractionReplyEditSpec.builder().contentOrNull(string).build();
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

    record Parameters(MetaType type, String id) {}

    record Response(MetaType type, String id, boolean success) {}
}
enum MetaType {
    INITIALIZE,
    UPDATE,
    DELETE,
    PATCH
}
