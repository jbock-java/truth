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
 * Static utility methods pertaining to {@code long} primitives, that are not already found in
 * either {@link Long} or {@link Arrays}.
 *
 * <p>See the Guava User Guide article on <a
 * href="https://github.com/google/guava/wiki/PrimitivesExplained">primitive utilities</a>.
 *
 * @author Kevin Bourrillion
 * @since 1.0
 */
public final class Longs {
    private Longs() {
    }

    /**
     * The number of bytes required to represent a primitive {@code long} value.
     *
     * <p><b>Java 8 users:</b> use {@link Long#BYTES} instead.
     */
    public static final int BYTES = Long.SIZE / Byte.SIZE;

    /**
     * The largest power of two that can be represented as a {@code long}.
     *
     * @since 10.0
     */
    public static final long MAX_POWER_OF_TWO = 1L << (Long.SIZE - 2);

    /**
     * Returns a hash code for {@code value}; equal to the result of invoking {@code ((Long)
     * value).hashCode()}.
     *
     * <p>This method always return the value specified by {@link Long#hashCode()} in java, which
     * might be different from {@code ((Long) value).hashCode()} in GWT because {@link
     * Long#hashCode()} in GWT does not obey the JRE contract.
     *
     * <p><b>Java 8 users:</b> use {@link Long#hashCode(long)} instead.
     *
     * @param value a primitive {@code long} value
     * @return a hash code for the value
     */
    public static int hashCode(long value) {
        return (int) (value ^ (value >>> 32));
    }

    /**
     * Compares the two specified {@code long} values. The sign of the value returned is the same as
     * that of {@code ((Long) a).compareTo(b)}.
     *
     * <p><b>Note for Java 7 and later:</b> this method should be treated as deprecated; use the
     * equivalent {@link Long#compare} method instead.
     *
     * @param a the first {@code long} to compare
     * @param b the second {@code long} to compare
     * @return a negative value if {@code a} is less than {@code b}; a positive value if {@code a} is
     *     greater than {@code b}; or zero if they are equal
     */
    public static int compare(long a, long b) {
        return (a < b) ? -1 : ((a > b) ? 1 : 0);
    }

