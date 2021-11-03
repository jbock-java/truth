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

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.util.Objects.requireNonNull;

/**
 * Static utility methods pertaining to {@code double} primitives, that are not already found in
 * either {@link Double} or {@link Arrays}.
 *
 * <p>See the Guava User Guide article on <a
 * href="https://github.com/google/guava/wiki/PrimitivesExplained">primitive utilities</a>.
 *
 * @author Kevin Bourrillion
 * @since 1.0
 */
public final class Doubles {
    private Doubles() {
    }

    /**
     * The number of bytes required to represent a primitive {@code double} value.
     *
     * <p><b>Java 8 users:</b> use {@link Double#BYTES} instead.
     *
     * @since 10.0
     */
    public static final int BYTES = Double.SIZE / Byte.SIZE;

    /**
     * Returns a hash code for {@code value}; equal to the result of invoking {@code ((Double)
     * value).hashCode()}.
     *
     * <p><b>Java 8 users:</b> use {@link Double#hashCode(double)} instead.
     *
     * @param value a primitive {@code double} value
     * @return a hash code for the value
     */
    public static int hashCode(double value) {
        return ((Double) value).hashCode();
        // TODO(kevinb): do it this way when we can (GWT problem):
        // long bits = Double.doubleToLongBits(value);
        // return (int) (bits ^ (bits >>> 32));
    }

    /**
     * Compares the two specified {@code double} values. The sign of the value returned is the same as
     * that of <code>((Double) a).{@linkplain Double#compareTo compareTo}(b)</code>. As with that
     * method, {@code NaN} is treated as greater than all other values, and {@code 0.0 > -0.0}.
     *
     * <p><b>Note:</b> this method simply delegates to the JDK method {@link Double#compare}. It is
     * provided for consistency with the other primitive types, whose compare methods were not added
     * to the JDK until JDK 7.
     *
     * @param a the first {@code double} to compare
     * @param b the second {@code double} to compare
     * @return a negative value if {@code a} is less than {@code b}; a positive value if {@code a} is
     *     greater than {@code b}; or zero if they are equal
     */
    public static int compare(double a, double b) {
        return Double.compare(a, b);
    }

    /**
     * Returns {@code true} if {@code value} represents a real number. This is equivalent to, but not
     * necessarily implemented as, {@code !(Double.isInfinite(value) || Double.isNaN(value))}.
     *
     * <p><b>Java 8 users:</b> use {@link Double#isFinite(double)} instead.
     *
     * @since 10.0
     */
    public static boolean isFinite(double value) {
        return NEGATIVE_INFINITY < value && value < POSITIVE_INFINITY;
    }

    /**
     * Returns {@code true} if {@code target} is present as an element anywhere in {@code array}. Note
     * that this always returns {@code false} when {@code target} is {@code NaN}.
     *
     * @param array an array of {@code double} values, possibly empty
     * @param target a primitive {@code double} value
     * @return {@code true} if {@code array[i] == target} for some value of {@code i}
     */
    public static boolean contains(double[] array, double target) {
        for (double value : array) {
            if (value == target) {
                return true;
            }
        }
        return false;
    }

    // TODO(kevinb): consider making this public
    private static int indexOf(double[] array, double target, int start, int end) {
        for (int i = start; i < end; i++) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }

