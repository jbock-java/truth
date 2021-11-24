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
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for {@link com.google.common.truth.PrimitiveIntArraySubject}.
 *
 * @author Christian Gruber (cgruber@israfil.net)
 */
class PrimitiveIntArraySubjectTest extends BaseSubjectTestCase {
    private static final int[] EMPTY = new int[0];

    @Test
    void isEqualTo() {
        assertThat(array(2, 5)).isEqualTo(array(2, 5));
    }

    @SuppressWarnings("TruthSelfEquals")
    @Test
    void isEqualTo_Same() {
        int[] same = array(2, 5);
        assertThat(same).isEqualTo(same);
    }

    @Test
    void asList() {
        assertThat(array(5, 2, 9)).asList().containsAtLeast(2, 9);
    }

    @Test
    void hasLength() {
        assertThat(EMPTY).hasLength(0);
        assertThat(array(2, 5)).hasLength(2);
    }

    @Test
    void hasLengthFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(2, 5))
                        .hasLength(1));
        assertFailureValue(
                failure,
                "value of", "array.length");
    }

    @Test
    void hasLengthNegative() {
        try {
            assertThat(array(2, 5)).hasLength(-1);
            fail("Should have failed.");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    void isEmpty() {
        assertThat(EMPTY).isEmpty();
    }

    @Test
    void isEmptyFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(2, 5))
                        .isEmpty());
        assertFailureKeys(
                failure,
                "expected to be empty", "but was");
    }

    @Test
    void isNotEmpty() {
        assertThat(array(2, 5)).isNotEmpty();
    }

    @Test
    void isNotEmptyFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(EMPTY)
                        .isNotEmpty());
        assertFailureKeys(
                failure,
                "expected not to be empty");
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
        assertThrows(
                AssertionError.class,
                () -> assertThat(array(2, 3, 4))
                        .isEqualTo(new Object()));
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
        int[] same = array(2, 3);
        assertThrows(
                AssertionError.class,
                () -> assertThat(same)
                        .isNotEqualTo(same));
    }

    private static int[] array(int... ts) {
        return ts;
    }
}
