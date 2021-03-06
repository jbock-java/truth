/*
 * Copyright (c) 2018 Google, Inc.
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

import java.util.List;

import static io.jbock.common.truth.Fact.fact;
import static io.jbock.common.truth.Fact.makeMessage;
import static io.jbock.common.truth.Fact.simpleFact;
import static io.jbock.common.truth.Truth.assertThat;

/** Tests for {@link Fact}. */
class FactTest {
    @Test
    void string() {
        assertThat(fact("foo", "bar").toString()).isEqualTo("foo: bar");
    }

    @Test
    void stringWithoutValue() {
        assertThat(simpleFact("foo").toString()).isEqualTo("foo");
    }

    @Test
    void oneFacts() {
        assertThat(makeMessage(List.of(), List.of(fact("foo", "bar"))))
                .isEqualTo("foo: bar");
    }

    @Test
    void twoFacts() {
        assertThat(
                makeMessage(
                        List.of(),
                        List.of(fact("foo", "bar"), fact("longer name", "other value"))))
                .isEqualTo("foo        : bar\nlonger name: other value");
    }

    @Test
    void oneFactWithoutValue() {
        assertThat(makeMessage(List.of(), List.of(simpleFact("foo"))))
                .isEqualTo("foo");
    }

    @Test
    void twoFactsOneWithoutValue() {
        assertThat(
                makeMessage(
                        List.of(),
                        List.of(fact("hello", "there"), simpleFact("foo"))))
                .isEqualTo("hello: there\nfoo");
    }

    @Test
    void newline() {
        assertThat(makeMessage(List.of(), List.of(fact("foo", "bar\nbaz"))))
                .isEqualTo("foo:\n    bar\n    baz");
    }

    @Test
    void newlineWithoutValue() {
        assertThat(
                makeMessage(
                        List.of(),
                        List.of(fact("hello", "there\neveryone"), simpleFact("xyz"))))
                .isEqualTo("hello:\n    there\n    everyone\nxyz");
    }

    @Test
    void withMessage() {
        assertThat(makeMessage(List.of("hello"), List.of(fact("foo", "bar"))))
                .isEqualTo("hello\nfoo: bar");
    }
}
