package com.github.milomarten.taisharangers.discord;

import com.github.milomarten.taisharangers.discord.commands.Command;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.SimilarityScore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@ConditionalOnBean(GatewayDiscordClient.class)
@RequiredArgsConstructor
public class CommandHandler implements ApplicationRunner {
    private final GatewayDiscordClient gateway;

    private final CommandPool commands;

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
        .onErrorResume(t -> {
            t.printStackTrace();
            return Mono.empty();
        })
        .subscribe();

        gateway.on(ChatInputAutoCompleteEvent.class, autocomplete -> {
            var command = this.commands.getCommandByName(autocomplete.getCommandName());
            if (command.isPresent() && command.get() instanceof SupportsAutocomplete sa) {
                var candidates = sa.getCandidates(autocomplete.getFocusedOption().getName());
                var soFar = autocomplete.getFocusedOption().getValue()
                        .map(ApplicationCommandInteractionOptionValue::asString)
                        .orElse("");

                var substringCandidates = candidates.stream()
                        .filter(c -> StringUtils.startsWith(c, soFar))
                        .limit(25)
                        .sorted(Comparator.comparing(String::length))
                        .map(c -> ApplicationCommandOptionChoiceData.builder()
                                .name(c)
                                .value(c)
                                .build())
                        .collect(Collectors.<ApplicationCommandOptionChoiceData>toList());
                return autocomplete.respondWithSuggestions(substringCandidates);
            } else {
                return Mono.empty();
            }
        }).subscribe();
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
