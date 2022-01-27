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

import java.util.List;
import java.util.stream.LongStream;

import static io.jbock.common.truth.FailureAssertions.assertFailureKeys;
import static io.jbock.common.truth.FailureAssertions.assertFailureValue;
import static io.jbock.common.truth.LongStreamSubject.longStreams;
import static io.jbock.common.truth.Truth8.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for Java 8 {@link LongStream} Subjects.
 *
 * @author Kurt Alfred Kluever
 */
final class LongStreamSubjectTest {

    @Test
    void testIsEqualTo() {
        LongStream stream = LongStream.of(42);
        assertThat(stream).isEqualTo(stream);
    }

    @Test
    void testIsEqualToList() {
        LongStream stream = LongStream.of(42);
        List<Long> list = asList(42L);
        AssertionError unused = expectFailure(whenTesting -> whenTesting.that(stream).isEqualTo(list));
    }

    @Test
    void testNullStream_fails() {
        LongStream nullStream = null;
        try {
            assertThat(nullStream).isEmpty();
            fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    void testNullStreamIsNull() {
        LongStream nullStream = null;
        assertThat(nullStream).isNull();
    }

    @Test
    void testIsSameInstanceAs() {
        LongStream stream = LongStream.of(1);
        assertThat(stream).isSameInstanceAs(stream);
    }

    @Test
    void testIsEmpty() {
        assertThat(LongStream.of()).isEmpty();
    }

    @Test
    void testIsEmpty_fails() {
        AssertionError unused =
                expectFailure(whenTesting -> whenTesting.that(LongStream.of(42)).isEmpty());
    }

    @Test
    void testIsNotEmpty() {
        assertThat(LongStream.of(42)).isNotEmpty();
    }

    @Test
    void testIsNotEmpty_fails() {
        AssertionError unused =
                expectFailure(whenTesting -> whenTesting.that(LongStream.of()).isNotEmpty());
    }

    @Test
    void testHasSize() {
        assertThat(LongStream.of(42)).hasSize(1);
    }

    @Test
    void testHasSize_fails() {
        AssertionError unused =
                expectFailure(whenTesting -> whenTesting.that(LongStream.of(42)).hasSize(2));
    }

    @Test
    void testContainsNoDuplicates() {
        assertThat(LongStream.of(42)).containsNoDuplicates();
    }

    @Test
    void testContainsNoDuplicates_fails() {
        AssertionError unused =
                expectFailure(
                        whenTesting -> whenTesting.that(LongStream.of(42, 42)).containsNoDuplicates());
    }

    @Test
    void testContains() {
        assertThat(LongStream.of(42)).contains(42);
    }

    @Test
    void testContains_fails() {
        AssertionError unused =
                expectFailure(whenTesting -> whenTesting.that(LongStream.of(42)).contains(100));
    }

    @Test
    void testContainsAnyOf() {
        assertThat(LongStream.of(42)).containsAnyOf(42, 43);
    }

    @Test
    void testContainsAnyOf_fails() {
        AssertionError unused =
                expectFailure(whenTesting -> whenTesting.that(LongStream.of(42)).containsAnyOf(43, 44));
    }

    @Test
    void testContainsAnyIn() {
        assertThat(LongStream.of(42)).containsAnyIn(asList(42L, 43L));
    }

    @Test
    void testContainsAnyIn_fails() {
        AssertionError unused =
                expectFailure(
                        whenTesting -> whenTesting.that(LongStream.of(42)).containsAnyIn(asList(43, 44)));
    }

    @Test
    void testDoesNotContain() {
        assertThat(LongStream.of(42)).doesNotContain(43);
    }

    @Test
    void testDoesNotContain_fails() {
        AssertionError unused =
                expectFailure(whenTesting -> whenTesting.that(LongStream.of(42)).doesNotContain(42));
    }

    @Test
    void testContainsNoneOf() {
        assertThat(LongStream.of(42)).containsNoneOf(43, 44);
    }

    @Test
    void testContainsNoneOf_fails() {
        AssertionError unused =
                expectFailure(whenTesting -> whenTesting.that(LongStream.of(42)).containsNoneOf(42, 43));
    }

    @Test
    void testContainsNoneIn() {
        assertThat(LongStream.of(42)).containsNoneIn(asList(43, 44));
    }

    @Test
    void testContainsNoneIn_fails() {
        AssertionError unused =
                expectFailure(
                        whenTesting -> whenTesting.that(LongStream.of(42)).containsNoneIn(asList(42L, 43L)));
    }

    @Test
    void testContainsAtLeast() {
        assertThat(LongStream.of(42, 43)).containsAtLeast(42, 43);
    }

    @Test
    void testContainsAtLeast_fails() {
        AssertionError unused =
                expectFailure(
                        whenTesting -> whenTesting.that(LongStream.of(42, 43)).containsAtLeast(42, 43, 44));
    }

    @Test
    void testContainsAtLeast_inOrder() {
        assertThat(LongStream.of(42, 43)).containsAtLeast(42, 43).inOrder();
    }

    @Test
    void testContainsAtLeast_inOrder_fails() {
        try {
            assertThat(LongStream.of(42, 43)).containsAtLeast(43, 42).inOrder();
            fail();
        } catch (AssertionError expected) {
            assertFailureKeys(
                    expected,
                    "required elements were all found, but order was wrong",
                    "expected order for required elements",
                    "but was");
            assertFailureValue(expected, "expected order for required elements", "[43, 42]");
        }
    }

    @Test
    void testContainsAtLeastElementsIn() {
        assertThat(LongStream.of(42, 43)).containsAtLeastElementsIn(asList(42L, 43L));
    }

    @Test
    void testContainsAtLeastElementsIn_fails() {
        AssertionError unused =
                expectFailure(
                        whenTesting ->
                                whenTesting
                                        .that(LongStream.of(42, 43))
                                        .containsAtLeastElementsIn(asList(42L, 43L, 44L)));
    }

    @Test
    void testContainsAtLeastElementsIn_wrongType_fails() {
        AssertionError unused =
                expectFailure(
                        whenTesting ->
                                whenTesting
                                        .that(LongStream.of(42, 43))
                                        .containsAtLeastElementsIn(asList(42, 43, 44)));
    }

    @Test
    void testContainsAtLeastElementsIn_inOrder() {
        assertThat(LongStream.of(42, 43)).containsAtLeastElementsIn(asList(42L, 43L)).inOrder();
    }

    @Test
    void testContainsAtLeastElementsIn_inOrder_fails() {
        try {
            assertThat(LongStream.of(42, 43)).containsAtLeastElementsIn(asList(43L, 42L)).inOrder();
            fail();
        } catch (AssertionError expected) {
            assertFailureKeys(
                    expected,
                    "required elements were all found, but order was wrong",
                    "expected order for required elements",
                    "but was");
            assertFailureValue(expected, "expected order for required elements", "[43, 42]");
        }
    }

    @Test
    void testContainsAtLeastElementsIn_inOrder_wrongType_fails() {
        AssertionError unused =
                expectFailure(
                        whenTesting ->
                                whenTesting
                                        .that(LongStream.of(42, 43))
                                        .containsAtLeastElementsIn(asList(43, 42))
                                        .inOrder());
    }

    @Test
    void testContainsExactly() {
        assertThat(LongStream.of(42, 43)).containsExactly(42, 43);
    }

    @Test
    void testContainsExactly_fails() {
        try {
            assertThat(LongStream.of(42, 43)).containsExactly(42);
            fail();
        } catch (AssertionError expected) {
            assertFailureKeys(expected, "unexpected (1)", "---", "expected", "but was");
            assertFailureValue(expected, "expected", "[42]");
        }
    }

    @Test
    void testContainsExactly_inOrder() {
        assertThat(LongStream.of(42, 43)).containsExactly(42, 43).inOrder();
    }

    @Test
    void testContainsExactly_inOrder_fails() {
        try {
            assertThat(LongStream.of(42, 43)).containsExactly(43, 42).inOrder();
            fail();
        } catch (AssertionError expected) {
            assertFailureKeys(expected, "contents match, but order was wrong", "expected", "but was");
            assertFailureValue(expected, "expected", "[43, 42]");
        }
    }

    @Test
    void testContainsExactlyElementsIn() {
        assertThat(LongStream.of(42, 43)).containsExactlyElementsIn(asList(42L, 43L));
        assertThat(LongStream.of(42, 43)).containsExactlyElementsIn(asList(43L, 42L));
    }

    @Test
    void testContainsExactlyElementsIn_fails() {
        try {
            assertThat(LongStream.of(42, 43)).containsExactlyElementsIn(asList(42L));
            fail();
        } catch (AssertionError expected) {
            assertFailureKeys(expected, "unexpected (1)", "---", "expected", "but was");
            assertFailureValue(expected, "expected", "[42]");
        }
    }

    @Test
    void testContainsExactlyElementsIn_wrongType_fails() {
        AssertionError unused =
                expectFailure(
                        whenTesting ->
                                whenTesting.that(LongStream.of(42, 43)).containsExactlyElementsIn(asList(42)));
    }

    @Test
    void testContainsExactlyElementsIn_inOrder() {
        assertThat(LongStream.of(42, 43)).containsExactlyElementsIn(asList(42L, 43L)).inOrder();
    }

    @Test
    void testContainsExactlyElementsIn_inOrder_fails() {
        try {
            assertThat(LongStream.of(42, 43)).containsExactlyElementsIn(asList(43L, 42L)).inOrder();
            fail();
        } catch (AssertionError expected) {
            assertFailureKeys(expected, "contents match, but order was wrong", "expected", "but was");
            assertFailureValue(expected, "expected", "[43, 42]");
        }
    }

    @Test
    void testContainsExactlyElementsIn_inOrder_wrongType_fails() {
        AssertionError unused =
                expectFailure(
                        whenTesting ->
                                whenTesting
                                        .that(LongStream.of(42, 43))
                                        .containsExactlyElementsIn(asList(43, 42))
                                        .inOrder());
    }

    @Test
    void testContainsExactlyElementsIn_inOrder_LongStream() {
        assertThat(LongStream.of(1, 2, 3, 4)).containsExactly(1, 2, 3, 4).inOrder();
    }

    @Test
    void testIsInOrder() {
        assertThat(LongStream.of()).isInOrder();
        assertThat(LongStream.of(1)).isInOrder();
        assertThat(LongStream.of(1, 1, 2, 3, 3, 3, 4)).isInOrder();
    }

    @Test
    void testIsInOrder_fails() {
        AssertionError unused =
                expectFailure(whenTesting -> whenTesting.that(LongStream.of(1, 3, 2, 4)).isInOrder());
    }

    @Test
    void testIsInStrictOrder() {
        assertThat(LongStream.of()).isInStrictOrder();
        assertThat(LongStream.of(1)).isInStrictOrder();
        assertThat(LongStream.of(1, 2, 3, 4)).isInStrictOrder();
    }

    @Test
    void testIsInStrictOrder_fails() {
        AssertionError unused =
                expectFailure(whenTesting -> whenTesting.that(LongStream.of(1, 2, 2, 4)).isInStrictOrder());
    }

    private static AssertionError expectFailure(
            ExpectFailure.SimpleSubjectBuilderCallback<LongStreamSubject, LongStream> assertionCallback) {
        return ExpectFailure.expectFailureAbout(longStreams(), assertionCallback);
    }
}
