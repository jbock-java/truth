/*
 * Copyright (C) 2007 The Guava Authors
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

package com.google.common.truth;

final class Preconditions {
    private Preconditions() {
    }

    static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    static void checkArgument(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    static void checkArgument(
            boolean b, String errorMessageTemplate, Object p1) {
        if (!b) {
            throw new IllegalArgumentException(String.format(errorMessageTemplate, p1));
        }
    }

    static void checkArgument(
            boolean b, String errorMessageTemplate, Object p1, Object p2) {
        if (!b) {
            throw new IllegalArgumentException(String.format(errorMessageTemplate, p1, p2));
        }
    }

    static void checkState(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }

    static void checkState(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalStateException(String.valueOf(errorMessage));
        }
    }

    static void checkState(
            boolean expression,
            String errorMessageTemplate,
            Object p1) {
        if (!expression) {
            throw new IllegalStateException(String.format(errorMessageTemplate, p1));
        }
    }

    static void checkState(
            boolean expression,
            String errorMessageTemplate,
            Object p1,
            Object p2) {
        if (!expression) {
            throw new IllegalStateException(String.format(errorMessageTemplate, p1, p2));
        }
    }

    static void checkState(
            boolean expression,
            String errorMessageTemplate,
            Object p1,
            Object p2,
            Object p3) {
        if (!expression) {
            throw new IllegalStateException(String.format(errorMessageTemplate, p1, p2, p3));
        }
    }

    static int checkNonnegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " cannot be negative but was: " + value);
        }
        return value;
    }

    static long checkNonnegative(long value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " cannot be negative but was: " + value);
        }
        return value;
    }

    /*
     * All recent hotspots (as of 2009) *really* like to have the natural code
     *
     * if (guardExpression) {
     *    throw new BadException(messageExpression);
     * }
     *
     * refactored so that messageExpression is moved to a separate String-returning method.
     *
     * if (guardExpression) {
     *    throw new BadException(badMsg(...));
     * }
     *
     * The alternative natural refactorings into void or Exception-returning methods are much slower.
     * This is a big deal - we're talking factors of 2-8 in microbenchmarks, not just 10-20%. (This is
     * a hotspot optimizer bug, which should be fixed, but that's a separate, big project).
     *
     * The coding pattern above is heavily used in java.util, e.g. in ArrayList. There is a
     * RangeCheckMicroBenchmark in the JDK that was used to test this.
     *
     * But the methods in this class want to throw different exceptions, depending on the args, so it
     * appears that this pattern is not directly applicable. But we can use the ridiculous, devious
     * trick of throwing an exception in the middle of the construction of another exception. Hotspot
     * is fine with that.
     */

    /**
     * Ensures that {@code index} specifies a valid <i>element</i> in an array, list or string of size
     * {@code size}. An element index may range from zero, inclusive, to {@code size}, exclusive.
     *
     * @param index a user-supplied index identifying an element of an array, list or string
     * @param size the size of that array, list or string
     * @return the value of {@code index}
     * @throws IndexOutOfBoundsException if {@code index} is negative or is not less than {@code size}
     * @throws IllegalArgumentException if {@code size} is negative
     */
    static int checkElementIndex(int index, int size) {
        return checkElementIndex(index, size, "index");
    }

    /**
     * Ensures that {@code index} specifies a valid <i>element</i> in an array, list or string of size
     * {@code size}. An element index may range from zero, inclusive, to {@code size}, exclusive.
     *
     * @param index a user-supplied index identifying an element of an array, list or string
     * @param size the size of that array, list or string
     * @param desc the text to use to describe this index in an error message
     * @return the value of {@code index}
     * @throws IndexOutOfBoundsException if {@code index} is negative or is not less than {@code size}
     * @throws IllegalArgumentException if {@code size} is negative
     */
    static int checkElementIndex(int index, int size, String desc) {
        // Carefully optimized for execution by hotspot (explanatory comment above)
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(badElementIndex(index, size, desc));
        }
        return index;
    }

    private static String badElementIndex(int index, int size, String desc) {
        if (index < 0) {
            return String.format("%s (%s) must not be negative", desc, index);
        } else if (size < 0) {
            throw new IllegalArgumentException("negative size: " + size);
        } else { // index >= size
            return String.format("%s (%s) must be less than size (%s)", desc, index, size);
        }
    }

    /**
     * Ensures that {@code start} and {@code end} specify valid <i>positions</i> in an array, list or
     * string of size {@code size}, and are in order. A position index may range from zero to {@code
     * size}, inclusive.
     *
     * @param start a user-supplied index identifying a starting position in an array, list or string
     * @param end a user-supplied index identifying an ending position in an array, list or string
     * @param size the size of that array, list or string
     * @throws IndexOutOfBoundsException if either index is negative or is greater than {@code size},
     *     or if {@code end} is less than {@code start}
     * @throws IllegalArgumentException if {@code size} is negative
     */
    static void checkPositionIndexes(int start, int end, int size) {
        // Carefully optimized for execution by hotspot (explanatory comment above)
        if (start < 0 || end < start || end > size) {
            throw new IndexOutOfBoundsException(badPositionIndexes(start, end, size));
        }
    }

    private static String badPositionIndex(int index, int size, String desc) {
        if (index < 0) {
            return String.format("%s (%s) must not be negative", desc, index);
        } else if (size < 0) {
            throw new IllegalArgumentException("negative size: " + size);
        } else { // index > size
            return String.format("%s (%s) must not be greater than size (%s)", desc, index, size);
        }
    }

    private static String badPositionIndexes(int start, int end, int size) {
        if (start < 0 || start > size) {
            return badPositionIndex(start, size, "start index");
        }
        if (end < 0 || end > size) {
            return badPositionIndex(end, size, "end index");
        }
        // end < start
        return String.format("end index (%s) must not be less than start index (%s)", end, start);
    }
}
