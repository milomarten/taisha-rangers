package com.github.milomarten.taisharangers.discord;

import discord4j.core.GatewayDiscordClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * The service responsible for syncing the commands defined in code with Discord's repository.
 * Slash commands must be registered in Discord manually, and only through their API. This service attempts to
 * align the two.
 * Currently, all methods only act on one command at a time, and require the user to know if the command is already
 * known to Discord or not. In the future, bulk methods should be supported, as well as possibly a global 'sync' command
 * that does everything automatically. May also be a 'promote' method, to make a command available globally.
 */
@Component
@ConditionalOnBean(GatewayDiscordClient.class)
@RequiredArgsConstructor
public class DiscordCommandService {
    private static final long GUILD_ID = 902681369405173840L; // The D&D server

    private final GatewayDiscordClient gateway;
    private final CommandPool commands;
    private long applicationId;

    @PostConstruct
    private void setUp() {
        this.applicationId = gateway.getRestClient().getApplicationId().blockOptional()
                .orElseThrow(() -> new IllegalStateException("Couldn't get Application ID"));

//        initializeCommand("commands").subscribe();
    }

    /**
     * Create a command in Discord
     * @param id The name of the command.
     * @return A Mono which returns true, if the command was created, or false if the ID didn't match anything. Propagates any Discord errors.
     */
    public Mono<Boolean> initializeCommand(String id) {
        var cmd = commands.getCommandByName(id);
        return cmd.map(command -> gateway.getRestClient().getApplicationService()
                    .createGuildApplicationCommand(this.applicationId, GUILD_ID, command.getDiscordSpec())
                    .thenReturn(true))
                .orElseGet(() -> Mono.just(false));

    }

    /**
     * Update a command in Discord
     * @param id The name of the command.
     * @return A Mono which returns true if the command was updated, or false if the ID didn't match. Propagates any Discord errors.
     */
    public Mono<Boolean> updateCommand(String id) {
        var cmd = commands.getCommandByName(id);
        return cmd.map(command -> gateway.getRestClient().getApplicationService()
                    .bulkOverwriteGuildApplicationCommand(this.applicationId, GUILD_ID, List.of(command.getDiscordSpec()))
                    .then(Mono.just(true)))
                .orElseGet(() -> Mono.just(false));
    }

    /**
     * Delete a command in Discord
     * @param id The name of the command.
     * @return A Mono which returns true if the command was deleted, or false if the ID didn't match. Propagates any Discord errors.
     */
    public Mono<Boolean> deleteCommand(String id) {
        var service = gateway.getRestClient().getApplicationService();
        return service
                .getGuildApplicationCommands(this.applicationId, GUILD_ID)
                .filter(acd -> acd.name().equals(id))
                .next()
                .flatMap(acd -> service.deleteGuildApplicationCommand(this.applicationId, GUILD_ID, acd.id().asLong()).thenReturn(true))
                .switchIfEmpty(Mono.just(false));
    }
}
