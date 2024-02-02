package com.github.milomarten.taisharangers.models.graphql.operations;

public record LessThanOrEqual<T extends Comparable<T>>(T _lte) implements Operation<T> {
}
