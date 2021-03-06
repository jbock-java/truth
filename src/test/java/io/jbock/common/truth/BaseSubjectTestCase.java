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

import static io.jbock.common.truth.ExpectFailure.assertThat;

/** Base class for truth subject tests to extend. */
abstract class BaseSubjectTestCase extends PlatformBaseSubjectTestCase {
    final void assertFailureKeys(
            AssertionError failure,
            String... keys) {
        assertThat(failure).factKeys().containsExactlyElementsIn(keys).inOrder();
    }

    final void assertFailureValue(
            AssertionError failure,
            String key,
            String value) {
        assertThat(failure).factValue(key).isEqualTo(value);
    }

    final void assertFailureValueIndexed(
            AssertionError failure,
            String key,
            int index,
            String value) {
        assertThatFailure(failure).factValue(key, index).isEqualTo(value);
    }

    final TruthFailureSubject assertThatFailure(AssertionError failure) {
        return assertThat(failure);
    }
}
