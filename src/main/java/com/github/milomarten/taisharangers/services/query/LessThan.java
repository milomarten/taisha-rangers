package com.github.milomarten.taisharangers.services.query;

public record LessThan<T extends Comparable<T>>(T _lt) implements Operation<T> {
}
