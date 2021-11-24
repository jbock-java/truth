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

import com.google.common.base.Optional;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for Guava {@link Optional} Subjects.
 *
 * @author Christian Gruber (cgruber@israfil.net)
 */
class GuavaOptionalSubjectTest extends BaseSubjectTestCase {

    @Test
    void isPresent() {
        assertThat(Optional.of("foo")).isPresent();
    }

    @Test
    void isPresentFailing() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(Optional.absent())
                        .isPresent());
        assertFailureKeys(
                failure,
                "expected to be present");
    }

    @Test
    void isPresentFailingNull() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat((Optional<?>) null)
                        .isPresent());
        assertFailureKeys(
                failure,
                "expected present optional", "but was");
    }

    @Test
    void isAbsent() {
        assertThat(Optional.absent()).isAbsent();
    }

    @Test
    void isAbsentFailing() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(Optional.of("foo"))
                        .isAbsent());
        assertFailureKeys(
                failure,
                "expected to be absent", "but was present with value");
        assertFailureValue(
                failure,
                "but was present with value", "foo");
    }

    @Test
    void isAbsentFailingNull() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat((Optional<?>) null)
                        .isAbsent());
        assertFailureKeys(
                failure,
                "expected absent optional", "but was");
    }

    @Test
    void hasValue() {
        assertThat(Optional.of("foo")).hasValue("foo");
    }

    @Test
    void hasValue_failingWithAbsent() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(Optional.absent())
                        .hasValue("foo"));
        assertFailureKeys(
                failure,
                "expected to have value", "but was absent");
        assertFailureValue(
                failure,
                "expected to have value", "foo");
    }

    @Test
    void hasValue_npeWithNullParameter() {
        try {
            assertThat(Optional.of("foo")).hasValue(null);
            fail("Expected NPE");
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessageThat().contains("Optional");
        }
    }

    @Test
    void hasValue_failingWithWrongValue() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(Optional.of("foo"))
                        .hasValue("boo"));
        assertFailureValue(
                failure,
                "value of", "optional.get()");
    }
}
