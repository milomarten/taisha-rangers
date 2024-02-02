package com.github.milomarten.taisharangers.models.graphql.query;

import com.github.milomarten.taisharangers.models.graphql.operations.Operation;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AggregateCount<OF> {
    private Operation<Integer> predicate;
}
