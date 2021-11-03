/*
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.truth;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class Iterables {
    private Iterables() {
    }

    /**
     * Returns the single element contained in {@code iterable}.
     *
     * <p><b>Java 8 users:</b> the {@code Stream} equivalent to this method is {@code
     * stream.collect(MoreCollectors.onlyElement())}.
     *
     * @throws NoSuchElementException if the iterable is empty
     * @throws IllegalArgumentException if the iterable contains multiple elements
     */
    public static <T> T getOnlyElement(Iterable<T> iterable) {
        Iterator<T> iterator = iterable.iterator();
        T first = iterator.next();
        if (!iterator.hasNext()) {
            return first;
        }

        StringBuilder sb = new StringBuilder().append("expected one element but was: <").append(first);
        for (int i = 0; i < 4 && iterator.hasNext(); i++) {
            sb.append(", ").append(iterator.next());
        }
        if (iterator.hasNext()) {
            sb.append(", ...");
        }
        sb.append('>');

        throw new IllegalArgumentException(sb.toString());
    }

    /**
     * Determines if the given iterable contains no elements.
     *
     * <p>There is no precise {@link Iterator} equivalent to this method, since one can only ask an
     * iterator whether it has any elements <i>remaining</i> (which one does using {@link
     * Iterator#hasNext}).
     *
     * <p><b>{@code Stream} equivalent:</b> {@code !stream.findAny().isPresent()}
     *
     * @return {@code true} if the iterable contains no elements
     */
    public static boolean isEmpty(Iterable<?> iterable) {
        if (iterable instanceof Collection) {
            return ((Collection<?>) iterable).isEmpty();
        }
        return !iterable.iterator().hasNext();
    }

    public static String toString(Iterable<?> iterable) {
        Iterator<?> iterator = iterable.iterator();
        StringBuilder sb = new StringBuilder().append('[');
        boolean first = true;
        while (iterator.hasNext()) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(iterator.next());
        }
        return sb.append(']').toString();
    }
}
