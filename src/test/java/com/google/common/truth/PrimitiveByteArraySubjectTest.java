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
 * Tests for {@link com.google.common.truth.PrimitiveByteArraySubject}.
 *
 * @author Kurt Alfred Kluever
 */
class PrimitiveByteArraySubjectTest extends BaseSubjectTestCase {
    private static final byte BYTE_0 = (byte) 0;
    private static final byte BYTE_1 = (byte) 1;
    private static final byte BYTE_2 = (byte) 2;

    @Test
    void isEqualTo() {
        assertThat(array(BYTE_0, BYTE_1)).isEqualTo(array(BYTE_0, BYTE_1));
    }

    @SuppressWarnings("TruthSelfEquals")
    @Test
    void isEqualTo_Same() {
        byte[] same = array(BYTE_0, BYTE_1);
        assertThat(same).isEqualTo(same);
    }

    @Test
    void asList() {
        assertThat(array(BYTE_0, BYTE_1, BYTE_2)).asList().containsAtLeast(BYTE_0, BYTE_2);
    }

    @Test
    void isEqualTo_Fail_UnequalOrdering() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(BYTE_0, (byte) 123))
                        .isEqualTo(array((byte) 123, BYTE_0)));
        assertFailureKeys(
                failure,
                "expected", "but was", "expected", "but was");
        assertFailureValueIndexed(
                failure,
                "expected", 0, "7B00");
        assertFailureValueIndexed(
                failure,
                "but was", 0, "007B");
        assertFailureValueIndexed(
                failure,
                "expected", 1, "[123, 0]");
        assertFailureValueIndexed(
                failure,
                "but was", 1, "[0, 123]");
        assertThat(failure).isInstanceOf(ComparisonFailureWithFacts.class);
    }

    @Test
    void isEqualTo_Fail_NotAnArray() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(BYTE_0, BYTE_1))
                        .isEqualTo(new int[]{}));
        assertFailureKeys(
                failure,
                "expected", "but was", "wrong type", "expected", "but was");
        assertFailureValueIndexed(
                failure,
                "expected", 1, "int[]");
        assertFailureValueIndexed(
                failure,
                "but was", 1, "byte[]");
    }

    @Test
    void isNotEqualTo_SameLengths() {
        assertThat(array(BYTE_0, BYTE_1)).isNotEqualTo(array(BYTE_1, BYTE_0));
    }

    @Test
    void isNotEqualTo_DifferentLengths() {
        assertThat(array(BYTE_0, BYTE_1)).isNotEqualTo(array(BYTE_1, BYTE_0, BYTE_2));
    }

    @Test
    void isNotEqualTo_DifferentTypes() {
        assertThat(array(BYTE_0, BYTE_1)).isNotEqualTo(new Object());
    }

    @Test
    void isNotEqualTo_FailEquals() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(array(BYTE_0, BYTE_1))
                        .isNotEqualTo(array(BYTE_0, BYTE_1)));
    }

    @SuppressWarnings("TruthSelfEquals")
    @Test
    void isNotEqualTo_FailSame() {
        byte[] same = array(BYTE_0, BYTE_1);
        assertThrows(
                AssertionError.class,
                () -> assertThat(same)
                        .isNotEqualTo(same));
    }

    private static byte[] array(byte... ts) {
        return ts;
    }
}
