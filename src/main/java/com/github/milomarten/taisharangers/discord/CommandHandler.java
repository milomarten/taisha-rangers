package com.github.milomarten.taisharangers.discord;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@ConditionalOnBean(GatewayDiscordClient.class)
public class CommandHandler implements ApplicationRunner {
    private final GatewayDiscordClient gateway;

    private final Map<String, Command> commands;

    public CommandHandler(GatewayDiscordClient gateway, List<Command> commands) {
        this.gateway = gateway;
        this.commands = commands.stream()
                .collect(Collectors.toMap(Command::getName, Function.identity()));
    }

    @PreDestroy
    private void spinDown() {
        gateway.logout().block();
    }

    @Override
    public void run(ApplicationArguments args) {
        gateway.on(ChatInputInteractionEvent.class, chat -> {
            var command = this.commands.get(chat.getCommandName());
            if (command == null) {
                return chat.reply("No idea what that means").withEphemeral(true);
            } else {
                return command.handle(chat);
            }
        })
        .onErrorResume(t -> {
            t.printStackTrace();
            return Mono.empty();
        })
        .subscribe();
    }

    private void setupCommand() {
        var appId = gateway.getRestClient().getApplicationId().block();

        var request = ApplicationCommandRequest.builder()
                .name("pokedex")
                .description("Look up any Pokemon in the Pokedex, powered by PokeAPI")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("name")
                        .required(true)
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .description("The Pokemon's name or Dex Number")
                        .build()
                )
                .build();

        gateway.getRestClient()
                .getApplicationService()
                .createGuildApplicationCommand(appId, 902681369405173840L, request)
                .block();
    }
}
