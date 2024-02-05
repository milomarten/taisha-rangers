package com.github.milomarten.taisharangers.models.graphql.operations;

public record RangeInclusive<T extends Comparable<T>>(T _lte, T _gte) implements Operation<T> {
}
