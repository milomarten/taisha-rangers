package com.github.milomarten.taisharangers.services;

import com.github.milomarten.taisharangers.models.graphql.operations.*;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class GraphQLOperationUtils {
    public <T> Operation<T> equalsOrIn(List<T> l) {
        if (l.size() == 1) {
            return new Equals<>(l.get(0));
        } else {
            return new In<>(l);
        }
    }

    public <T extends Comparable<T>> Operation<T> range(T lowerInclusive, T upperInclusive) {
        if (lowerInclusive == null && upperInclusive == null) {
            return null;
        } else if (lowerInclusive == null) {
            return new LessThanOrEqual<>(upperInclusive);
        } else if (upperInclusive == null) {
            return new GreaterThanOrEqual<>(lowerInclusive);
        } else {
            int cmp = lowerInclusive.compareTo(upperInclusive);
            if (cmp == 0) {
                return new Equals<>(lowerInclusive);
            } else if (cmp < 0) {
                return new RangeInclusive<>(lowerInclusive, upperInclusive);
            } else {
                return new RangeInclusive<>(upperInclusive, lowerInclusive);
            }
        }
    }
}
