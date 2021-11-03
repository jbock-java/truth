/*
 * Copyright (C) 2008 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.common.primitives;

import com.google.common.truth.Preconditions;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.Spliterators;

import static com.google.common.truth.Preconditions.checkElementIndex;
import static java.util.Objects.requireNonNull;

/**
 * Static utility methods pertaining to {@code int} primitives, that are not already found in either
 * {@link Integer} or {@link Arrays}.
 *
 * <p>See the Guava User Guide article on <a
 * href="https://github.com/google/guava/wiki/PrimitivesExplained">primitive utilities</a>.
 *
 * @author Kevin Bourrillion
 * @since 1.0
 */
public final class Ints {
    private Ints() {
    }

    /**
     * The number of bytes required to represent a primitive {@code int} value.
     *
     * <p><b>Java 8 users:</b> use {@link Integer#BYTES} instead.
     */
    public static final int BYTES = Integer.SIZE / Byte.SIZE;

    /**
     * Returns a hash code for {@code value}; equal to the result of invoking {@code ((Integer)
     * value).hashCode()}.
     *
     * <p><b>Java 8 users:</b> use {@link Integer#hashCode(int)} instead.
     *
     * @param value a primitive {@code int} value
     * @return a hash code for the value
     */
    public static int hashCode(int value) {
        return value;
    }

    /**
     * Returns the {@code int} value that is equal to {@code value}, if possible.
     *
     * @param value any value in the range of the {@code int} type
     * @return the {@code int} value that equals {@code value}
     * @throws IllegalArgumentException if {@code value} is greater than {@link Integer#MAX_VALUE} or
     *     less than {@link Integer#MIN_VALUE}
     */
    public static int checkedCast(long value) {
        int result = (int) value;
        Preconditions.checkArgument(result == value, "Out of range: %s", value);
        return result;
    }

    /**
     * Compares the two specified {@code int} values. The sign of the value returned is the same as
     * that of {@code ((Integer) a).compareTo(b)}.
     *
     * <p><b>Note for Java 7 and later:</b> this method should be treated as deprecated; use the
     * equivalent {@link Integer#compare} method instead.
     *
     * @param a the first {@code int} to compare
     * @param b the second {@code int} to compare
     * @return a negative value if {@code a} is less than {@code b}; a positive value if {@code a} is
     *     greater than {@code b}; or zero if they are equal
     */
    public static int compare(int a, int b) {
        return (a < b) ? -1 : ((a > b) ? 1 : 0);
    }

    /**
     * Returns {@code true} if {@code target} is present as an element anywhere in {@code array}.
     *
     * @param array an array of {@code int} values, possibly empty
     * @param target a primitive {@code int} value
     * @return {@code true} if {@code array[i] == target} for some value of {@code i}
     */
    public static boolean contains(int[] array, int target) {
        for (int value : array) {
            if (value == target) {
                return true;
            }
        }
        return false;
    }

    // TODO(kevinb): consider making this public
    private static int indexOf(int[] array, int target, int start, int end) {
        for (int i = start; i < end; i++) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }

