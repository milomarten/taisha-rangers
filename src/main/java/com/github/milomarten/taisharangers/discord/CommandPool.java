package com.github.milomarten.taisharangers.discord;

import com.github.milomarten.taisharangers.discord.commands.Command;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The pool of registered Commands in the system.
 */
@RequiredArgsConstructor
@Component
public class CommandPool {
    @Getter
    private final List<Command> commands;
    private Map<String, Command> commandsByName = new HashMap<>();

    @PostConstruct
    private void setUp() {
        this.commandsByName = this.commands.stream()
                .collect(Collectors.toMap(Command::getName, Function.identity()));
    }

    /**
     * Get a command, if it exists, by its name
     * @param name The command's name
     * @return The command, or empty if no match
     */
    public Optional<Command> getCommandByName(String name) {
        return Optional.ofNullable(commandsByName.get(name));
    }
}
