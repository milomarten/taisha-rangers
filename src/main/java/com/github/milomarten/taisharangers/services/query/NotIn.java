package com.github.milomarten.taisharangers.services.query;

import java.util.List;

public record NotIn<T>(List<T> _nin) implements Operation<T>{
}
