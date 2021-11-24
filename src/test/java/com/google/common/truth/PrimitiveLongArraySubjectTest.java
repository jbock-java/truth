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

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link com.google.common.truth.PrimitiveLongArraySubject}.
 *
 * @author Christian Gruber (cgruber@israfil.net)
 */
class PrimitiveLongArraySubjectTest extends BaseSubjectTestCase {

    @Test
    void isEqualTo() {
        assertThat(array(2L, 5)).isEqualTo(array(2L, 5));
    }

    @SuppressWarnings("TruthSelfEquals")
    @Test
    void isEqualTo_Same() {
        long[] same = array(2L, 5);
        assertThat(same).isEqualTo(same);
    }

    @Test
    void asList() {
        assertThat(array(5, 2, 9)).asList().containsAtLeast(2L, 9L);
    }

    @Test
    void isEqualTo_Fail_UnequalOrdering() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(2, 3))
                        .isEqualTo(array(3, 2)));
        assertFailureKeys(
                failure,
                "expected", "but was", "differs at index");
        assertFailureValue(
                failure,
                "expected", "[3, 2]");
        assertFailureValue(
                failure,
                "but was", "[2, 3]");
        assertFailureValue(
                failure,
                "differs at index", "[0]");
    }

    @Test
    void isEqualTo_Fail_NotAnArray() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(2, 3, 4))
                        .isEqualTo(new int[]{}));
        assertFailureKeys(
                failure,
                "expected", "but was", "wrong type", "expected", "but was");
        assertFailureValueIndexed(
                failure,
                "expected", 1, "int[]");
        assertFailureValueIndexed(
                failure,
                "but was", 1, "long[]");
    }

    @Test
    void isNotEqualTo_SameLengths() {
        assertThat(array(2, 3)).isNotEqualTo(array(3, 2));
    }

    @Test
    void isNotEqualTo_DifferentLengths() {
        assertThat(array(2, 3)).isNotEqualTo(array(2, 3, 1));
    }

    @Test
    void isNotEqualTo_DifferentTypes() {
        assertThat(array(2, 3)).isNotEqualTo(new Object());
    }

    @Test
    void isNotEqualTo_FailEquals() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(array(2, 3))
                        .isNotEqualTo(array(2, 3)));
    }

    @SuppressWarnings("TruthSelfEquals")
    @Test
    void isNotEqualTo_FailSame() {
        long[] same = array(2, 3);
        assertThrows(
                AssertionError.class,
                () -> assertThat(same)
                        .isNotEqualTo(same));
    }

    private static long[] array(long... ts) {
        return ts;
    }
}
