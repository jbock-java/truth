/*
 * Copyright (c) 2017 Google, Inc.
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

import static io.jbock.common.truth.ExpectFailure.assertThat;
import static io.jbock.common.truth.ExpectFailure.expectFailure;
import static io.jbock.common.truth.ExpectFailure.expectFailureAbout;

/** Tests of {@link ExpectFailure}'s Java 8 support. */
final class ExpectFailure8Test {

    @Test
    void testExpectFailure() {
        AssertionError failure1 = expectFailure(whenTesting -> whenTesting.that(4).isEqualTo(5));
        assertThat(failure1).factValue("expected").isEqualTo("5");

        // verify multiple independent failures can be caught in the same test
        AssertionError failure2 = expectFailure(whenTesting -> whenTesting.that(5).isEqualTo(4));
        assertThat(failure2).factValue("expected").isEqualTo("4");
    }

    @Test
    void testExpectFailureAbout() {
        expectFailureAbout(
                STRINGS,
                whenTesting -> whenTesting.that("foo").contains("bar"));
    }

    private static final Subject.Factory<StringSubject, String> STRINGS = StringSubject::new;
}
