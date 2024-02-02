package com.github.milomarten.taisharangers.services.query;

public record IsNull<T>(boolean _is_null) implements Operation<T> {
}
