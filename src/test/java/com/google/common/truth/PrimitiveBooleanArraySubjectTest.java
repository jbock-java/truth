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
 * Tests for {@link com.google.common.truth.PrimitiveBooleanArraySubject}.
 *
 * @author Christian Gruber (cgruber@israfil.net)
 */
class PrimitiveBooleanArraySubjectTest extends BaseSubjectTestCase {

    @Test
    void isEqualTo() {
        assertThat(array(true, false, true)).isEqualTo(array(true, false, true));
    }

    @SuppressWarnings("TruthSelfEquals")
    @Test
    void isEqualTo_Same() {
        boolean[] same = array(true, false, true);
        assertThat(same).isEqualTo(same);
    }

    @Test
    void asList() {
        assertThat(array(true, true, false)).asList().containsAtLeast(true, false);
    }

    @Test
    void isEqualTo_Fail_UnequalOrdering() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(true, false, true))
                        .isEqualTo(array(false, true, true)));
        assertFailureValue(
                failure,
                "differs at index", "[0]");
    }

    @Test
    void isEqualTo_Fail_NotAnArray() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(array(true, false, true))
                        .isEqualTo(new Object()));
    }

    @Test
    void isNotEqualTo_SameLengths() {
        assertThat(array(true, false)).isNotEqualTo(array(true, true));
    }

    @Test
    void isNotEqualTo_DifferentLengths() {
        assertThat(array(true, false)).isNotEqualTo(array(true, false, true));
    }

    @Test
    void isNotEqualTo_DifferentTypes() {
        assertThat(array(true, false)).isNotEqualTo(new Object());
    }

    @Test
    void isNotEqualTo_FailEquals() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(array(true, false))
                        .isNotEqualTo(array(true, false)));
    }

    @SuppressWarnings("TruthSelfEquals")
    @Test
    void isNotEqualTo_FailSame() {
        boolean[] same = array(true, false);
        assertThrows(
                AssertionError.class,
                () -> assertThat(same)
                        .isNotEqualTo(same));
    }

    private static boolean[] array(boolean... ts) {
        return ts;
    }
}
