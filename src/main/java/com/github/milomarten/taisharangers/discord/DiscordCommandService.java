package com.github.milomarten.taisharangers.discord;

import discord4j.core.GatewayDiscordClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@ConditionalOnBean(GatewayDiscordClient.class)
@RequiredArgsConstructor
public class DiscordCommandService {
    private static final long GUILD_ID = 902681369405173840L;

    private final GatewayDiscordClient gateway;
    private final CommandPool commands;
    private long applicationId;

    @PostConstruct
    private void setUp() {
        this.applicationId = gateway.getRestClient().getApplicationId().blockOptional()
                .orElseThrow(() -> new IllegalStateException("Couldn't get Application ID"));

//        initializeCommand("commands").subscribe();
    }

    public Mono<Boolean> initializeCommand(String id) {
        var cmd = commands.getCommandByName(id);
        return cmd.map(command -> gateway.getRestClient().getApplicationService()
                    .createGuildApplicationCommand(this.applicationId, GUILD_ID, command.getDiscordSpec())
                    .thenReturn(true))
                .orElseGet(() -> Mono.just(false));

    }

    public Mono<Boolean> updateCommand(String id) {
        var cmd = commands.getCommandByName(id);
        return cmd.map(command -> gateway.getRestClient().getApplicationService()
                    .bulkOverwriteGuildApplicationCommand(this.applicationId, GUILD_ID, List.of(command.getDiscordSpec()))
                    .then(Mono.just(true)))
                .orElseGet(() -> Mono.just(false));
    }

    public Mono<Boolean> deleteCommand(String id) {
        var cmd = commands.getCommandByName(id);
        if (cmd.isEmpty()) {
            return Mono.just(false);
        }

        var service = gateway.getRestClient().getApplicationService();
        return service
                .getGuildApplicationCommands(this.applicationId, GUILD_ID)
                .filter(acd -> acd.name().equals(id))
                .next()
                .flatMap(acd -> service.deleteGuildApplicationCommand(this.applicationId, GUILD_ID, acd.id().asLong()).thenReturn(true))
                .switchIfEmpty(Mono.just(false));
    }
}
