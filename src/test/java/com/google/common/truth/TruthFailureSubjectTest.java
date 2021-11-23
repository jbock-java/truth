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

package com.google.common.truth;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Fact.fact;
import static com.google.common.truth.Fact.simpleFact;
import static com.google.common.truth.TruthFailureSubject.HOW_TO_TEST_KEYS_WITHOUT_VALUES;
import static com.google.common.truth.TruthFailureSubject.truthFailures;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/** Tests for {@link TruthFailureSubject}. */
class TruthFailureSubjectTest extends BaseSubjectTestCase {
    // factKeys()

    @Test
    void factKeys() {
        assertThat(fact("foo", "the foo")).factKeys().containsExactly("foo");
    }

    @Test
    void factKeysNoValue() {
        assertThat(simpleFact("foo")).factKeys().containsExactly("foo");
    }

    @Test
    void factKeysFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> Truth.assertAbout(truthFailures())
                        .that(failure(fact("foo", "the foo")))
                        .factKeys()
                        .containsExactly("bar"));
        Truth.assertThat(failure)
                .hasMessageThat()
                .contains("value of: failure.factKeys()");
        // TODO(cpovirk): Switch to using fact-based assertions once IterableSubject uses them.
    }

    // factValue(String)

    @Test
    void factValue() {
        assertThat(fact("foo", "the foo")).factValue("foo").isEqualTo("the foo");
    }

    @Test
    void factValueFailWrongValue() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(fact("foo", "the foo"))
                        .factValue("foo")
                        .isEqualTo("the bar"));
        assertFailureValue(failure,
                "value of", "failure.factValue(foo)");
    }

    @Test
    void factValueFailNoSuchKey() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(fact("foo", "the foo"))
                        .factValue("bar"));
        assertFailureKeys(
                failure,
                "expected to contain fact", "but contained only");
        assertFailureValue(
                failure,
                "expected to contain fact", "bar");
        assertFailureValue(
                failure,
                "but contained only", "[foo]");
    }

    @Test
    void factValueFailMultipleKeys() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> Truth.assertAbout(truthFailures())
                        .that(failure(fact("foo", "the foo"), fact("foo", "the other foo")))
                        .factValue("foo"));
        assertFailureKeys(
                failure,
                "expected to contain a single fact with key", "but contained multiple");
        assertFailureValue(
                failure,
                "expected to contain a single fact with key", "foo");
        assertFailureValue(
                failure,
                "but contained multiple", "[foo: the foo, foo: the other foo]");
    }

    @Test
    void factValueFailNoValue() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> Truth.assertAbout(truthFailures())
                        .that(failure(simpleFact("foo")))
                        .factValue("foo"));
        assertFailureKeys(
                failure,
                "expected to have a value",
                "for key",
                "but the key was present with no value",
                HOW_TO_TEST_KEYS_WITHOUT_VALUES.key);
        assertFailureValue(
                failure,
                "for key", "foo");
    }

    // factValue(String, int)

    @Test
    void factValueInt() {
        assertThat(fact("foo", "the foo")).factValue("foo", 0).isEqualTo("the foo");
    }

    @Test
    void factValueIntMultipleKeys() {
        assertThat(fact("foo", "the foo"), fact("foo", "the other foo"))
                .factValue("foo", 1)
                .isEqualTo("the other foo");
    }

    @Test
    void factValueIntFailNegative() {
        try {
            assertThat(fact("foo", "the foo")).factValue("foo", -1);
            fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    void factValueIntFailWrongValue() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> Truth.assertAbout(truthFailures())
                        .that(failure(fact("foo", "the foo")))
                        .factValue("foo", 0)
                        .isEqualTo("the bar"));
        assertFailureValue(
                failure,
                "value of", "failure.factValue(foo, 0)");
    }

    @Test
    void factValueIntFailNoSuchKey() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> Truth.assertAbout(truthFailures())
                        .that(failure(fact("foo", "the foo")))
                        .factValue("bar", 0));
        assertFailureKeys(
                failure,
                "expected to contain fact", "but contained only");
        assertFailureValue(
                failure,
                "expected to contain fact", "bar");
        assertFailureValue(
                failure,
                "but contained only", "[foo]");
    }

    @Test
    void factValueIntFailNotEnoughWithKey() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> Truth.assertAbout(truthFailures())
                        .that(failure(fact("foo", "the foo")))
                        .factValue("foo", 5));
        assertFailureKeys(
                failure,
                "for key", "index too high", "fact count was");
        assertFailureValue(
                failure,
                "for key", "foo");
        assertFailureValue(
                failure,
                "index too high", "5");
        assertFailureValue(
                failure,
                "fact count was", "1");
    }

    @Test
    void factValueIntFailNoValue() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> Truth.assertAbout(truthFailures())
                        .that(failure(simpleFact("foo")))
                        .factValue("foo", 0));
        assertFailureKeys(
                failure,
                "expected to have a value",
                "for key",
                "and index",
                "but the key was present with no value",
                HOW_TO_TEST_KEYS_WITHOUT_VALUES.key);
        assertFailureValue(
                failure,
                "for key", "foo");
        assertFailureValue(
                failure,
                "and index", "0");
    }

    // other tests

    @Test
    void nonTruthErrorFactKeys() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> Truth.assertAbout(truthFailures())
                        .that(new AssertionError())
                        .factKeys());
        assertFailureKeys(
                failure,
                "expected a failure thrown by Truth's new failure API", "but was");
    }

    @Test
    void nonTruthErrorFactValue() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> Truth.assertAbout(truthFailures())
                        .that(new AssertionError())
                        .factValue("foo"));
        assertFailureKeys(
                failure,
                "expected a failure thrown by Truth's new failure API", "but was");
    }

    private TruthFailureSubject assertThat(Fact... facts) {
        return ExpectFailure.assertThat(failure(facts));
    }

    private AssertionErrorWithFacts failure(Fact... facts) {
        return new AssertionErrorWithFacts(
                ImmutableList.<String>of(), ImmutableList.copyOf(facts), /* cause= */ null);
    }
}
