/*
 * Copyright (c) 2015 Google, Inc.
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
 * Tests for {@link PrimitiveShortArraySubject}.
 *
 * @author Christian Gruber (cgruber@israfil.net)
 */
class PrimitiveShortArraySubjectTest extends BaseSubjectTestCase {

    @Test
    void isEqualTo() {
        assertThat(array(1, 0, 1)).isEqualTo(array(1, 0, 1));
    }

    @SuppressWarnings("TruthSelfEquals")
    @Test
    void isEqualTo_Same() {
        short[] same = array(1, 0, 1);
        assertThat(same).isEqualTo(same);
    }

    @Test
    void asList() {
        assertThat(array(1, 1, 0)).asList().containsAtLeast((short) 1, (short) 0);
    }

    @Test
    void asListWithoutCastingFails() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1, 1, 0))
                        .asList()
                        .containsAtLeast(1, 0));
        assertFailureKeys(
                failure,
                "value of",
                "missing (2)",
                "though it did contain (3)",
                "---",
                "expected to contain at least",
                "but was");
    }

    @Test
    void isEqualTo_Fail_UnequalOrdering() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1, 0, 1))
                        .isEqualTo(array(0, 1, 1)));
        assertFailureKeys(
                failure,
                "expected", "but was", "differs at index");
        assertFailureValue(
                failure,
                "expected", "[0, 1, 1]");
        assertFailureValue(
                failure,
                "but was", "[1, 0, 1]");
        assertFailureValue(
                failure,
                "differs at index", "[0]");
    }

    @Test
    void isEqualTo_Fail_NotAnArray() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(array(1, 0, 1))
                        .isEqualTo(new Object()));
    }

    @Test
    void isNotEqualTo_SameLengths() {
        assertThat(array(1, 0)).isNotEqualTo(array(1, 1));
    }

    @Test
    void isNotEqualTo_DifferentLengths() {
        assertThat(array(1, 0)).isNotEqualTo(array(1, 0, 1));
    }

    @Test
    void isNotEqualTo_DifferentTypes() {
        assertThat(array(1, 0)).isNotEqualTo(new Object());
    }

    @Test
    void isNotEqualTo_FailEquals() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(array(1, 0))
                        .isNotEqualTo(array(1, 0)));
    }

    @SuppressWarnings("TruthSelfEquals")
    @Test
    void isNotEqualTo_FailSame() {
        short[] same = array(1, 0);
        assertThrows(
                AssertionError.class,
                () -> assertThat(same)
                        .isNotEqualTo(same));
    }

    private static short[] array(int a, int b, int c) {
        return new short[]{(short) a, (short) b, (short) c};
    }

    private static short[] array(int a, int b) {
        return new short[]{(short) a, (short) b};
    }
}
