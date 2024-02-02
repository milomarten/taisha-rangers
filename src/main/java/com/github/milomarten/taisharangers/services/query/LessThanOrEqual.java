package com.github.milomarten.taisharangers.services.query;

public record LessThanOrEqual<T extends Comparable<T>>(T _lte) implements Operation<T> {
}
