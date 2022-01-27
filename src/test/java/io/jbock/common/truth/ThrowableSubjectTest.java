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
package io.jbock.common.truth;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.jbock.common.truth.Truth.assertThat;
import static io.jbock.common.truth.Truth.assertWithMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link Throwable} subjects.
 *
 * @author Kurt Alfred Kluever
 */
class ThrowableSubjectTest extends BaseSubjectTestCase {

    @Test
    void hasMessageThat() {
        NullPointerException npe = new NullPointerException("message");
        assertThat(npe).hasMessageThat().isEqualTo("message");
    }

    @Test
    void hasMessageThat_null() {
        assertThat(new NullPointerException()).hasMessageThat().isNull();
        assertThat(new NullPointerException(null)).hasMessageThat().isNull();
    }

    @Test
    void hasMessageThat_failure() {
        NullPointerException actual = new NullPointerException("message");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .hasMessageThat()
                        .isEqualTo("foobar"));
        assertFailureValue(
                failure,
                "value of", "throwable.getMessage()");
        assertErrorHasActualAsCause(actual, failure);
    }

    @Test
    void hasMessageThat_MessageHasNullMessage_failure() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(new NullPointerException("message"))
                        .hasMessageThat()
                        .isNull());
    }

    @Test
    void hasMessageThat_NullMessageHasMessage_failure() {
        NullPointerException npe = new NullPointerException(null);
        assertThrows(
                AssertionError.class,
                () -> assertThat(npe)
                        .hasMessageThat()
                        .isEqualTo("message"));
    }

    @Test
    void hasCauseThat_message() {
        assertThat(new Exception("foobar", new IOException("barfoo")))
                .hasCauseThat()
                .hasMessageThat()
                .isEqualTo("barfoo");
    }

    @Test
    void hasCauseThat_instanceOf() {
        assertThat(new Exception("foobar", new IOException("barfoo")))
                .hasCauseThat()
                .isInstanceOf(IOException.class);
    }

    @Test
    void hasCauseThat_null() {
        assertThat(new Exception("foobar")).hasCauseThat().isNull();
    }

    @Test
    void hasCauseThat_message_failure() {
        Exception actual = new Exception("foobar", new IOException("barfoo"));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .hasCauseThat()
                        .hasMessageThat()
                        .isEqualTo("message"));
        assertFailureValue(
                failure,
                "value of", "throwable.getCause().getMessage()");
        assertErrorHasActualAsCause(actual, failure);
    }

    @Test
    void hasCauseThat_instanceOf_failure() {
        Exception actual = new Exception("foobar", new IOException("barfoo"));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .hasCauseThat()
                        .isInstanceOf(RuntimeException.class));
        assertFailureValue(
                failure,
                "value of", "throwable.getCause()");
        assertErrorHasActualAsCause(actual, failure);
    }

    @Test
    void hasCauseThat_tooDeep_failure() {
        Exception actual = new Exception("foobar");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .hasCauseThat()
                        .hasCauseThat()
                        .isNull());
        assertThat(failure.getMessage())
                .isEqualTo(
                        "Causal chain is not deep enough - add a .isNotNull() check?\n"
                                + "value of: throwable.getCause().getCause()");
        assertErrorHasActualAsCause(actual, failure);
    }

    @Test
    void hasCauseThat_deepNull_failure() {
        Exception actual =
                new Exception("foobar", new RuntimeException("barfoo", new IOException("buzz")));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .hasCauseThat()
                        .hasCauseThat()
                        .hasMessageThat()
                        .isEqualTo("message"));
        assertFailureValue(
                failure,
                "value of", "throwable.getCause().getCause().getMessage()");
        assertErrorHasActualAsCause(actual, failure);
    }

    @Test
    void inheritedMethodChainsSubject() {
        NullPointerException expected = new NullPointerException("expected");
        NullPointerException actual = new NullPointerException("actual");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .isEqualTo(expected));
        assertErrorHasActualAsCause(actual, failure);
    }

    private static void assertErrorHasActualAsCause(Throwable actual, AssertionError failure) {
        assertWithMessage("AssertionError's cause").that(failure.getCause()).isEqualTo(actual);
    }
}
