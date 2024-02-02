package com.github.milomarten.taisharangers.models.graphql.operations.strings;

import com.github.milomarten.taisharangers.models.graphql.operations.Operation;

public record Regex(String _regex) implements Operation<String> {
}
