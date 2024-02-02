package com.github.milomarten.taisharangers.models.graphql.operations;

public record GreaterThan<T extends Comparable<T>>(T _gt) implements Operation<T> {
}
