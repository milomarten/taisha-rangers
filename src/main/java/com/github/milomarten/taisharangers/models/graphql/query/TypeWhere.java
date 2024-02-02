package com.github.milomarten.taisharangers.models.graphql.query;

import com.github.milomarten.taisharangers.models.graphql.operations.Operation;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TypeWhere extends Where {
    private Operation<String> name;
}
