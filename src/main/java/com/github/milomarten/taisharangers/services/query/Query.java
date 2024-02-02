package com.github.milomarten.taisharangers.services.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuperBuilder
public abstract class Query<WHERE> {
    private WHERE where;

    private Integer limit;

    private Integer offset;

    public abstract String getLabel();
}
