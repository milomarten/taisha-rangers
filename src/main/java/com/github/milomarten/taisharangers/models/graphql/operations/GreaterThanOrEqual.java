package com.github.milomarten.taisharangers.models.graphql.operations;

public record GreaterThanOrEqual<T extends Comparable<T>>(T _gte) implements Operation<T> {
}
