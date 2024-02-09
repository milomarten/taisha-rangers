package com.github.milomarten.taisharangers.discord;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

public interface Command {
    String getName();
    default ApplicationCommandRequest getDiscordSpec() {
        return ApplicationCommandRequest.builder()
                .name(getName())
                .build();
    }
    Mono<Void> handle(ChatInputInteractionEvent event);
}
