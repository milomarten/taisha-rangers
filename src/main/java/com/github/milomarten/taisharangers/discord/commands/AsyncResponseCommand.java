package com.github.milomarten.taisharangers.discord.commands;

import com.github.milomarten.taisharangers.discord.StandardParams;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.InteractionReplyEditSpec;
import reactor.core.publisher.Mono;

/**
 * A common abstraction on a Command which talks with a backend asynchronously
 * A Discord slash command must respond within 3 seconds, or it will be considered a failure. For longer
 * responses, the programmer must first defer the reply (displaying a loading spinner on Discord), and then
 * editing the reply when the command is complete.
 * This abstraction is meant to help separate these steps in
 * a consistent way, by breaking down the command into four template steps:
 * 1. Validate and parse the parameters. On a validation failure, the defer step won't happen, it will immediately
 * respond with an error message. On a success, the reply is defered, and the parameter object will be passed to Step 2.
 * 2. Do the asynchronous operations, using the Parameters computed in Step 1.
 * 3. On a success, format the asynchronous response into a Discord reply
 * 4. On a failure, format the exception into a Discord reply
 * If step 4 fails (because of Discord failure, for instance), the command is quietly dropped, and the error is logged.
 * @param <PARAM> The Parameter type (i.e. the request to the backend)
 * @param <RES> The Response type (i.e. the response from the backend)
 */
public abstract class AsyncResponseCommand<PARAM, RES> implements Command {
    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        var parse = parseParameters(event);
        if (parse.getError() != null) {
            return event.reply(parse.getError()).withEphemeral(!isShare(event));
        }
        return event.deferReply()
                .withEphemeral(!isShare(event))
                .then(doAsyncOperations(parse.getObj()))
                .flatMap(response -> event.editReply(formatResponse(response)))
                .onErrorResume(t -> event.editReply(formatErrorResponse(t)))
                .onErrorContinue((t, o) -> t.printStackTrace())
                .then();
    }

    /**
     * Determine if the response should be shared or not.
     * Mostly to support "share" parameters, which I use frequently. All responses, including errors, will have the
     * same ephemeral parameter
     * @param event The event context
     * @return True if the response should be visible only to the user.
     */
    protected boolean isShare(ChatInputInteractionEvent event) {
        return StandardParams.isShare(event);
    }

    /**
     * Attempt to parse the event context into a Parameter object
     * A failure Try will have its message sent, verbatim, to the user on Discord.
     * @param event The event context
     * @return The result of attempting to parse into the parameter object.
     */
    protected abstract Try<PARAM> parseParameters(ChatInputInteractionEvent event);

    /**
     * Perform the asynchronous operation on the parameters.
     * @param parameters The parameters to use
     * @return A Mono containing the eventual results of the operation
     */
    protected abstract Mono<RES> doAsyncOperations(PARAM parameters);

    /**
     * Format the response into a Discord reply.
     * @param response The response object, fully resolved
     * @return The Spec to be sent as the reply.
     */
    protected abstract InteractionReplyEditSpec formatResponse(RES response);

    /**
     * An optional handling if the async operation produces an error. By default, the response is
     * 'Encountered an error while retrieving. Please check the logs.'
     * @param err The cause of the error
     * @return The string response for that error.
     */
    protected String formatErrorResponse(Throwable err) { return "Encountered an error while retrieving. Please check the logs."; }
}

