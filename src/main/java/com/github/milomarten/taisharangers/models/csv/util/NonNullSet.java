package com.github.milomarten.taisharangers.models.csv.util;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

class NonNullSet<V> extends AbstractSet<V> {
    private final Set<V> backing;

    public NonNullSet(Set<V> backing) {
        this.backing = backing;
    }

    @Override
    public Iterator<V> iterator() {
        return backing.iterator();
    }

    @Override
    public int size() {
        return backing.size();
    }

    @Override
    public boolean add(V v) {
        if (v == null) {
            return false;
        }
        return backing.add(v);
    }
}
