package com.github.milomarten.taisharangers.models.graphql.operations.strings;

import com.github.milomarten.taisharangers.models.graphql.operations.Operation;

public record CaseInsensitiveNotRegex(String _niregex) implements Operation<String> {
}
