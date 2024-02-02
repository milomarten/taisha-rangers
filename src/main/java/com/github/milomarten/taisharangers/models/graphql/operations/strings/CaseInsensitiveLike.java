package com.github.milomarten.taisharangers.models.graphql.operations.strings;

import com.github.milomarten.taisharangers.models.graphql.operations.Operation;

public record CaseInsensitiveLike(String _ilike) implements Operation<String> {
}
