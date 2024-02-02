package com.github.milomarten.taisharangers.services.query;

public record GreaterThanOrEqual<T extends Comparable<T>>(T _gte) implements Operation<T> {
}