    /**
     * Returns {@code true} if {@code target} is present as an element anywhere in {@code array}.
     *
     * @param array an array of {@code long} values, possibly empty
     * @param target a primitive {@code long} value
     * @return {@code true} if {@code array[i] == target} for some value of {@code i}
     */
    public static boolean contains(long[] array, long target) {
        for (long value : array) {
            if (value == target) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the index of the first appearance of the value {@code target} in {@code array}.
     *
     * @param array an array of {@code long} values, possibly empty
     * @param target a primitive {@code long} value
     * @return the least index {@code i} for which {@code array[i] == target}, or {@code -1} if no
     *     such index exists.
     */
    public static int indexOf(long[] array, long target) {
        return indexOf(array, target, 0, array.length);
    }

    // TODO(kevinb): consider making this public
    private static int indexOf(long[] array, long target, int start, int end) {
        for (int i = start; i < end; i++) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }

    // TODO(kevinb): consider making this public
    private static int lastIndexOf(long[] array, long target, int start, int end) {
        for (int i = end - 1; i >= start; i--) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }


    /*
     * Moving asciiDigits into this static holder class lets ProGuard eliminate and inline the Longs
     * class.
     */
    static final class AsciiDigits {
        private AsciiDigits() {
        }

        private static final byte[] asciiDigits;

        static {
            byte[] result = new byte[128];
            Arrays.fill(result, (byte) -1);
            for (int i = 0; i < 10; i++) {
                result['0' + i] = (byte) i;
            }
            for (int i = 0; i < 26; i++) {
                result['A' + i] = (byte) (10 + i);
                result['a' + i] = (byte) (10 + i);
            }
            asciiDigits = result;
        }

        static int digit(char c) {
            return (c < 128) ? asciiDigits[c] : -1;
        }
    }

    /**
     * Parses the specified string as a signed decimal long value. The ASCII character {@code '-'} (
     * <code>'&#92;u002D'</code>) is recognized as the minus sign.
     *
     * <p>Unlike {@link Long#parseLong(String)}, this method returns {@code null} instead of throwing
     * an exception if parsing fails. Additionally, this method only accepts ASCII digits, and returns
     * {@code null} if non-ASCII digits are present in the string.
     *
     * <p>Note that strings prefixed with ASCII {@code '+'} are rejected, even under JDK 7, despite
     * the change to {@link Long#parseLong(String)} for that version.
     *
     * @param string the string representation of a long value
     * @return the long value represented by {@code string}, or {@code null} if {@code string} has a
     *     length of zero or cannot be parsed as a long value
     * @throws NullPointerException if {@code string} is {@code null}
     * @since 14.0
     */
    public static Long tryParse(String string) {
        return tryParse(string, 10);
    }

    /**
     * Parses the specified string as a signed long value using the specified radix. The ASCII
     * character {@code '-'} (<code>'&#92;u002D'</code>) is recognized as the minus sign.
     *
     * <p>Unlike {@link Long#parseLong(String, int)}, this method returns {@code null} instead of
     * throwing an exception if parsing fails. Additionally, this method only accepts ASCII digits,
     * and returns {@code null} if non-ASCII digits are present in the string.
     *
     * <p>Note that strings prefixed with ASCII {@code '+'} are rejected, even under JDK 7, despite
     * the change to {@link Long#parseLong(String, int)} for that version.
     *
     * @param string the string representation of an long value
     * @param radix the radix to use when parsing
     * @return the long value represented by {@code string} using {@code radix}, or {@code null} if
     *     {@code string} has a length of zero or cannot be parsed as a long value
     * @throws IllegalArgumentException if {@code radix < Character.MIN_RADIX} or {@code radix >
     *     Character.MAX_RADIX}
     * @throws NullPointerException if {@code string} is {@code null}
     * @since 19.0
     */
    public static Long tryParse(String string, int radix) {
        if (requireNonNull(string).isEmpty()) {
            return null;
        }
        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
            throw new IllegalArgumentException(
                    "radix must be between MIN_RADIX and MAX_RADIX but was " + radix);
        }
        boolean negative = string.charAt(0) == '-';
        int index = negative ? 1 : 0;
        if (index == string.length()) {
            return null;
        }
        int digit = AsciiDigits.digit(string.charAt(index++));
        if (digit < 0 || digit >= radix) {
            return null;
        }
        long accum = -digit;

        long cap = Long.MIN_VALUE / radix;

        while (index < string.length()) {
            digit = AsciiDigits.digit(string.charAt(index++));
            if (digit < 0 || digit >= radix || accum < cap) {
                return null;
            }
            accum *= radix;
            if (accum < Long.MIN_VALUE + digit) {
                return null;
            }
            accum -= digit;
        }

        if (negative) {
            return accum;
        } else if (accum == Long.MIN_VALUE) {
            return null;
        } else {
            return -accum;
        }
    }

    /**
     * Returns a fixed-size list backed by the specified array, similar to {@link
     * Arrays#asList(Object[])}. The list supports {@link List#set(int, Object)}, but any attempt to
     * set a value to {@code null} will result in a {@link NullPointerException}.
     *
     * <p>The returned list maintains the values, but not the identities, of {@code Long} objects
     * written to or read from it. For example, whether {@code list.get(0) == list.get(0)} is true for
     * the returned list is unspecified.
     *
     * <p><b>Note:</b> when possible, you should represent your data as an {@code ImmutableLongArray}
     * instead, which has an {@code ImmutableLongArray#asList asList} view.
     *
     * @param backingArray the array to back the list
     * @return a list view of the array
     */
    public static List<Long> asList(long... backingArray) {
        if (backingArray.length == 0) {
            return Collections.emptyList();
        }
        return new LongArrayAsList(backingArray);
    }

    private static class LongArrayAsList extends AbstractList<Long>
            implements RandomAccess, Serializable {
        final long[] array;
        final int start;
        final int end;

        LongArrayAsList(long[] array) {
            this(array, 0, array.length);
        }

        LongArrayAsList(long[] array, int start, int end) {
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
        public Long get(int index) {
            Preconditions.checkElementIndex(index, size());
            return array[start + index];
        }

        @Override
        public Spliterator.OfLong spliterator() {
            return Spliterators.spliterator(array, start, end, 0);
        }

        @Override
        public boolean contains(Object target) {
            // Overridden to prevent a ton of boxing
            return (target instanceof Long) && Longs.indexOf(array, (Long) target, start, end) != -1;
        }

        @Override
        public int indexOf(Object target) {
            // Overridden to prevent a ton of boxing
            if (target instanceof Long) {
                int i = Longs.indexOf(array, (Long) target, start, end);
                if (i >= 0) {
                    return i - start;
                }
            }
            return -1;
        }

        @Override
        public int lastIndexOf(Object target) {
            // Overridden to prevent a ton of boxing
            if (target instanceof Long) {
                int i = Longs.lastIndexOf(array, (Long) target, start, end);
                if (i >= 0) {
                    return i - start;
                }
            }
            return -1;
        }

        @Override
        public Long set(int index, Long element) {
            checkElementIndex(index, size());
            long oldValue = array[start + index];
            // checkNotNull for GWT (do not optimize)
            array[start + index] = requireNonNull(element);
            return oldValue;
        }

        @Override
        public List<Long> subList(int fromIndex, int toIndex) {
            int size = size();
            Preconditions.checkPositionIndexes(fromIndex, toIndex, size);
            if (fromIndex == toIndex) {
                return Collections.emptyList();
            }
            return new LongArrayAsList(array, start + fromIndex, start + toIndex);
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof LongArrayAsList) {
                LongArrayAsList that = (LongArrayAsList) object;
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
                result = 31 * result + Longs.hashCode(array[i]);
            }
            return result;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(size() * 10);
            builder.append('[').append(array[start]);
            for (int i = start + 1; i < end; i++) {
                builder.append(", ").append(array[i]);
            }
            return builder.append(']').toString();
        }

        long[] toLongArray() {
            return Arrays.copyOfRange(array, start, end);
        }

        private static final long serialVersionUID = 0;
    }
}
