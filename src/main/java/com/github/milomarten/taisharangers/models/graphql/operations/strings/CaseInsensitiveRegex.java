package com.github.milomarten.taisharangers.models.graphql.operations.strings;

import com.github.milomarten.taisharangers.models.graphql.operations.Operation;

public record CaseInsensitiveRegex(String _iregex) implements Operation<String> {
}
