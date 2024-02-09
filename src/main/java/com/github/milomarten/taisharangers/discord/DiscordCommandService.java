package com.github.milomarten.taisharangers.discord;

import discord4j.core.GatewayDiscordClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.SetUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@ConditionalOnBean(GatewayDiscordClient.class)
@RequiredArgsConstructor
public class DiscordCommandService {
    private static final long GUILD_ID = 902681369405173840L;

    private final GatewayDiscordClient gateway;
    private final List<Command> commands;
    private Map<String, Command> commandsByName;
    private long applicationId;
    @PostConstruct
    private void setUp() {
        var appId = gateway.getRestClient().getApplicationId().block();
        if (appId == null) {
            throw new IllegalStateException("Couldn't get Application ID");
        }
        this.applicationId = appId;

        this.commandsByName = this.commands.stream()
                .collect(Collectors.toMap(Command::getName, Function.identity()));
    }

    public Mono<Boolean> initializeCommand(String id) {
        var cmd = commandsByName.get(id);
        if (cmd == null) {
            return Mono.just(false);
        }

        return gateway.getRestClient().getApplicationService()
                .createGuildApplicationCommand(this.applicationId, GUILD_ID, cmd.getDiscordSpec())
                .thenReturn(true);
    }

    public Mono<Boolean> updateCommand(String id) {
        var cmd = commandsByName.get(id);
        if (cmd == null) {
            return Mono.just(false);
        }

        return gateway.getRestClient().getApplicationService()
                .bulkOverwriteGuildApplicationCommand(this.applicationId, GUILD_ID, List.of(cmd.getDiscordSpec()))
                .then(Mono.just(true));
    }

    public Mono<Boolean> deleteCommand(String id) {
        var cmd = commandsByName.get(id);
        if (cmd == null) {
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
