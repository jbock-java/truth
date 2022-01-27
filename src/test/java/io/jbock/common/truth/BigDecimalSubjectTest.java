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
package io.jbock.common.truth;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.jbock.common.truth.Truth.assertThat;
import static java.math.BigDecimal.TEN;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for BigDecimal Subjects.
 *
 * @author Kurt Alfred Kluever
 */
class BigDecimalSubjectTest extends BaseSubjectTestCase {
    @SuppressWarnings("TruthSelfEquals")
    @Test
    void isEqualTo() {
        // make sure this still works
        assertThat(TEN).isEqualTo(TEN);
    }

    @Test
    void isEquivalentAccordingToCompareTo() {
        // make sure this still works
        assertThat(TEN).isEquivalentAccordingToCompareTo(TEN);
    }

    @Test
    void isEqualToIgnoringScale_bigDecimal() {
        assertThat(TEN).isEqualToIgnoringScale(TEN);
        assertThat(TEN).isEqualToIgnoringScale(new BigDecimal(10));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(TEN)
                        .isEqualToIgnoringScale(new BigDecimal(3)));
        assertFailureKeys(
                failure,
                "expected", "but was", "(scale is ignored)");
        assertFailureValue(
                failure,
                "expected", "3");
        assertFailureValue(
                failure,
                "but was", "10");
    }

    @Test
    void isEqualToIgnoringScale_int() {
        assertThat(TEN).isEqualToIgnoringScale(10);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(TEN)
                        .isEqualToIgnoringScale(3));
        assertFailureKeys(
                failure,
                "expected", "but was", "(scale is ignored)");
        assertFailureValue(
                failure,
                "expected", "3");
        assertFailureValue(
                failure,
                "but was", "10");
    }

    @Test
    void isEqualToIgnoringScale_long() {
        assertThat(TEN).isEqualToIgnoringScale(10L);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(TEN)
                        .isEqualToIgnoringScale(3L));
        assertFailureKeys(
                failure,
                "expected", "but was", "(scale is ignored)");
        assertFailureValue(
                failure,
                "expected", "3");
        assertFailureValue(
                failure,
                "but was", "10");
    }

    @Test
    void isEqualToIgnoringScale_string() {
        assertThat(TEN).isEqualToIgnoringScale("10");
        assertThat(TEN).isEqualToIgnoringScale("10.");
        assertThat(TEN).isEqualToIgnoringScale("10.0");
        assertThat(TEN).isEqualToIgnoringScale("10.00");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(TEN)
                        .isEqualToIgnoringScale("3"));
        assertFailureKeys(
                failure,
                "expected", "but was", "(scale is ignored)");
        assertFailureValue(
                failure,
                "expected", "3");
        assertFailureValue(
                failure,
                "but was", "10");
    }
}
