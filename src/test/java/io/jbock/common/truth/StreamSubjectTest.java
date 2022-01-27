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
import java.util.stream.Stream;

import static io.jbock.common.truth.FailureAssertions.assertFailureKeys;
import static io.jbock.common.truth.FailureAssertions.assertFailureValue;
import static io.jbock.common.truth.StreamSubject.streams;
import static io.jbock.common.truth.Truth8.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for Java 8 {@link Stream} Subjects.
 *
 * @author Kurt Alfred Kluever
 */
final class StreamSubjectTest {

    @Test
    void testIsEqualTo() {
        Stream<String> stream = Stream.of("hello");
        assertThat(stream).isEqualTo(stream);
    }

    @Test
    void testIsEqualToList() {
        Stream<String> stream = Stream.of("hello");
        List<String> list = asList("hello");
        AssertionError unused = expectFailure(whenTesting -> whenTesting.that(stream).isEqualTo(list));
    }

    @Test
    void testNullStream_fails() {
        Stream<String> nullStream = null;
        try {
            assertThat(nullStream).isEmpty();
            fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    void testNullStreamIsNull() {
        Stream<String> nullStream = null;
        assertThat(nullStream).isNull();
    }

    @Test
    void testIsSameInstanceAs() {
        Stream<String> stream = Stream.of("hello");
        assertThat(stream).isSameInstanceAs(stream);
    }

    @Test
    void testIsEmpty() {
        assertThat(Stream.of()).isEmpty();
    }

    @Test
    void testIsEmpty_fails() {
        AssertionError unused =
                expectFailure(whenTesting -> whenTesting.that(Stream.of("hello")).isEmpty());
    }

    @Test
    void testIsNotEmpty() {
        assertThat(Stream.of("hello")).isNotEmpty();
    }

    @Test
    void testIsNotEmpty_fails() {
        AssertionError unused =
                expectFailure(whenTesting -> whenTesting.that(Stream.of()).isNotEmpty());
    }

    @Test
    void testHasSize() {
        assertThat(Stream.of("hello")).hasSize(1);
    }

    @Test
    void testHasSize_fails() {
        AssertionError unused =
                expectFailure(whenTesting -> whenTesting.that(Stream.of("hello")).hasSize(2));
    }

    @Test
    void testContainsNoDuplicates() {
        assertThat(Stream.of("hello")).containsNoDuplicates();
    }

    @Test
    void testContainsNoDuplicates_fails() {
        AssertionError unused =
                expectFailure(
                        whenTesting -> whenTesting.that(Stream.of("hello", "hello")).containsNoDuplicates());
    }

    @Test
    void testContains() {
        assertThat(Stream.of("hello")).contains("hello");
    }

    @Test
    void testContains_fails() {
        AssertionError unused =
                expectFailure(whenTesting -> whenTesting.that(Stream.of("hello")).contains("goodbye"));
    }

    @Test
    void testContainsAnyOf() {
        assertThat(Stream.of("hello")).containsAnyOf("hello", "hell");
    }

    @Test
    void testContainsAnyOf_fails() {
        AssertionError unused =
                expectFailure(
                        whenTesting -> whenTesting.that(Stream.of("hello")).containsAnyOf("goodbye", "good"));
    }

    @Test
    void testContainsAnyIn() {
        assertThat(Stream.of("hello")).containsAnyIn(asList("hello", "hell"));
    }

    @Test
    void testContainsAnyIn_fails() {
        AssertionError unused =
                expectFailure(
                        whenTesting ->
                                whenTesting.that(Stream.of("hello")).containsAnyIn(asList("goodbye", "good")));
    }

    @Test
    void testDoesNotContain() {
        assertThat(Stream.of("hello")).doesNotContain("goodbye");
    }

    @Test
    void testDoesNotContain_fails() {
        AssertionError unused =
                expectFailure(whenTesting -> whenTesting.that(Stream.of("hello")).doesNotContain("hello"));
    }

    @Test
    void testContainsNoneOf() {
        assertThat(Stream.of("hello")).containsNoneOf("goodbye", "good");
    }

    @Test
    void testContainsNoneOf_fails() {
        AssertionError unused =
                expectFailure(
                        whenTesting -> whenTesting.that(Stream.of("hello")).containsNoneOf("hello", "hell"));
    }

    @Test
    void testContainsNoneIn() {
        assertThat(Stream.of("hello")).containsNoneIn(asList("goodbye", "good"));
    }

    @Test
    void testContainsNoneIn_fails() {
        AssertionError unused =
                expectFailure(
                        whenTesting ->
                                whenTesting.that(Stream.of("hello")).containsNoneIn(asList("hello", "hell")));
    }

    @Test
    void testContainsAtLeast() {
        assertThat(Stream.of("hell", "hello")).containsAtLeast("hell", "hello");
    }

    @Test
    void testContainsAtLeast_fails() {
        AssertionError unused =
                expectFailure(
                        whenTesting ->
                                whenTesting
                                        .that(Stream.of("hell", "hello"))
                                        .containsAtLeast("hell", "hello", "goodbye"));
    }

    @Test
    void testContainsAtLeast_inOrder() {
        assertThat(Stream.of("hell", "hello")).containsAtLeast("hell", "hello").inOrder();
    }

    @Test
    void testContainsAtLeast_inOrder_fails() {
        try {
            assertThat(Stream.of("hell", "hello")).containsAtLeast("hello", "hell").inOrder();
            fail();
        } catch (AssertionError expected) {
            assertFailureKeys(
                    expected,
                    "required elements were all found, but order was wrong",
                    "expected order for required elements",
                    "but was");
            assertFailureValue(expected, "expected order for required elements", "[hello, hell]");
        }
    }

    @Test
    void testContainsAtLeastElementsIn() {
        assertThat(Stream.of("hell", "hello")).containsAtLeastElementsIn(asList("hell", "hello"));
    }

    @Test
    void testContainsAtLeastElementsIn_fails() {
        AssertionError unused =
                expectFailure(
                        whenTesting ->
                                whenTesting
                                        .that(Stream.of("hell", "hello"))
                                        .containsAtLeastElementsIn(asList("hell", "hello", "goodbye")));
    }

    @Test
    void testContainsAtLeastElementsIn_inOrder() {
        assertThat(Stream.of("hell", "hello"))
                .containsAtLeastElementsIn(asList("hell", "hello"))
                .inOrder();
    }

    @Test
    void testContainsAtLeastElementsIn_inOrder_fails() {
        try {
            assertThat(Stream.of("hell", "hello"))
                    .containsAtLeastElementsIn(asList("hello", "hell"))
                    .inOrder();
            fail();
        } catch (AssertionError expected) {
            assertFailureKeys(
                    expected,
                    "required elements were all found, but order was wrong",
                    "expected order for required elements",
                    "but was");
            assertFailureValue(expected, "expected order for required elements", "[hello, hell]");
        }
    }

    @Test
    void testContainsExactly() {
        assertThat(Stream.of("hell", "hello")).containsExactly("hell", "hello");
        assertThat(Stream.of("hell", "hello")).containsExactly("hello", "hell");
    }

    @Test
    void testContainsExactly_fails() {
        try {
            assertThat(Stream.of("hell", "hello")).containsExactly("hell");
            fail();
        } catch (AssertionError expected) {
            assertFailureKeys(expected, "unexpected (1)", "---", "expected", "but was");
            assertFailureValue(expected, "expected", "[hell]");
        }
    }

    @Test
    void testContainsExactly_inOrder() {
        assertThat(Stream.of("hell", "hello")).containsExactly("hell", "hello").inOrder();
    }

    @Test
    void testContainsExactly_inOrder_fails() {
        try {
            assertThat(Stream.of("hell", "hello")).containsExactly("hello", "hell").inOrder();
            fail();
        } catch (AssertionError expected) {
            assertFailureKeys(expected, "contents match, but order was wrong", "expected", "but was");
            assertFailureValue(expected, "expected", "[hello, hell]");
        }
    }

    @Test
    void testContainsExactlyElementsIn() {
        assertThat(Stream.of("hell", "hello")).containsExactlyElementsIn(asList("hell", "hello"));
        assertThat(Stream.of("hell", "hello")).containsExactlyElementsIn(asList("hello", "hell"));
    }

    @Test
    void testContainsExactlyElementsIn_fails() {
        try {
            assertThat(Stream.of("hell", "hello")).containsExactlyElementsIn(asList("hell"));
            fail();
        } catch (AssertionError expected) {
            assertFailureKeys(expected, "unexpected (1)", "---", "expected", "but was");
            assertFailureValue(expected, "expected", "[hell]");
        }
    }

    @Test
    void testContainsExactlyElementsIn_inOrder() {
        assertThat(Stream.of("hell", "hello"))
                .containsExactlyElementsIn(asList("hell", "hello"))
                .inOrder();
    }

    @Test
    void testContainsExactlyElementsIn_inOrder_fails() {
        try {
            assertThat(Stream.of("hell", "hello"))
                    .containsExactlyElementsIn(asList("hello", "hell"))
                    .inOrder();
            fail();
        } catch (AssertionError expected) {
            assertFailureKeys(expected, "contents match, but order was wrong", "expected", "but was");
            assertFailureValue(expected, "expected", "[hello, hell]");
        }
    }

    @Test
    void testIsInOrder() {
        assertThat(Stream.of()).isInOrder();
        assertThat(Stream.of(1)).isInOrder();
        assertThat(Stream.of(1, 1, 2, 3, 3, 3, 4)).isInOrder();
    }

    @Test
    void testIsInOrder_fails() {
        AssertionError unused =
                expectFailure(whenTesting -> whenTesting.that(Stream.of(1, 3, 2, 4)).isInOrder());
    }

    @Test
    void testIsInStrictOrder() {
        assertThat(Stream.of()).isInStrictOrder();
        assertThat(Stream.of(1)).isInStrictOrder();
        assertThat(Stream.of(1, 2, 3, 4)).isInStrictOrder();
    }

    @Test
    void testIsInStrictOrder_fails() {
        AssertionError unused =
                expectFailure(whenTesting -> whenTesting.that(Stream.of(1, 2, 2, 4)).isInStrictOrder());
    }

    private static AssertionError expectFailure(
            ExpectFailure.SimpleSubjectBuilderCallback<StreamSubject, Stream<?>> assertionCallback) {
        return ExpectFailure.expectFailureAbout(streams(), assertionCallback);
    }
}
