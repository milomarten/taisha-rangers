package com.github.milomarten.taisharangers.discord.commands;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Quick and dirty abstraction for validation without relying on exceptions.
 * Inspiration taken from Rust.
 * A Try has two states, either a success response, or a failure message.
 * May be removed in the future to support more in-depth libraries.
 * @param <T> The type contained, in a successful scenario.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
class Try<T> {
    private final T obj;
    private final String error;

    /**
     * Create a successful-state Try
     * @param obj The object to wrap
     * @return The created Try
     * @param <T> The type of the object
     */
    public static <T> Try<T> success(T obj) {
        return new Try<>(obj, null);
    }

    /**
     * Create a failure-state Try
     * @param reason The reason for the failure
     * @return The created Try
     * @param <T> The type the success would have been
     */
    public static <T> Try<T> failure(String reason) {
        return new Try<>(null, reason);
    }
}
