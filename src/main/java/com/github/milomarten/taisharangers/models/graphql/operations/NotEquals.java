package com.github.milomarten.taisharangers.models.graphql.operations;

public record NotEquals<T>(T _neq) implements Operation<T> {
}
