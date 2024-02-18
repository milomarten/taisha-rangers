package com.github.milomarten.taisharangers.discord.commands;

import java.util.List;

/**
 * An interface which marks that a component can help with Discord Autocomplete
 * This can be implemented within a Command itself, or in a separate bean that supports
 * multiple similar commands.
 * Discord expects Autocomplete choices to be returned promptly, so Mono and Flux are not supported here.
 * Also remember that the `autocomplete` attribute must be present on any parameters that can be autocompleted.
 * Otherwise, this bean will not even be called!
 */
public interface SupportsAutocomplete {
    /**
     * Determine whether this bean can help with a specific command
     * @param commandName The name of the command to check
     * @return True, if options may be provided for that command
     */
    boolean supportsCommand(String commandName);

    /**
     * Get all the possible choices for the given parameter name
     * For consistency, pattern matching is done outside of this method, rather than
     * potentially allowing each command to have its own fuzzy match.
     * @param paramName The parameter being investigated
     * @return The list of all options.
     */
    List<Choice> getCandidates(String paramName);

    /**
     * Represents one Autocomplete choice
     * @param id The ID, or computer-friendly, name of this choice
     * @param display The value displayed to the user
     */
    public record Choice(String id, String display) {
        /**
         * Create a choice where the ID and Display names are the same
         * @param display The name to show
         * @return The Choice object
         */
        public static Choice fromString(String display) {
            return new Choice(display, display);
        }
    }
}
