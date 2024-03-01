package com.github.milomarten.taisharangers.discord.commands;

import com.github.milomarten.taisharangers.discord.StandardParams;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.spec.InteractionReplyEditSpec;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequiredArgsConstructor
public abstract class AsyncResponseMultiCommand implements Command {
    protected final Map<String, Branch<?, ?>> branches;

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        var options = event.getOptions();
        if (options.isEmpty()) {
            return handleNoOptions(event);
        }
        var optsMaybe = options.get(0);
        if (branches.containsKey(optsMaybe.getName())) {
            return branches.get(optsMaybe.getName()).handle(event, optsMaybe);
        } else {
            return handleUnknownOption(event, optsMaybe.getName());
        }
    }

    protected Mono<Void> handleNoOptions(ChatInputInteractionEvent event) {
        return event.reply("No options specified").withEphemeral(true);
    }

    protected Mono<Void> handleUnknownOption(ChatInputInteractionEvent event, String attempt) {
        return event.reply("Unknown option: " + attempt).withEphemeral(true);
    }

    static abstract class Branch<PARAM, RES> {
        public Mono<Void> handle(ChatInputInteractionEvent event, ApplicationCommandInteractionOption options) {
            var parse = parseParameters(event, options);
            if (parse.getError() != null) {
                return event.reply(parse.getError()).withEphemeral(!isShare(options));
            }
            return event.deferReply()
                    .withEphemeral(!isShare(options))
                    .then(doAsyncOperations(parse.getObj()))
                    .flatMap(response -> event.editReply(formatResponse(response)))
                    .onErrorResume(t -> event.editReply(formatErrorResponse(t)))
                    .onErrorContinue((t, o) -> t.printStackTrace())
                    .then();
        }

        protected abstract Try<PARAM> parseParameters(ChatInputInteractionEvent event, ApplicationCommandInteractionOption options);
        protected abstract Mono<RES> doAsyncOperations(PARAM parameters);
        protected abstract InteractionReplyEditSpec formatResponse(RES response);
        protected String formatErrorResponse(Throwable err) { return "Encountered an error while retrieving. Please check the logs."; }
        protected boolean isShare(ApplicationCommandInteractionOption options) {
            return StandardParams.isShare(options);
        }
    }
}
