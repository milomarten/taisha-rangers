package com.github.milomarten.taisharangers.discord.commands;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
class Try<T> {
    private final T obj;
    private final String error;

    public static <T> Try<T> success(T obj) {
        return new Try<>(obj, null);
    }

    public static <T> Try<T> failure(String reason) {
        return new Try<>(null, reason);
    }
}
