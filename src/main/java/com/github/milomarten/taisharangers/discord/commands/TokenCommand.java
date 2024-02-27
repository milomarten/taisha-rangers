package com.github.milomarten.taisharangers.discord.commands;

import com.github.milomarten.taisharangers.discord.StandardParams;
import com.github.milomarten.taisharangers.image.gradients.TypeGradient;
import com.github.milomarten.taisharangers.image.layers.LayeredImage;
import com.github.milomarten.taisharangers.models.Gender;
import com.github.milomarten.taisharangers.services.TokenGeneratorService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.spec.InteractionReplyEditSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.pokemon.Pokemon;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class TokenCommand extends AsyncResponseCommand<TokenCommandParams, LayeredImage> implements SupportsAutocomplete {
    private static final String ID_PARAMETER = "id";
    private static final String GENDER_PARAMETER = "gender";
    private static final String SHINY_PARAMETER = "shiny";
    private static final String COLOR_OVERRIDE_ONE_PARAMETER = "color-override-1";
    private static final String COLOR_OVERRIDE_TWO_PARAMETER = "color-override-2";

    private final PokeApiClient client;
    private final TokenGeneratorService service;

    @Override
    protected boolean isEphemeral(ChatInputInteractionEvent event) {
        return !StandardParams.isShare(event);
    }

    @Override
    public ApplicationCommandRequest getDiscordSpec() {
        return ApplicationCommandRequest.builder()
                .name(getName())
                .description("Generate a token for a Pokemon")
                .addOption(ApplicationCommandOptionData.builder()
                        .name(ID_PARAMETER)
                        .description("The Pokemon name or number to generate")
                        .required(true)
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .build())
                .addOption(ApplicationCommandOptionData.builder()
                        .name(GENDER_PARAMETER)
                        .description("Whether the female sprite should be used instead of the male/genderless sprite.")
                        .required(false)
                        .type(ApplicationCommandOption.Type.BOOLEAN.getValue())
                        .build())
                .addOption(ApplicationCommandOptionData.builder()
                        .name(SHINY_PARAMETER)
                        .description("Whether the sprite should use the shiny coloration.")
                        .required(false)
                        .type(ApplicationCommandOption.Type.BOOLEAN.getValue())
                        .build())
                .addOption(ApplicationCommandOptionData.builder()
                        .name(COLOR_OVERRIDE_ONE_PARAMETER)
                        .description("Override to use a custom Pokemon type instead of the natural primary type")
                        .required(false)
                        .autocomplete(true)
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .build())
                .addOption(ApplicationCommandOptionData.builder()
                        .name(COLOR_OVERRIDE_TWO_PARAMETER)
                        .description("Override to use a custom Pokemon type instead of the natural secondary type")
                        .required(false)
                        .autocomplete(true)
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .build())
                .addOption(StandardParams.shareParameter())
                .build();
    }

    @Override
    protected Try<TokenCommandParams> parseParameters(ChatInputInteractionEvent event) {
        var id = event.getOption(ID_PARAMETER)
                .flatMap(a -> a.getValue())
                .map(a -> a.asString());
        if (id.isEmpty()) {
            return Try.failure("Pokemon Name or ID must be present");
        }
        var opts = TokenGeneratorService.CustomizationOptions.builder();
        var gender = event.getOption(GENDER_PARAMETER)
                .flatMap(a -> a.getValue()).map(a -> a.asBoolean())
                .orElse(false);
        opts.gender(gender ? Gender.FEMALE : Gender.MALE);
        event.getOption(SHINY_PARAMETER)
                .flatMap(a -> a.getValue()).map(a -> a.asBoolean())
                .ifPresent(opts::shiny);
        var typeOneAttempt = event.getOption(COLOR_OVERRIDE_ONE_PARAMETER)
                .flatMap(a -> a.getValue()).map(a -> a.asString());
        if (typeOneAttempt.isPresent()) {
            var color = EnumUtils.getEnumIgnoreCase(TypeGradient.class, typeOneAttempt.get());
            if (color == null) {
                return Try.failure("Invalid primary color. Must be one of the 18 Pokemon types");
            }
            opts.firstColor(color.getLighter());
        }
        var typeTwoAttempt = event.getOption(COLOR_OVERRIDE_TWO_PARAMETER)
                .flatMap(a -> a.getValue()).map(a -> a.asString());
        if (typeTwoAttempt.isPresent()) {
            var color = EnumUtils.getEnumIgnoreCase(TypeGradient.class, typeTwoAttempt.get());
            if (color == null) {
                return Try.failure("Invalid secondary color. Must be one of the 18 Pokemon types");
            }
            opts.secondColor(color.getDarker());
        }

        return Try.success(new TokenCommandParams(id.get(), opts.build()));
    }

    @Override
    protected Mono<LayeredImage> doAsyncOperations(TokenCommandParams parameters) {
        return client.getResource(Pokemon.class, parameters.id())
                .map(pkmn -> service.generateToken(pkmn, parameters.opts()));
    }

    @Override
    protected InteractionReplyEditSpec formatResponse(LayeredImage response) {
        try {
            return InteractionReplyEditSpec.builder()
                    .contentOrNull("Ding! Here's a fresh token for you!")
                    .addFile("token.png", new ByteArrayInputStream(response.toBytes()))
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return "token";
    }

    @Override
    public boolean supportsCommand(String commandName) {
        return getName().equals(commandName);
    }

    @Override
    public List<Choice> getCandidates(String paramName) {
        if (paramName.startsWith("color-override")) {
            return Arrays.stream(TypeGradient.values())
                    .map(tg -> new Choice(tg.name(), WordUtils.capitalize(tg.name())))
                    .toList();
        } else {
            return null;
        }
    }
}

record TokenCommandParams(String id, TokenGeneratorService.CustomizationOptions opts) {}
