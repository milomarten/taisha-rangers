package com.github.milomarten.taisharangers.services.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Where {
    @JsonProperty("_and")
    private Where and;
    @JsonProperty("_or")
    private Where or;
    @JsonProperty("_not")
    private Where not;
}
