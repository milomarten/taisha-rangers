package com.github.milomarten.taisharangers.models.graphql.operations.strings;

import com.github.milomarten.taisharangers.models.graphql.operations.Operation;

public record CaseInsensitiveNotLike(String _nilike) implements Operation<String> {
}
