package com.github.milomarten.taisharangers.models.graphql.query.domain;

import com.github.milomarten.taisharangers.models.graphql.operations.Operation;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AbilityWhere {
    private Operation<String> name;
}
