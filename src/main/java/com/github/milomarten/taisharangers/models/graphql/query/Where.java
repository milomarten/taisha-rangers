package com.github.milomarten.taisharangers.models.graphql.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuperBuilder
@NoArgsConstructor
public abstract class Where {
    @JsonProperty("_and")
    private Where and;
    @JsonProperty("_or")
    private Where or;
    @JsonProperty("_not")
    private Where not;
}
