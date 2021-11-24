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
 * Tests for {@link com.google.common.truth.PrimitiveCharArraySubject}.
 *
 * @author Christian Gruber (cgruber@israfil.net)
 */
class PrimitiveCharArraySubjectTest extends BaseSubjectTestCase {

    @Test
    void isEqualTo() {
        assertThat(array('a', 'q')).isEqualTo(array('a', 'q'));
    }

    @SuppressWarnings("TruthSelfEquals")
    @Test
    void isEqualTo_Same() {
        char[] same = array('a', 'q');
        assertThat(same).isEqualTo(same);
    }

    @Test
    void asList() {
        assertThat(array('a', 'q', 'z')).asList().containsAtLeast('a', 'z');
    }

    @Test
    void isEqualTo_Fail_UnequalOrdering() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array('a', 'q'))
                        .isEqualTo(array('q', 'a')));
        assertFailureKeys(
                failure,
                "expected", "but was", "differs at index");
        assertFailureValue(
                failure,
                "expected", "[q, a]");
        assertFailureValue(
                failure,
                "but was", "[a, q]");
        assertFailureValue(
                failure,
                "differs at index", "[0]");
    }

    @Test
    void isEqualTo_Fail_DifferentKindOfArray() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array('a', 'q'))
                        .isEqualTo(new int[]{}));
        assertFailureKeys(
                failure,
                "expected", "but was", "wrong type", "expected", "but was");
        assertFailureValueIndexed(
                failure,
                "expected", 1, "int[]");
        assertFailureValueIndexed(
                failure,
                "but was", 1, "char[]");
    }

    @Test
    void isNotEqualTo_SameLengths() {
        assertThat(array('a', 'q')).isNotEqualTo(array('q', 'a'));
    }

    @Test
    void isNotEqualTo_DifferentLengths() {
        assertThat(array('a', 'q')).isNotEqualTo(array('q', 'a', 'b'));
    }

    @Test
    void isNotEqualTo_DifferentTypes() {
        assertThat(array('a', 'q')).isNotEqualTo(new Object());
    }

    @Test
    void isNotEqualTo_FailEquals() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(array('a', 'q'))
                        .isNotEqualTo(array('a', 'q')));
    }

    @SuppressWarnings("TruthSelfEquals")
    @Test
    void isNotEqualTo_FailSame() {
        char[] same = array('a', 'q');
        assertThrows(
                AssertionError.class,
                () -> assertThat(same)
                        .isNotEqualTo(same));
    }

    private static char[] array(char... ts) {
        return ts;
    }
}
