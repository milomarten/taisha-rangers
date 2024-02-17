package com.github.milomarten.taisharangers.discord;

import com.github.milomarten.taisharangers.discord.commands.SupportsAutocomplete;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@ConditionalOnBean(GatewayDiscordClient.class)
@RequiredArgsConstructor
@Slf4j
public class CommandHandler implements ApplicationRunner {
    private final GatewayDiscordClient gateway;

    private final CommandPool commands;

    private final List<SupportsAutocomplete> autocompleteHandlers;

    @PreDestroy
    private void spinDown() {
        gateway.logout().block();
    }

    @Override
    public void run(ApplicationArguments args) {
        gateway.on(ChatInputInteractionEvent.class, chat -> {
            var command = this.commands.getCommandByName(chat.getCommandName());
            if (command.isEmpty()) {
                return chat.reply("No idea what that means").withEphemeral(true);
            } else {
                return command.get().handle(chat);
            }
        })
        .onErrorContinue((t, o) -> log.error("Error in Chat Input handler", t))
        .subscribe();

        gateway.on(ChatInputAutoCompleteEvent.class, autocomplete -> {
            List<SupportsAutocomplete.Choice> candidates = autocompleteHandlers.stream()
                    .filter(sa -> sa.supportsCommand(autocomplete.getCommandName()))
                    .flatMap(sa -> {
                        var potential = sa.getCandidates(autocomplete.getFocusedOption().getName());
                        return potential == null ? Stream.empty() : potential.stream();
                    })
                    .toList();

            if (CollectionUtils.isEmpty(candidates)) {
                return Mono.empty();
            } else {
                var soFar = autocomplete.getFocusedOption().getValue()
                        .map(ApplicationCommandInteractionOptionValue::asString)
                        .orElse("");

                var substringCandidates = candidates.stream()
                    .filter(c -> StringUtils.startsWithIgnoreCase(c.display(), soFar))
                    .limit(25)
                    .sorted(Comparator.comparing(c -> c.display().length()))
                    .map(c -> ApplicationCommandOptionChoiceData.builder()
                            .name(c.display())
                            .value(c.id())
                            .build())
                    .collect(Collectors.<ApplicationCommandOptionChoiceData>toList());

                return autocomplete.respondWithSuggestions(substringCandidates);
            }
        })
        .onErrorContinue((t, o) -> log.error("Error in Autocomplete handler", t))
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
