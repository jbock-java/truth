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
 * Tests for introspective Subject behaviour.
 *
 * @author Christian Gruber (cgruber@israfil.net)
 */
class ClassSubjectTest extends BaseSubjectTestCase {
    @Test
    void testIsAssignableTo_same() {
        assertThat(String.class).isAssignableTo(String.class);
    }

    @Test
    void testIsAssignableTo_parent() {
        assertThat(String.class).isAssignableTo(Object.class);
        assertThat(NullPointerException.class).isAssignableTo(Exception.class);
    }

    @Test
    void testIsAssignableTo_reversed() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(Object.class)
                        .isAssignableTo(String.class));
        assertFailureValue(
                failure,
                "expected to be assignable to", "java.lang.String");
    }

    @Test
    void testIsAssignableTo_differentTypes() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(String.class)
                        .isAssignableTo(Exception.class));
        assertFailureValue(
                failure,
                "expected to be assignable to", "java.lang.Exception");
    }
}
