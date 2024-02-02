package com.github.milomarten.taisharangers.models.graphql.operations;

public record IsNull<T>(boolean _is_null) implements Operation<T> {
}
