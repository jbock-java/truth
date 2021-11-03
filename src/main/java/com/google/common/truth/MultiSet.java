package com.google.common.truth;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

final class MultiSet<E> extends AbstractSet<Set<E>> {

    @Override
    public Iterator<Set<E>> iterator() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }
}
