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
 * Tests for Boolean Subjects.
 *
 * @author Christian Gruber (cgruber@israfil.net)
 */
class BooleanSubjectTest extends BaseSubjectTestCase {

    @Test
    void isTrue() {
        assertThat(true).isTrue();
    }

    @Test
    void nullIsTrueFailing() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat((Boolean) null)
                        .isTrue());
        assertFailureKeys(
                failure,
                "expected", "but was");
        assertFailureValue(
                failure,
                "expected", "true");
        assertFailureValue(
                failure,
                "but was", "null");
    }

    @Test
    void nullIsFalseFailing() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat((Boolean) null)
                        .isFalse());
        assertFailureKeys(
                failure,
                "expected", "but was");
        assertFailureValue(
                failure,
                "expected", "false");
        assertFailureValue(
                failure,
                "but was", "null");
    }

    @Test
    void isTrueFailing() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(false)
                        .isTrue());
        assertFailureKeys(
                failure,
                "expected to be true");
    }

    @Test
    void isFalse() {
        assertThat(false).isFalse();
    }

    @Test
    void isFalseFailing() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(true)
                        .isFalse());
        assertFailureKeys(
                failure,
                "expected to be false");
    }
}
