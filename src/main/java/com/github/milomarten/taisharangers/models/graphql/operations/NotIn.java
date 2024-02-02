package com.github.milomarten.taisharangers.models.graphql.operations;

import java.util.List;

public record NotIn<T>(List<T> _nin) implements Operation<T>{
}