    // TODO(kevinb): consider making this public
    private static int lastIndexOf(int[] array, int target, int start, int end) {
        for (int i = end - 1; i >= start; i--) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns a fixed-size list backed by the specified array, similar to {@link
     * Arrays#asList(Object[])}. The list supports {@link List#set(int, Object)}, but any attempt to
     * set a value to {@code null} will result in a {@link NullPointerException}.
     *
     * <p>The returned list maintains the values, but not the identities, of {@code Integer} objects
     * written to or read from it. For example, whether {@code list.get(0) == list.get(0)} is true for
     * the returned list is unspecified.
     *
     * <p><b>Note:</b> when possible, you should represent your data as an {@code ImmutableIntArray}
     * instead, which has an {@code ImmutableIntArray#asList asList} view.
     *
     * @param backingArray the array to back the list
     * @return a list view of the array
     */
    public static List<Integer> asList(int... backingArray) {
        if (backingArray.length == 0) {
            return Collections.emptyList();
        }
        return new IntArrayAsList(backingArray);
    }

    private static class IntArrayAsList extends AbstractList<Integer>
            implements RandomAccess, Serializable {
        final int[] array;
        final int start;
        final int end;

        IntArrayAsList(int[] array) {
            this(array, 0, array.length);
        }

        IntArrayAsList(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        public int size() {
            return end - start;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Integer get(int index) {
            checkElementIndex(index, size());
            return array[start + index];
        }

        @Override
        public Spliterator.OfInt spliterator() {
            return Spliterators.spliterator(array, start, end, 0);
        }

        @Override
        public boolean contains(Object target) {
            // Overridden to prevent a ton of boxing
            return (target instanceof Integer) && Ints.indexOf(array, (Integer) target, start, end) != -1;
        }

        @Override
        public int indexOf(Object target) {
            // Overridden to prevent a ton of boxing
            if (target instanceof Integer) {
                int i = Ints.indexOf(array, (Integer) target, start, end);
                if (i >= 0) {
                    return i - start;
                }
            }
            return -1;
        }

        @Override
        public int lastIndexOf(Object target) {
            // Overridden to prevent a ton of boxing
            if (target instanceof Integer) {
                int i = Ints.lastIndexOf(array, (Integer) target, start, end);
                if (i >= 0) {
                    return i - start;
                }
            }
            return -1;
        }

        @Override
        public Integer set(int index, Integer element) {
            checkElementIndex(index, size());
            int oldValue = array[start + index];
            // checkNotNull for GWT (do not optimize)
            array[start + index] = requireNonNull(element);
            return oldValue;
        }

        @Override
        public List<Integer> subList(int fromIndex, int toIndex) {
            int size = size();
            Preconditions.checkPositionIndexes(fromIndex, toIndex, size);
            if (fromIndex == toIndex) {
                return Collections.emptyList();
            }
            return new IntArrayAsList(array, start + fromIndex, start + toIndex);
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof IntArrayAsList) {
                IntArrayAsList that = (IntArrayAsList) object;
                int size = size();
                if (that.size() != size) {
                    return false;
                }
                for (int i = 0; i < size; i++) {
                    if (array[start + i] != that.array[that.start + i]) {
                        return false;
                    }
                }
                return true;
            }
            return super.equals(object);
        }

        @Override
        public int hashCode() {
            int result = 1;
            for (int i = start; i < end; i++) {
                result = 31 * result + Ints.hashCode(array[i]);
            }
            return result;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(size() * 5);
            builder.append('[').append(array[start]);
            for (int i = start + 1; i < end; i++) {
                builder.append(", ").append(array[i]);
            }
            return builder.append(']').toString();
        }

        int[] toIntArray() {
            return Arrays.copyOfRange(array, start, end);
        }

        private static final long serialVersionUID = 0;
    }

    /**
     * Parses the specified string as a signed decimal integer value. The ASCII character {@code '-'}
     * (<code>'&#92;u002D'</code>) is recognized as the minus sign.
     *
     * <p>Unlike {@link Integer#parseInt(String)}, this method returns {@code null} instead of
     * throwing an exception if parsing fails. Additionally, this method only accepts ASCII digits,
     * and returns {@code null} if non-ASCII digits are present in the string.
     *
     * <p>Note that strings prefixed with ASCII {@code '+'} are rejected, even under JDK 7, despite
     * the change to {@link Integer#parseInt(String)} for that version.
     *
     * @param string the string representation of an integer value
     * @return the integer value represented by {@code string}, or {@code null} if {@code string} has
     *     a length of zero or cannot be parsed as an integer value
     * @throws NullPointerException if {@code string} is {@code null}
     * @since 11.0
     */
    public static Integer tryParse(String string) {
        return tryParse(string, 10);
    }

    /**
     * Parses the specified string as a signed integer value using the specified radix. The ASCII
     * character {@code '-'} (<code>'&#92;u002D'</code>) is recognized as the minus sign.
     *
     * <p>Unlike {@link Integer#parseInt(String, int)}, this method returns {@code null} instead of
     * throwing an exception if parsing fails. Additionally, this method only accepts ASCII digits,
     * and returns {@code null} if non-ASCII digits are present in the string.
     *
     * <p>Note that strings prefixed with ASCII {@code '+'} are rejected, even under JDK 7, despite
     * the change to {@link Integer#parseInt(String, int)} for that version.
     *
     * @param string the string representation of an integer value
     * @param radix the radix to use when parsing
     * @return the integer value represented by {@code string} using {@code radix}, or {@code null} if
     *     {@code string} has a length of zero or cannot be parsed as an integer value
     * @throws IllegalArgumentException if {@code radix < Character.MIN_RADIX} or {@code radix >
     *     Character.MAX_RADIX}
     * @throws NullPointerException if {@code string} is {@code null}
     * @since 19.0
     */
    public static Integer tryParse(String string, int radix) {
        Long result = Longs.tryParse(string, radix);
        if (result == null || result.longValue() != result.intValue()) {
            return null;
        } else {
            return result.intValue();
        }
    }
}
