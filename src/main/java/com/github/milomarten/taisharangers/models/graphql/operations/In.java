package com.github.milomarten.taisharangers.models.graphql.operations;

import java.util.List;

public record In<T>(List<T> _in) implements Operation<T> {
}
