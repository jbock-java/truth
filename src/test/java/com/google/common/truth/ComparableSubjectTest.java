/*
 * Copyright (c) 2014 Google, Inc.
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

import com.google.common.collect.Range;
import com.google.common.primitives.Ints;
import org.junit.jupiter.api.Test;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.truth.ExpectFailure.assertThat;
import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for Comparable Subjects.
 *
 * @author Kurt Alfred Kluever
 */
class ComparableSubjectTest extends BaseSubjectTestCase {

    @Test
    void testNulls() {
        try {
            assertThat(6).isEquivalentAccordingToCompareTo(null);
            fail();
        } catch (NullPointerException expected) {
        }
        try {
            assertThat(6).isGreaterThan(null);
            fail();
        } catch (NullPointerException expected) {
        }
        try {
            assertThat(6).isLessThan(null);
            fail();
        } catch (NullPointerException expected) {
        }
        try {
            assertThat(6).isAtMost(null);
            fail();
        } catch (NullPointerException expected) {
        }
        try {
            assertThat(6).isAtLeast(null);
            fail();
        } catch (NullPointerException expected) {
        }
    }


    @Test
    void isEquivalentAccordingToCompareTo() {
        assertThat(new StringComparedByLength("abc"))
                .isEquivalentAccordingToCompareTo(new StringComparedByLength("xyz"));

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(new StringComparedByLength("abc"))
                        .isEquivalentAccordingToCompareTo(new StringComparedByLength("abcd")));
        assertFailureValue(
                failure,
                "expected value that sorts equal to", "abcd");
    }

    private static final class StringComparedByLength implements Comparable<StringComparedByLength> {
        private final String value;

        StringComparedByLength(String value) {
            this.value = checkNotNull(value);
        }

        @Override
        public int compareTo(StringComparedByLength other) {
            return Ints.compare(value.length(), other.value.length());
        }

        @Override
        public String toString() {
            return value;
        }
    }

    @Test
    void isGreaterThan_failsEqual() {
        assertThat(5).isGreaterThan(4);

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(4)
                        .isGreaterThan(4));
        assertFailureValue(
                failure,
                "expected to be greater than", "4");
    }

    @Test
    void isGreaterThan_failsSmaller() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(3)
                        .isGreaterThan(4));
        assertFailureValue(
                failure,
                "expected to be greater than", "4");
    }

    @Test
    void isLessThan_failsEqual() {
        assertThat(4).isLessThan(5);

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(4)
                        .isLessThan(4));
        assertFailureValue(
                failure,
                "expected to be less than", "4");
    }

    @Test
    void isLessThan_failsGreater() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(4)
                        .isLessThan(3));
        assertFailureValue(
                failure,
                "expected to be less than", "3");
    }

    @Test
    void isAtMost() {
        assertThat(5).isAtMost(5);
        assertThat(5).isAtMost(6);

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(4)
                        .isAtMost(3));
        assertFailureValue(
                failure,
                "expected to be at most", "3");
    }

    @Test
    void isAtLeast() {
        assertThat(4).isAtLeast(3);
        assertThat(4).isAtLeast(4);

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(4)
                        .isAtLeast(5));
        assertFailureValue(
                failure,
                "expected to be at least", "5");
    }

    // Brief tests with other comparable types (no negative test cases)

    @Test
    void longs() {
        assertThat(5L).isGreaterThan(4L);
        assertThat(4L).isLessThan(5L);

        assertThat(4L).isAtMost(4L);
        assertThat(4L).isAtMost(5L);
        assertThat(4L).isAtLeast(4L);
        assertThat(4L).isAtLeast(3L);
    }

    @Test
    void strings() {
        assertThat("kak").isGreaterThan("gak");
        assertThat("gak").isLessThan("kak");

        assertThat("kak").isAtMost("kak");
        assertThat("gak").isAtMost("kak");
        assertThat("kak").isAtLeast("kak");
        assertThat("kak").isAtLeast("gak");
    }

    @Test
    void comparableType() {
        assertThat(new ComparableType(4)).isGreaterThan(new ComparableType(3));
        assertThat(new ComparableType(3)).isLessThan(new ComparableType(4));
    }

    @Test
    void namedComparableType() {
        assertWithMessage("comparable").that(new ComparableType(2)).isLessThan(new ComparableType(3));
    }

    private static final class ComparableType implements Comparable<ComparableType> {
        private final int wrapped;

        private ComparableType(int toWrap) {
            this.wrapped = toWrap;
        }

        @Override
        public int compareTo(ComparableType other) {
            return wrapped - other.wrapped;
        }
    }

    @Test
    void rawComparableType() {
        assertThat(new RawComparableType(3)).isLessThan(new RawComparableType(4));
    }

    @SuppressWarnings("ComparableType")
    private static final class RawComparableType implements Comparable {
        private final int wrapped;

        private RawComparableType(int toWrap) {
            this.wrapped = toWrap;
        }

        @Override
        public int compareTo(Object other) {
            return wrapped - ((RawComparableType) other).wrapped;
        }

        @Override
        public String toString() {
            return Integer.toString(wrapped);
        }
    }
}
