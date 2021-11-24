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

import com.google.common.collect.ImmutableMultiset;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for Multiset Subjects.
 *
 * @author Kurt Alfred Kluever
 */
class MultisetSubjectTest extends BaseSubjectTestCase {

    @Test
    void hasCount() {
        ImmutableMultiset<String> multiset = ImmutableMultiset.of("kurt", "kurt", "kluever");
        assertThat(multiset).hasCount("kurt", 2);
        assertThat(multiset).hasCount("kluever", 1);
        assertThat(multiset).hasCount("alfred", 0);

        assertWithMessage("name").that(multiset).hasCount("kurt", 2);
    }

    @Test
    void hasCountFail() {
        ImmutableMultiset<String> multiset = ImmutableMultiset.of("kurt", "kurt", "kluever");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(multiset)
                        .hasCount("kurt", 3));
        assertFailureValue(
                failure,
                "value of", "multiset.count(kurt)");
    }
}
