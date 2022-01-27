/*
 * Copyright (c) 2016 Google, Inc.
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

import java.util.OptionalDouble;

import static io.jbock.common.truth.ExpectFailure.assertThat;
import static io.jbock.common.truth.OptionalDoubleSubject.optionalDoubles;
import static io.jbock.common.truth.Truth8.assertThat;

/**
 * Tests for Java 8 {@link OptionalDouble} Subjects.
 *
 * @author Ben Douglass
 */
class OptionalDoubleSubjectTest {

    @Test
    void failOnNullSubject() {
        AssertionError expected = expectFailure(whenTesting -> whenTesting.that(null).isEmpty());
        assertThat(expected).factKeys().containsExactly("expected empty optional", "but was").inOrder();
    }

    @Test
    void isPresent() {
        assertThat(OptionalDouble.of(1337.0)).isPresent();
    }

    @Test
    void isPresentFailing() {
        AssertionError expected =
                expectFailure(whenTesting -> whenTesting.that(OptionalDouble.empty()).isPresent());
        assertThat(expected).factKeys().containsExactly("expected to be present");
    }

    @Test
    void isEmpty() {
        assertThat(OptionalDouble.empty()).isEmpty();
    }

    @Test
    void isEmptyFailing() {
        AssertionError expected =
                expectFailure(whenTesting -> whenTesting.that(OptionalDouble.of(1337.0)).isEmpty());
        assertThat(expected).factKeys().contains("expected to be empty");
        assertThat(expected).factValue("but was present with value").isEqualTo("1337.0");
    }

    @Test
    void isEmptyFailingNull() {
        AssertionError expected = expectFailure(whenTesting -> whenTesting.that(null).isEmpty());
        assertThat(expected).factKeys().containsExactly("expected empty optional", "but was").inOrder();
    }

    @Test
    void hasValue() {
        assertThat(OptionalDouble.of(1337.0)).hasValue(1337.0);
    }

    @Test
    void hasValue_FailingWithEmpty() {
        AssertionError expected =
                expectFailure(whenTesting -> whenTesting.that(OptionalDouble.empty()).hasValue(1337.0));
        assertThat(expected)
                .factKeys()
                .containsExactly("expected to have value", "but was absent")
                .inOrder();
        assertThat(expected).factValue("expected to have value").isEqualTo("1337.0");
    }

    @Test
    void hasValue_FailingWithWrongValue() {
        AssertionError expected =
                expectFailure(whenTesting -> whenTesting.that(OptionalDouble.of(1337.0)).hasValue(42.0));
        assertThat(expected).factValue("value of").isEqualTo("optionalDouble.getAsDouble()");
    }

    private static AssertionError expectFailure(
            ExpectFailure.SimpleSubjectBuilderCallback<OptionalDoubleSubject, OptionalDouble>
                    assertionCallback) {
        return ExpectFailure.expectFailureAbout(optionalDoubles(), assertionCallback);
    }
}
