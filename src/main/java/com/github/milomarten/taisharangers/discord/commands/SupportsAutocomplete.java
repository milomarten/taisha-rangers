package com.github.milomarten.taisharangers.discord.commands;

import java.util.List;

public interface SupportsAutocomplete {
    List<String> getCandidates(String paramName);
}