    // TODO(kevinb): consider making this public
    private static int lastIndexOf(double[] array, double target, int start, int end) {
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
     * <p>The returned list maintains the values, but not the identities, of {@code Double} objects
     * written to or read from it. For example, whether {@code list.get(0) == list.get(0)} is true for
     * the returned list is unspecified.
     *
     * <p>The returned list may have unexpected behavior if it contains {@code NaN}, or if {@code NaN}
     * is used as a parameter to any of its methods.
     *
     * <p><b>Note:</b> when possible, you should represent your data as an {@code
     * ImmutableDoubleArray} instead, which has an {@code ImmutableDoubleArray#asList asList} view.
     *
     * @param backingArray the array to back the list
     * @return a list view of the array
     */
    public static List<Double> asList(double... backingArray) {
        if (backingArray.length == 0) {
            return Collections.emptyList();
        }
        return new DoubleArrayAsList(backingArray);
    }

    private static class DoubleArrayAsList extends AbstractList<Double>
            implements RandomAccess, Serializable {
        final double[] array;
        final int start;
        final int end;

        DoubleArrayAsList(double[] array) {
            this(array, 0, array.length);
        }

        DoubleArrayAsList(double[] array, int start, int end) {
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
        public Double get(int index) {
            Preconditions.checkElementIndex(index, size());
            return array[start + index];
        }

        @Override
        public Spliterator.OfDouble spliterator() {
            return Spliterators.spliterator(array, start, end, 0);
        }

        @Override
        public boolean contains(Object target) {
            // Overridden to prevent a ton of boxing
            return (target instanceof Double)
                    && Doubles.indexOf(array, (Double) target, start, end) != -1;
        }

        @Override
        public int indexOf(Object target) {
            // Overridden to prevent a ton of boxing
            if (target instanceof Double) {
                int i = Doubles.indexOf(array, (Double) target, start, end);
                if (i >= 0) {
                    return i - start;
                }
            }
            return -1;
        }

        @Override
        public int lastIndexOf(Object target) {
            // Overridden to prevent a ton of boxing
            if (target instanceof Double) {
                int i = Doubles.lastIndexOf(array, (Double) target, start, end);
                if (i >= 0) {
                    return i - start;
                }
            }
            return -1;
        }

        @Override
        public Double set(int index, Double element) {
            Preconditions.checkElementIndex(index, size());
            double oldValue = array[start + index];
            // checkNotNull for GWT (do not optimize)
            array[start + index] = requireNonNull(element);
            return oldValue;
        }

        @Override
        public List<Double> subList(int fromIndex, int toIndex) {
            int size = size();
            Preconditions.checkPositionIndexes(fromIndex, toIndex, size);
            if (fromIndex == toIndex) {
                return Collections.emptyList();
            }
            return new DoubleArrayAsList(array, start + fromIndex, start + toIndex);
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof DoubleArrayAsList) {
                DoubleArrayAsList that = (DoubleArrayAsList) object;
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
                result = 31 * result + Doubles.hashCode(array[i]);
            }
            return result;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(size() * 12);
            builder.append('[').append(array[start]);
            for (int i = start + 1; i < end; i++) {
                builder.append(", ").append(array[i]);
            }
            return builder.append(']').toString();
        }

        double[] toDoubleArray() {
            return Arrays.copyOfRange(array, start, end);
        }

        private static final long serialVersionUID = 0;
    }

    /**
     * This is adapted from the regex suggested by {@link Double#valueOf(String)} for prevalidating
     * inputs. All valid inputs must pass this regex, but it's semantically fine if not all inputs
     * that pass this regex are valid -- only a performance hit is incurred, not a semantics bug.
     */
    static final
    java.util.regex.Pattern
            FLOATING_POINT_PATTERN = fpPattern();

    private static java.util.regex.Pattern
    fpPattern() {
        /*
         * We use # instead of * for possessive quantifiers. This lets us strip them out when building
         * the regex for RE2 (which doesn't support them) but leave them in when building it for
         * java.util.regex (where we want them in order to avoid catastrophic backtracking).
         */
        String decimal = "(?:\\d+#(?:\\.\\d*#)?|\\.\\d+#)";
        String completeDec = decimal + "(?:[eE][+-]?\\d+#)?[fFdD]?";
        String hex = "(?:[0-9a-fA-F]+#(?:\\.[0-9a-fA-F]*#)?|\\.[0-9a-fA-F]+#)";
        String completeHex = "0[xX]" + hex + "[pP][+-]?\\d+#[fFdD]?";
        String fpPattern = "[+-]?(?:NaN|Infinity|" + completeDec + "|" + completeHex + ")";
        fpPattern =
                fpPattern.replace(
                        "#",
                        "+"
                );
        return
                java.util.regex.Pattern
                        .compile(fpPattern);
    }
}
