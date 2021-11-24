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
package com.google.common.truth;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.base.Strings.lenientFormat;
import static com.google.common.truth.Fact.simpleFact;
import static com.google.common.truth.Truth.assertThat;

/** Tests for {@link ExpectFailure} */
class ExpectFailureTest {
    private final ExpectFailure expectFailure = new ExpectFailure();

    @BeforeEach
    void setupExpectFailure() {
        expectFailure.enterRuleContext();
    }

    @Test
    void expectFail() {
        expectFailure.whenTesting().withMessage("abc").fail();
        assertThat(expectFailure.getFailure()).hasMessageThat().isEqualTo("abc");
    }

    @Test
    void expectFail_withCause() {
        expectFailure.whenTesting().that(new NullPointerException()).isNull();
        assertThat(expectFailure.getFailure()).hasMessageThat().contains("NullPointerException");
        assertThat(expectFailure.getFailure()).hasCauseThat().isInstanceOf(NullPointerException.class);
    }

    @Test
    void expectFail_about() {
        expectFailure.whenTesting().about(strings()).that("foo").isEqualTo("bar");
        assertThat(expectFailure.getFailure()).hasMessageThat().contains("foo");
    }

    @Test
    void expectFail_passesIfUnused() {
        assertThat(4).isEqualTo(4);
    }

    @Test
    void expectFail_failsOnSuccess() {
        expectFailure.whenTesting().that(4).isEqualTo(4);
        try {
            @SuppressWarnings("unused")
            AssertionError unused = expectFailure.getFailure();
            throw new Error("Expected to fail");
        } catch (AssertionError expected) {
            assertThat(expected).hasMessageThat().contains("ExpectFailure did not capture a failure.");
        }
    }

    @Test
    void expectFail_failsOnMultipleFailures() {
        try {
            expectFailure.whenTesting().about(BadSubject.badSubject()).that(5).isEqualTo(4);
            throw new Error("Expected to fail");
        } catch (AssertionError expected) {
            assertThat(expected).hasMessageThat().contains("caught multiple failures");
            assertThat(expected).hasMessageThat().contains("<4> is equal to <5>");
            assertThat(expected).hasMessageThat().contains("<5> is equal to <4>");
        }
    }

    @Test
    void expectFail_failsOnMultiplewhenTestings() {
        try {
            expectFailure.whenTesting().that(4).isEqualTo(4);
            StandardSubjectBuilder unused = expectFailure.whenTesting();
            throw new Error("Expected to fail");
        } catch (AssertionError expected) {
            assertThat(expected)
                    .hasMessageThat()
                    .contains(
                            "ExpectFailure.whenTesting() called previously, but did not capture a failure.");
        }
    }

    @Test
    void expectFail_failsOnMultiplewhenTestings_thatFail() {
        expectFailure.whenTesting().that(5).isEqualTo(4);
        try {
            StandardSubjectBuilder unused = expectFailure.whenTesting();
            throw new Error("Expected to fail");
        } catch (AssertionError expected) {
            assertThat(expected).hasMessageThat().contains("ExpectFailure already captured a failure");
        }
    }

    @Test
    void expectFail_failsAfterTest() {
        try {
            expectFailure.whenTesting().that(4).isEqualTo(4);
            expectFailure.ensureFailureCaught();
            throw new Error("Expected to fail");
        } catch (AssertionError expected) {
            assertThat(expected)
                    .hasMessageThat()
                    .contains("ExpectFailure.whenTesting() invoked, but no failure was caught.");
        }
    }

    private static Subject.Factory<StringSubject, String> strings() {
        return StringSubject::new;
    }

    private static class BadSubject extends Subject {
        private final Integer actual;

        BadSubject(FailureMetadata failureMetadat, Integer actual) {
            super(failureMetadat, actual);
            this.actual = actual;
        }

        @Override
        public void isEqualTo(Object expected) {
            if (!actual.equals(expected)) {
                failWithoutActual(
                        simpleFact(lenientFormat("expected <%s> is equal to <%s>", actual, expected)));
                failWithoutActual(
                        simpleFact(lenientFormat("expected <%s> is equal to <%s>", expected, actual)));
            }
        }

        private static Subject.Factory<BadSubject, Integer> badSubject() {
            return BadSubject::new;
        }
    }
}
