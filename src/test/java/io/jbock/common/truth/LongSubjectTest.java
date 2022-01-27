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
package io.jbock.common.truth;

import org.junit.jupiter.api.Test;

import static io.jbock.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for Long Subjects.
 *
 * @author David Saff
 * @author Christian Gruber
 * @author Kurt Alfred Kluever
 */
class LongSubjectTest extends BaseSubjectTestCase {

    @Test
    void simpleEquality() {
        assertThat(4L).isEqualTo(4L);
    }

    @Test
    void simpleInequality() {
        assertThat(4L).isNotEqualTo(5L);
    }

    @Test
    void equalityWithInts() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(0L)
                        .isNotEqualTo(0));
    }

    @Test
    void equalityFail() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(4L)
                        .isEqualTo(5L));
    }

    @Test
    void inequalityFail() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(4L)
                        .isNotEqualTo(4L));
    }

    @Test
    void equalityOfNulls() {
        assertThat((Long) null).isEqualTo(null);
    }

    @Test
    void equalityOfNullsFail_nullActual() {
        assertThrows(
                AssertionError.class,
                () -> assertThat((Long) null)
                        .isEqualTo(5L));
    }

    @Test
    void equalityOfNullsFail_nullExpected() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(5L)
                        .isEqualTo(null));
    }

    @Test
    void inequalityOfNulls() {
        assertThat(4L).isNotEqualTo(null);
        assertThat((Integer) null).isNotEqualTo(4L);
    }

    @Test
    void inequalityOfNullsFail() {
        assertThrows(
                AssertionError.class,
                () -> assertThat((Long) null)
                        .isNotEqualTo(null));
    }

    @Test
    void testNumericTypeWithSameValue_shouldBeEqual_long_long() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(42L)
                        .isNotEqualTo(42L));
    }

    @Test
    void testNumericTypeWithSameValue_shouldBeEqual_long_int() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(42L)
                        .isNotEqualTo(42));
    }

    @Test
    void isGreaterThan_int_strictly() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(2L)
                        .isGreaterThan(3));
    }

    @Test
    void isGreaterThan_int() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(2L)
                        .isGreaterThan(2));
        assertThat(2L).isGreaterThan(1);
    }

    @Test
    void isLessThan_int_strictly() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(2L)
                        .isLessThan(1));
    }

    @Test
    void isLessThan_int() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(2L)
                        .isLessThan(2));
        assertThat(2L).isLessThan(3);
    }

    @Test
    void isAtLeast_int() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(2L)
                        .isAtLeast(3));
        assertThat(2L).isAtLeast(2);
        assertThat(2L).isAtLeast(1);
    }

    @Test
    void isAtMost_int() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(2L)
                        .isAtMost(1));
        assertThat(2L).isAtMost(2);
        assertThat(2L).isAtMost(3);
    }
}
