package com.github.milomarten.taisharangers.models.csv.util;

import org.apache.commons.collections4.multimap.AbstractMultiValuedMap;

import java.util.*;

public class NonNullMultiValuedMap<K, V> extends AbstractMultiValuedMap<K, V> {
    public NonNullMultiValuedMap() {
        super(new HashMap<>());
    }

    @Override
    protected Collection<V> createCollection() {
        return new NonNullSet<>(new HashSet<>());
    }
}

