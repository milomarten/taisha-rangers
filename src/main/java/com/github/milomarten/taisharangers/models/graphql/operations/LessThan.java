package com.github.milomarten.taisharangers.models.graphql.operations;

public record LessThan<T extends Comparable<T>>(T _lt) implements Operation<T> {
}
