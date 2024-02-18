package com.github.milomarten.taisharangers.discord.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

/**
 * An abstraction of a usable command in Discord
 * This interface is...not great. It is as low-level as possible, for identification purposes within
 * Spring. In particular, the name returned from getName() and the name returned in getDiscordSpec() may possibly
 * be different, which would break a lot of meta commands.
 */
public interface Command {
    /**
     * Get the name of this command, i.e., what users will use to call it
     * @return The command name
     */
    String getName();

    /**
     * Get the spec of this command, including name, description, and parameters
     * @return The spec to send to Discord to fully describe this command
     */
    default ApplicationCommandRequest getDiscordSpec() {
        return ApplicationCommandRequest.builder()
                .name(getName())
                .build();
    }

    /**
     * The code to execute when this command is retrieved.
     * @param event The event context
     * @return A Mono which signals when handling is finished.
     */
    Mono<Void> handle(ChatInputInteractionEvent event);
}
