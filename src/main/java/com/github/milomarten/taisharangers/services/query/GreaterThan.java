package com.github.milomarten.taisharangers.services.query;

public record GreaterThan<T extends Comparable<T>>(T _gt) implements Operation<T> {
}
