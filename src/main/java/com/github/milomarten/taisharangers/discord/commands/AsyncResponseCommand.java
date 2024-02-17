package com.github.milomarten.taisharangers.discord.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.InteractionReplyEditSpec;
import reactor.core.publisher.Mono;

public abstract class AsyncResponseCommand<PARAM, RES> implements Command {
    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        var parse = parseParameters(event);
        if (parse.getError() != null) {
            return event.reply(parse.getError()).withEphemeral(isEphemeral(event));
        }
        return event.deferReply()
                .withEphemeral(isEphemeral(event))
                .then(doAsyncOperations(parse.getObj()))
                .flatMap(response -> event.editReply(formatResponse(response)))
                .onErrorResume(t -> event.editReply(formatErrorResponse(t)))
                .onErrorContinue((t, o) -> t.printStackTrace())
                .then();
    }

    protected abstract boolean isEphemeral(ChatInputInteractionEvent event);

    protected abstract Try<PARAM> parseParameters(ChatInputInteractionEvent event);

    protected abstract Mono<RES> doAsyncOperations(PARAM parameters);

    protected abstract InteractionReplyEditSpec formatResponse(RES response);

    protected String formatErrorResponse(Throwable err) { return "Encountered an error while retrieving. Please check the logs."; }
}

