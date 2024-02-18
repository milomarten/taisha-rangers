package com.github.milomarten.taisharangers.services;

import com.github.milomarten.taisharangers.models.graphql.operations.*;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * Utility classes to handle some common GraphQL operation functionality
 */
@UtilityClass
public class GraphQLOperationUtils {
    /**
     * Chooses between an Equals or an In query, depending on the number of objects in a list.
     * @param l The list to handle
     * @return An operation to efficiently check that a GraphQL attribute is one value in this list
     * @param <T> The type contained in the list.
     */
    public <T> Operation<T> equalsOrIn(List<T> l) {
        if (l.size() == 1) {
            return new Equals<>(l.get(0));
        } else {
            return new In<>(l);
        }
    }

    /**
     * Handles a Range query, based on the nullity of the upper and lower bounds.
     * All bounds are inclusive. Truth table:
     * Both bounds null -> return null
     * Lower bound null, upper bound non-null -> return LessThenOrEqual
     * Lower bound non-null, upper bound null -> return GreaterThanOrEqual
     * Lower and upper bounds equal -> return Equal
     * Lower bound greater than upper bound -> return Inclusive Range with arguments inverted
     * Lower bound less than upper bound -> return Inclusive Range
     * @param lowerInclusive The lower bound, inclusive.
     * @param upperInclusive The upper bound, exclusive.
     * @return An operation tailed to the provided bounds
     * @param <T> The type being queried.
     */
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
