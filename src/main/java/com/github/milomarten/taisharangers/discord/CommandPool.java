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

    public Optional<Command> getCommandByName(String name) {
        return Optional.ofNullable(commandsByName.get(name));
    }
}
