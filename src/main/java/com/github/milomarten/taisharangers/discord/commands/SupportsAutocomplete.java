package com.github.milomarten.taisharangers.discord.commands;

import java.util.List;

public interface SupportsAutocomplete {
    boolean supportsCommand(String commandName);
    List<Choice> getCandidates(String paramName);

    public record Choice(String id, String display) {
        public static Choice fromString(String display) {
            return new Choice(display, display);
        }
    }
}
