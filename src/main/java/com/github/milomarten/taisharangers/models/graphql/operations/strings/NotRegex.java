package com.github.milomarten.taisharangers.models.graphql.operations.strings;

import com.github.milomarten.taisharangers.models.graphql.operations.Operation;

public record NotRegex(String _nregex) implements Operation<String> {
}
