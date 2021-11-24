/*
 * Copyright (c) 2011 Google, Inc.
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

import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for Integer Subjects.
 *
 * @author David Saff
 * @author Christian Gruber
 * @author Kurt Alfred Kluever
 */
class IntegerSubjectTest extends BaseSubjectTestCase {

    @Test
    void simpleEquality() {
        assertThat(4).isEqualTo(4);
    }

    @Test
    void simpleInequality() {
        assertThat(4).isNotEqualTo(5);
    }

    @Test
    void equalityWithLongs() {
        assertThat(0).isEqualTo(0L);
        assertThrows(
                AssertionError.class,
                () -> assertThat(0)
                        .isNotEqualTo(0L));
    }

    @Test
    void equalityFail() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(4)
                        .isEqualTo(5));
    }

    @Test
    void inequalityFail() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(4)
                        .isNotEqualTo(4));
    }

    @Test
    void equalityOfNulls() {
        assertThat((Integer) null).isEqualTo(null);
    }

    @Test
    void equalityOfNullsFail_nullActual() {
        assertThrows(
                AssertionError.class,
                () -> assertThat((Integer) null)
                        .isEqualTo(5));
    }

    @Test
    void equalityOfNullsFail_nullExpected() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(5)
                        .isEqualTo(null));
    }

    @Test
    void inequalityOfNulls() {
        assertThat(4).isNotEqualTo(null);
        assertThat((Integer) null).isNotEqualTo(4);
    }

    @Test
    void inequalityOfNullsFail() {
        assertThrows(
                AssertionError.class,
                () -> assertThat((Integer) null)
                        .isNotEqualTo(null));
    }

    @Test
    void overflowOnPrimitives() {
        assertThat(Long.MIN_VALUE).isNotEqualTo(Integer.MIN_VALUE);
        assertThat(Long.MAX_VALUE).isNotEqualTo(Integer.MAX_VALUE);

        assertThat(Integer.MIN_VALUE).isNotEqualTo(Long.MIN_VALUE);
        assertThat(Integer.MAX_VALUE).isNotEqualTo(Long.MAX_VALUE);

        assertThat(Integer.MIN_VALUE).isEqualTo((long) Integer.MIN_VALUE);
        assertThat(Integer.MAX_VALUE).isEqualTo((long) Integer.MAX_VALUE);
    }

    @Test
    void overflowOnPrimitives_shouldBeEqualAfterCast_min() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(Integer.MIN_VALUE)
                        .isNotEqualTo((long) Integer.MIN_VALUE));
    }

    @Test
    void overflowOnPrimitives_shouldBeEqualAfterCast_max() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(Integer.MAX_VALUE)
                        .isNotEqualTo((long) Integer.MAX_VALUE));
    }

    @Test
    void overflowBetweenIntegerAndLong_shouldBeDifferent_min() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(Integer.MIN_VALUE)
                        .isEqualTo(Long.MIN_VALUE));
    }

    @Test
    void overflowBetweenIntegerAndLong_shouldBeDifferent_max() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(Integer.MAX_VALUE)
                        .isEqualTo(Long.MAX_VALUE));
    }

    @SuppressWarnings("TruthSelfEquals")
    @Test
    void testPrimitivesVsBoxedPrimitivesVsObject_int() {
        int int42 = 42;
        Integer integer42 = 42;
        Object object42 = 42;

        assertThat(int42).isEqualTo(int42);
        assertThat(integer42).isEqualTo(int42);
        assertThat(object42).isEqualTo(int42);

        assertThat(int42).isEqualTo(integer42);
        assertThat(integer42).isEqualTo(integer42);
        assertThat(object42).isEqualTo(integer42);

        assertThat(int42).isEqualTo(object42);
        assertThat(integer42).isEqualTo(object42);
        assertThat(object42).isEqualTo(object42);
    }

    @SuppressWarnings("TruthSelfEquals")
    @Test
    void testPrimitivesVsBoxedPrimitivesVsObject_long() {
        long longPrim42 = 42;
        Long long42 = 42L;
        Object object42 = 42L;

        assertThat(longPrim42).isEqualTo(longPrim42);
        assertThat(long42).isEqualTo(longPrim42);
        assertThat(object42).isEqualTo(longPrim42);

        assertThat(longPrim42).isEqualTo(long42);
        assertThat(long42).isEqualTo(long42);
        assertThat(object42).isEqualTo(long42);

        assertThat(longPrim42).isEqualTo(object42);
        assertThat(long42).isEqualTo(object42);
        assertThat(object42).isEqualTo(object42);
    }

    @Test
    void testAllCombinations_pass() {
        assertThat(42).isEqualTo(42L);
        assertThat(42).isEqualTo(42L);
        assertThat(42).isEqualTo(42L);
        assertThat(42).isEqualTo(42L);
        assertThat(42L).isEqualTo(42);
        assertThat(42L).isEqualTo(42);
        assertThat(42L).isEqualTo(42);
        assertThat(42L).isEqualTo(42);

        assertThat(42).isEqualTo(42);
        assertThat(42).isEqualTo(42);
        assertThat(42).isEqualTo(42);
        assertThat(42).isEqualTo(42);
        assertThat(42L).isEqualTo(42L);
        assertThat(42L).isEqualTo(42L);
        assertThat(42L).isEqualTo(42L);
        assertThat(42L).isEqualTo(42L);
    }

    @Test
    void testNumericTypeWithSameValue_shouldBeEqual_int_long() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(42)
                        .isNotEqualTo(42L));
    }

    @Test
    void testNumericTypeWithSameValue_shouldBeEqual_int_int() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(42)
                        .isNotEqualTo(42));
    }

    @Test
    void testNumericPrimitiveTypes_isNotEqual_shouldFail_intToChar() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(42)
                        .isNotEqualTo((char) 42));
        // 42 in ASCII is '*'
        assertFailureValue(
                failure,
                "expected not to be", "*");
        assertFailureValue(
                failure,
                "but was; string representation of actual value", "42");
    }

    @Test
    void testNumericPrimitiveTypes_isNotEqual_shouldFail_charToInt() {
        // Uses Object overload rather than Integer.
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat((char) 42)
                        .isNotEqualTo(42));
        // 42 in ASCII is '*'
        assertFailureValue(
                failure,
                "expected not to be", "42");
        assertFailureValue(
                failure,
                "but was; string representation of actual value", "*");
    }

    private static final Subject.Factory<Subject, Object> DEFAULT_SUBJECT_FACTORY =
            (metadata, that) -> new Subject(metadata, that);

    private static void expectFailure(
            ExpectFailure.SimpleSubjectBuilderCallback<Subject, Object> callback) {
        AssertionError unused = ExpectFailure.expectFailureAbout(DEFAULT_SUBJECT_FACTORY, callback);
    }

    @Test
    void testNumericPrimitiveTypes() {
        byte byte42 = (byte) 42;
        short short42 = (short) 42;
        char char42 = (char) 42;
        int int42 = 42;
        long long42 = (long) 42;

        ImmutableSet<Object> fortyTwos =
                ImmutableSet.<Object>of(byte42, short42, char42, int42, long42);
        for (Object actual : fortyTwos) {
            for (Object expected : fortyTwos) {
                assertThat(actual).isEqualTo(expected);
            }
        }

        ImmutableSet<Object> fortyTwosNoChar = ImmutableSet.<Object>of(byte42, short42, int42, long42);
        for (final Object actual : fortyTwosNoChar) {
            for (final Object expected : fortyTwosNoChar) {
                ExpectFailure.SimpleSubjectBuilderCallback<Subject, Object> actualFirst =
                        expect -> expect.that(actual).isNotEqualTo(expected);
                ExpectFailure.SimpleSubjectBuilderCallback<Subject, Object> expectedFirst =
                        expect -> expect.that(expected).isNotEqualTo(actual);
                expectFailure(actualFirst);
                expectFailure(expectedFirst);
            }
        }

        byte byte41 = (byte) 41;
        short short41 = (short) 41;
        char char41 = (char) 41;
        int int41 = 41;
        long long41 = (long) 41;

        ImmutableSet<Object> fortyOnes =
                ImmutableSet.<Object>of(byte41, short41, char41, int41, long41);

        for (Object first : fortyTwos) {
            for (Object second : fortyOnes) {
                assertThat(first).isNotEqualTo(second);
                assertThat(second).isNotEqualTo(first);
            }
        }
    }
}
