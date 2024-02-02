package com.github.milomarten.taisharangers.services.query;

import java.util.List;

public record In<T>(List<T> _in) implements Operation<T> {
}
