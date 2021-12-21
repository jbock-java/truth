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

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.google.common.truth.TestCorrespondences.CASE_INSENSITIVE_EQUALITY;
import static com.google.common.truth.TestCorrespondences.INT_DIFF_FORMATTER;
import static com.google.common.truth.TestCorrespondences.STRING_PARSES_TO_INTEGER_CORRESPONDENCE;
import static com.google.common.truth.TestCorrespondences.WITHIN_10_OF;
import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for {@link Map} subjects.
 *
 * @author Christian Gruber
 * @author Kurt Alfred Kluever
 */
class MapSubjectTest extends BaseSubjectTestCase {

    @Test
    void containsExactlyWithNullKey() {
        Map<String, String> actual = new HashMap<>();
        actual.put(null, "value");

        assertThat(actual).containsExactly(null, "value");
        assertThat(actual).containsExactly(null, "value").inOrder();
        assertThat(actual).containsExactlyEntriesIn(actual);
        assertThat(actual).containsExactlyEntriesIn(actual).inOrder();
    }

    @Test
    void containsExactlyWithNullValue() {
        Map<String, String> actual = new HashMap<>();
        actual.put("key", null);

        assertThat(actual).containsExactly("key", null);
        assertThat(actual).containsExactly("key", null).inOrder();
        assertThat(actual).containsExactlyEntriesIn(actual);
        assertThat(actual).containsExactlyEntriesIn(actual).inOrder();
    }

    @Test
    void containsExactlyEmpty() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of();

        assertThat(actual).containsExactly();
        assertThat(actual).containsExactly().inOrder();
        assertThat(actual).containsExactlyEntriesIn(actual);
        assertThat(actual).containsExactlyEntriesIn(actual).inOrder();
    }

    @Test
    void containsExactlyEmpty_fails() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactly());
        assertFailureKeys(
                failure,
                "expected to be empty", "but was");
    }

    @Test
    void containsExactlyEntriesInEmpty_fails() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactlyEntriesIn(ImmutableMap.of()));
        assertFailureKeys(
                failure,
                "expected to be empty", "but was");
    }

    @Test
    void containsExactlyOneEntry() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1);

        assertThat(actual).containsExactly("jan", 1);
        assertThat(actual).containsExactly("jan", 1).inOrder();
        assertThat(actual).containsExactlyEntriesIn(actual);
        assertThat(actual).containsExactlyEntriesIn(actual).inOrder();
    }

    @Test
    void containsExactlyMultipleEntries() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);

        assertThat(actual).containsExactly("march", 3, "jan", 1, "feb", 2);
        assertThat(actual).containsExactly("jan", 1, "feb", 2, "march", 3).inOrder();
        assertThat(actual).containsExactlyEntriesIn(actual);
        assertThat(actual).containsExactlyEntriesIn(actual).inOrder();
    }

    @Test
    void containsExactlyDuplicateKeys() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);

        try {
            assertThat(actual).containsExactly("jan", 1, "jan", 2, "jan", 3);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertThat(expected)
                    .hasMessageThat()
                    .isEqualTo("Duplicate key (jan) cannot be passed to containsExactly().");
        }
    }

    @Test
    void containsExactlyMultipleDuplicateKeys() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);

        try {
            assertThat(actual).containsExactly("jan", 1, "jan", 1, "feb", 2, "feb", 2);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertThat(expected)
                    .hasMessageThat()
                    .isEqualTo("Duplicate key (jan) cannot be passed to containsExactly().");
        }
    }

    @Test
    void containsExactlyExtraKey() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactly("feb", 2, "jan", 1));
        assertFailureKeys(
                failure,
                "unexpected keys", "for key", "unexpected value", "---", "expected", "but was");
        assertFailureValue(
                failure,
                "for key", "march");
        assertFailureValue(
                failure,
                "unexpected value", "3");
        assertFailureValue(
                failure,
                "expected", "{feb=2, jan=1}");
        assertFailureValue(
                failure,
                "but was", "{jan=1, feb=2, march=3}");
    }

    @Test
    void containsExactlyExtraKeyInOrder() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactly("feb", 2, "jan", 1)
                        .inOrder());
        assertFailureKeys(
                failure,
                "unexpected keys", "for key", "unexpected value", "---", "expected", "but was");
        assertFailureValue(
                failure,
                "for key", "march");
        assertFailureValue(
                failure,
                "unexpected value", "3");
    }

    @Test
    void containsExactlyMissingKey() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactly("jan", 1, "march", 3, "feb", 2)
                        .inOrder());
        assertFailureKeys(
                failure,
                "missing keys", "for key", "expected value", "---", "expected", "but was");
        assertFailureValue(
                failure,
                "for key", "march");
        assertFailureValue(
                failure,
                "expected value", "3");
    }

    @Test
    void containsExactlyWrongValue() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactly("jan", 1, "march", 33, "feb", 2));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "---",
                "expected",
                "but was");
        assertFailureValue(
                failure,
                "for key", "march");
        assertFailureValue(
                failure,
                "expected value", "33");
        assertFailureValue(
                failure,
                "but got value", "3");
    }

    @Test
    void containsExactlyWrongValueWithNull() {
        // Test for https://github.com/google/truth/issues/468
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactly("jan", 1, "march", null, "feb", 2));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "---",
                "expected",
                "but was");
        assertFailureValue(
                failure,
                "for key", "march");
        assertFailureValue(
                failure,
                "expected value", "null");
        assertFailureValue(
                failure,
                "but got value", "3");
    }

    @Test
    void containsExactlyExtraKeyAndMissingKey() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "march", 3);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactly("jan", 1, "feb", 2));
        assertFailureKeys(
                failure,
                "missing keys",
                "for key",
                "expected value",
                "unexpected keys",
                "for key",
                "unexpected value",
                "---",
                "expected",
                "but was");
        assertFailureValueIndexed(
                failure,
                "for key", 0, "feb");
        assertFailureValue(
                failure,
                "expected value", "2");
        assertFailureValueIndexed(
                failure,
                "for key", 1, "march");
        assertFailureValue(
                failure,
                "unexpected value", "3");
    }

    @Test
    void containsExactlyExtraKeyAndWrongValue() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactly("jan", 1, "march", 33));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "unexpected keys",
                "for key",
                "unexpected value",
                "---",
                "expected",
                "but was");
        assertFailureValueIndexed(
                failure,
                "for key", 0, "march");
        assertFailureValue(
                failure,
                "expected value", "33");
        assertFailureValue(
                failure,
                "but got value", "3");
        assertFailureValueIndexed(
                failure,
                "for key", 1, "feb");
        assertFailureValue(
                failure,
                "unexpected value", "2");
    }

    @Test
    void containsExactlyMissingKeyAndWrongValue() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "march", 3);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactly("jan", 1, "march", 33, "feb", 2));

        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "missing keys",
                "for key",
                "expected value",
                "---",
                "expected",
                "but was");
        assertFailureValueIndexed(
                failure,
                "for key", 0, "march");
        assertFailureValueIndexed(
                failure,
                "expected value", 0, "33");
        assertFailureValue(
                failure,
                "but got value", "3");
        assertFailureValueIndexed(
                failure,
                "for key", 1, "feb");
        assertFailureValueIndexed(
                failure,
                "expected value", 1, "2");
    }

    @Test
    void containsExactlyExtraKeyAndMissingKeyAndWrongValue() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "march", 3);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactly("march", 33, "feb", 2));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "missing keys",
                "for key",
                "expected value",
                "unexpected keys",
                "for key",
                "unexpected value",
                "---",
                "expected",
                "but was");
        assertFailureValueIndexed(
                failure,
                "for key", 0, "march");
        assertFailureValueIndexed(
                failure,
                "expected value", 0, "33");
        assertFailureValue(
                failure,
                "but got value", "3");
        assertFailureValueIndexed(
                failure,
                "for key", 1, "feb");
        assertFailureValueIndexed(
                failure,
                "expected value", 1, "2");
        assertFailureValueIndexed(
                failure,
                "for key", 2, "jan");
        assertFailureValue(
                failure,
                "unexpected value", "1");
    }

    @Test
    void containsExactlyNotInOrder() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
        assertThat(actual).containsExactlyEntriesIn(actual);
        assertThat(actual).containsExactlyEntriesIn(actual).inOrder();

        assertThat(actual).containsExactly("jan", 1, "march", 3, "feb", 2);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactly("jan", 1, "march", 3, "feb", 2)
                        .inOrder());
        assertFailureKeys(
                failure,
                "entries match, but order was wrong", "expected", "but was");
        assertFailureValue(
                failure,
                "expected", "{jan=1, march=3, feb=2}");
        assertFailureValue(
                failure,
                "but was", "{jan=1, feb=2, march=3}");
    }

    @Test
    @SuppressWarnings("ShouldHaveEvenArgs")
    void containsExactlyBadNumberOfArgs() {
        ImmutableMap<String, Integer> actual =
                ImmutableMap.of("jan", 1, "feb", 2, "march", 3, "april", 4, "may", 5);
        assertThat(actual).containsExactlyEntriesIn(actual);
        assertThat(actual).containsExactlyEntriesIn(actual).inOrder();

        try {
            assertThat(actual)
                    .containsExactly("jan", 1, "feb", 2, "march", 3, "april", 4, "may", 5, "june", 6, "july");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertThat(expected)
                    .hasMessageThat()
                    .isEqualTo(
                            "There must be an equal number of key/value pairs "
                                    + "(i.e., the number of key/value parameters (13) must be even).");
        }
    }

    @Test
    void containsExactlyWrongValue_sameToStringForValues() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(ImmutableMap.of("jan", 1L, "feb", 2L))
                        .containsExactly("jan", 1, "feb", 2));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "for key",
                "expected value",
                "but got value",
                "---",
                "expected",
                "but was");
        assertFailureValueIndexed(
                failure,
                "for key", 0, "jan");
        assertFailureValueIndexed(
                failure,
                "expected value", 0, "1 (java.lang.Integer)");
        assertFailureValueIndexed(
                failure,
                "but got value", 0, "1 (java.lang.Long)");
        assertFailureValueIndexed(
                failure,
                "for key", 1, "feb");
        assertFailureValueIndexed(
                failure,
                "expected value", 1, "2 (java.lang.Integer)");
        assertFailureValueIndexed(
                failure,
                "but got value", 1, "2 (java.lang.Long)");
    }

    @Test
    void containsExactlyWrongValue_sameToStringForKeys() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(ImmutableMap.of(1L, "jan", 1, "feb"))
                        .containsExactly(1, "jan", 1L, "feb"));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "for key",
                "expected value",
                "but got value",
                "---",
                "expected",
                "but was");
        assertFailureValueIndexed(
                failure,
                "for key", 0, "1 (java.lang.Integer)");
        assertFailureValueIndexed(
                failure,
                "expected value", 0, "jan");
        assertFailureValueIndexed(
                failure,
                "but got value", 0, "feb");
        assertFailureValueIndexed(
                failure,
                "for key", 1, "1 (java.lang.Long)");
        assertFailureValueIndexed(
                failure,
                "expected value", 1, "feb");
        assertFailureValueIndexed(
                failure,
                "but got value", 1, "jan");
    }

    @Test
    void containsExactlyExtraKeyAndMissingKey_failsWithSameToStringForKeys() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(ImmutableMap.of(1L, "jan", 2, "feb"))
                        .containsExactly(1, "jan", 2, "feb"));
        assertFailureKeys(
                failure,
                "missing keys",
                "for key",
                "expected value",
                "unexpected keys",
                "for key",
                "unexpected value",
                "---",
                "expected",
                "but was");
        assertFailureValueIndexed(
                failure,
                "for key", 0, "1 (java.lang.Integer)");
        assertFailureValue(
                failure,
                "expected value", "jan");
        assertFailureValueIndexed(
                failure,
                "for key", 1, "1 (java.lang.Long)");
        assertFailureValue(
                failure,
                "unexpected value", "jan");
    }

    @Test
    void containsAtLeastWithNullKey() {
        Map<String, String> actual = new HashMap<>();
        actual.put(null, "value");
        actual.put("unexpectedKey", "unexpectedValue");
        Map<String, String> expected = new HashMap<>();
        expected.put(null, "value");

        assertThat(actual).containsAtLeast(null, "value");
        assertThat(actual).containsAtLeast(null, "value").inOrder();
        assertThat(actual).containsAtLeastEntriesIn(expected);
        assertThat(actual).containsAtLeastEntriesIn(expected).inOrder();
    }

    @Test
    void containsAtLeastWithNullValue() {
        Map<String, String> actual = new HashMap<>();
        actual.put("key", null);
        actual.put("unexpectedKey", "unexpectedValue");
        Map<String, String> expected = new HashMap<>();
        expected.put("key", null);

        assertThat(actual).containsAtLeast("key", null);
        assertThat(actual).containsAtLeast("key", null).inOrder();
        assertThat(actual).containsAtLeastEntriesIn(expected);
        assertThat(actual).containsAtLeastEntriesIn(expected).inOrder();
    }

    @Test
    void containsAtLeastEmpty() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("key", 1);

        assertThat(actual).containsAtLeastEntriesIn(ImmutableMap.of());
        assertThat(actual).containsAtLeastEntriesIn(ImmutableMap.of()).inOrder();
    }

    @Test
    void containsAtLeastOneEntry() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1);

        assertThat(actual).containsAtLeast("jan", 1);
        assertThat(actual).containsAtLeast("jan", 1).inOrder();
        assertThat(actual).containsAtLeastEntriesIn(actual);
        assertThat(actual).containsAtLeastEntriesIn(actual).inOrder();
    }

    @Test
    void containsAtLeastMultipleEntries() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "mar", 3, "apr", 4);

        assertThat(actual).containsAtLeast("apr", 4, "jan", 1, "feb", 2);
        assertThat(actual).containsAtLeast("jan", 1, "feb", 2, "apr", 4).inOrder();
        assertThat(actual).containsAtLeastEntriesIn(ImmutableMap.of("apr", 4, "jan", 1, "feb", 2));
        assertThat(actual).containsAtLeastEntriesIn(actual).inOrder();
    }

    @Test
    void containsAtLeastDuplicateKeys() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);

        try {
            assertThat(actual).containsAtLeast("jan", 1, "jan", 2, "jan", 3);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertThat(expected)
                    .hasMessageThat()
                    .isEqualTo("Duplicate key (jan) cannot be passed to containsAtLeast().");
        }
    }

    @Test
    void containsAtLeastMultipleDuplicateKeys() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);

        try {
            assertThat(actual).containsAtLeast("jan", 1, "jan", 1, "feb", 2, "feb", 2);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertThat(expected)
                    .hasMessageThat()
                    .isEqualTo("Duplicate key (jan) cannot be passed to containsAtLeast().");
        }
    }

    @Test
    void containsAtLeastMissingKey() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsAtLeast("jan", 1, "march", 3));
        assertFailureKeys(
                failure,
                "missing keys",
                "for key",
                "expected value",
                "---",
                "expected to contain at least",
                "but was");
        assertFailureValue(
                failure,
                "for key", "march");
        assertFailureValue(
                failure,
                "expected value", "3");
        assertFailureValue(
                failure,
                "expected to contain at least", "{jan=1, march=3}");
    }

    @Test
    void containsAtLeastWrongValue() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsAtLeast("jan", 1, "march", 33));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "---",
                "expected to contain at least",
                "but was");
        assertFailureValue(
                failure,
                "for key", "march");
        assertFailureValue(
                failure,
                "expected value", "33");
        assertFailureValue(
                failure,
                "but got value", "3");
    }

    @Test
    void containsAtLeastWrongValueWithNull() {
        // Test for https://github.com/google/truth/issues/468
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsAtLeast("jan", 1, "march", null));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "---",
                "expected to contain at least",
                "but was");
        assertFailureValue(
                failure,
                "for key", "march");
        assertFailureValue(
                failure,
                "expected value", "null");
        assertFailureValue(
                failure,
                "but got value", "3");
    }

    @Test
    void containsAtLeastExtraKeyAndMissingKeyAndWrongValue() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "march", 3);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsAtLeast("march", 33, "feb", 2));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "missing keys",
                "for key",
                "expected value",
                "---",
                "expected to contain at least",
                "but was");
        assertFailureValueIndexed(
                failure,
                "for key", 0, "march");
        assertFailureValueIndexed(
                failure,
                "expected value", 0, "33");
        assertFailureValue(
                failure,
                "but got value", "3");
        assertFailureValueIndexed(
                failure,
                "for key", 1, "feb");
        assertFailureValueIndexed(
                failure,
                "expected value", 1, "2");
    }

    @Test
    void containsAtLeastNotInOrder() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsAtLeast("march", 3, "feb", 2)
                        .inOrder());
        assertFailureKeys(
                failure,
                "required entries were all found, but order was wrong",
                "expected to contain at least",
                "but was");
        assertFailureValue(
                failure,
                "expected to contain at least", "{march=3, feb=2}");
        assertFailureValue(
                failure,
                "but was", "{jan=1, feb=2, march=3}");
    }

    @Test
    @SuppressWarnings("ShouldHaveEvenArgs")
    void containsAtLeastBadNumberOfArgs() {
        ImmutableMap<String, Integer> actual =
                ImmutableMap.of("jan", 1, "feb", 2, "march", 3, "april", 4, "may", 5);

        try {
            assertThat(actual)
                    .containsAtLeast("jan", 1, "feb", 2, "march", 3, "april", 4, "may", 5, "june", 6, "july");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertThat(expected)
                    .hasMessageThat()
                    .isEqualTo(
                            "There must be an equal number of key/value pairs "
                                    + "(i.e., the number of key/value parameters (13) must be even).");
        }
    }

    @Test
    void containsAtLeastWrongValue_sameToStringForValues() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(ImmutableMap.of("jan", 1L, "feb", 2L, "mar", 3L))
                        .containsAtLeast("jan", 1, "feb", 2));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "for key",
                "expected value",
                "but got value",
                "---",
                "expected to contain at least",
                "but was");
        assertFailureValueIndexed(
                failure,
                "for key", 0, "jan");
        assertFailureValueIndexed(
                failure,
                "expected value", 0, "1 (java.lang.Integer)");
        assertFailureValueIndexed(
                failure,
                "but got value", 0, "1 (java.lang.Long)");
        assertFailureValueIndexed(
                failure,
                "for key", 1, "feb");
        assertFailureValueIndexed(
                failure,
                "expected value", 1, "2 (java.lang.Integer)");
        assertFailureValueIndexed(
                failure,
                "but got value", 1, "2 (java.lang.Long)");
    }

    @Test
    void containsAtLeastWrongValue_sameToStringForKeys() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(ImmutableMap.of(1L, "jan", 1, "feb"))
                        .containsAtLeast(1, "jan", 1L, "feb"));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "for key",
                "expected value",
                "but got value",
                "---",
                "expected to contain at least",
                "but was");
        assertFailureValueIndexed(
                failure,
                "for key", 0, "1 (java.lang.Integer)");
        assertFailureValueIndexed(
                failure,
                "expected value", 0, "jan");
        assertFailureValueIndexed(
                failure,
                "but got value", 0, "feb");
        assertFailureValueIndexed(
                failure,
                "for key", 1, "1 (java.lang.Long)");
        assertFailureValueIndexed(
                failure,
                "expected value", 1, "feb");
        assertFailureValueIndexed(
                failure,
                "but got value", 1, "jan");
    }

    @Test
    void containsAtLeastExtraKeyAndMissingKey_failsWithSameToStringForKeys() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(ImmutableMap.of(1L, "jan", 2, "feb"))
                        .containsAtLeast(1, "jan", 2, "feb"));
        assertFailureKeys(
                failure,
                "missing keys",
                "for key",
                "expected value",
                "---",
                "expected to contain at least",
                "but was");
        assertFailureValue(
                failure,
                "for key", "1 (java.lang.Integer)");
        assertFailureValue(
                failure,
                "expected value", "jan");
    }

    @Test
    void isEqualToPass() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
        ImmutableMap<String, Integer> expectedMap = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);

        assertThat(actual).isEqualTo(expectedMap);
    }

    @Test
    void isEqualToFailureExtraMissingAndDiffering() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
        ImmutableMap<String, Integer> expectedMap = ImmutableMap.of("jan", 1, "april", 4, "march", 5);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .isEqualTo(expectedMap));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "missing keys",
                "for key",
                "expected value",
                "unexpected keys",
                "for key",
                "unexpected value",
                "---",
                "expected",
                "but was");
        assertFailureValueIndexed(
                failure,
                "for key", 0, "march");
        assertFailureValueIndexed(
                failure,
                "expected value", 0, "5");
        assertFailureValue(
                failure,
                "but got value", "3");
        assertFailureValueIndexed(
                failure,
                "for key", 1, "april");
        assertFailureValueIndexed(
                failure,
                "expected value", 1, "4");
        assertFailureValueIndexed(
                failure,
                "for key", 2, "feb");
        assertFailureValue(
                failure,
                "unexpected value", "2");
        assertFailureValue(
                failure,
                "expected", "{jan=1, april=4, march=5}");
        assertFailureValue(
                failure,
                "but was", "{jan=1, feb=2, march=3}");
    }

    @Test
    void isEqualToFailureDiffering() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
        ImmutableMap<String, Integer> expectedMap = ImmutableMap.of("jan", 1, "feb", 2, "march", 4);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .isEqualTo(expectedMap));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "---",
                "expected",
                "but was");
        assertFailureValueIndexed(
                failure,
                "for key", 0, "march");
        assertFailureValue(
                failure,
                "expected value", "4");
        assertFailureValue(
                failure,
                "but got value", "3");
    }

    @Test
    void isEqualToFailureExtra() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
        ImmutableMap<String, Integer> expectedMap = ImmutableMap.of("jan", 1, "feb", 2);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .isEqualTo(expectedMap));
        assertFailureKeys(
                failure,
                "unexpected keys", "for key", "unexpected value", "---", "expected", "but was");
        assertFailureValue(
                failure,
                "for key", "march");
        assertFailureValue(
                failure,
                "unexpected value", "3");
    }

    @Test
    void isEqualToFailureMissing() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2);
        ImmutableMap<String, Integer> expectedMap = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .isEqualTo(expectedMap));

        assertFailureKeys(
                failure,
                "missing keys", "for key", "expected value", "---", "expected", "but was");
        assertFailureValue(
                failure,
                "for key", "march");
        assertFailureValue(
                failure,
                "expected value", "3");
    }

    @Test
    void isEqualToFailureExtraAndMissing() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
        ImmutableMap<String, Integer> expectedMap = ImmutableMap.of("jan", 1, "feb", 2, "mar", 3);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .isEqualTo(expectedMap));
        assertFailureKeys(
                failure,
                "missing keys",
                "for key",
                "expected value",
                "unexpected keys",
                "for key",
                "unexpected value",
                "---",
                "expected",
                "but was");
        assertFailureValueIndexed(
                failure,
                "for key", 0, "mar");
        assertFailureValue(
                failure,
                "expected value", "3");
        assertFailureValueIndexed(
                failure,
                "for key", 1, "march");
        assertFailureValue(
                failure,
                "unexpected value", "3");
    }

    @Test
    void isEqualToFailureDiffering_sameToString() {
        ImmutableMap<String, Number> actual =
                ImmutableMap.of("jan", 1, "feb", 2, "march", 3L);
        ImmutableMap<String, Integer> expectedMap = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .isEqualTo(expectedMap));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "---",
                "expected",
                "but was");
        assertFailureValueIndexed(
                failure,
                "for key", 0, "march");
        assertFailureValue(
                failure,
                "expected value", "3 (java.lang.Integer)");
        assertFailureValue(
                failure,
                "but got value", "3 (java.lang.Long)");
    }

    @Test
    void isEqualToNonMap() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .isEqualTo("something else"));
        assertFailureKeys(
                failure,
                "expected", "but was");
    }

    @Test
    void isEqualToNotConsistentWithEquals() {
        TreeMap<String, Integer> actual = new TreeMap<>(CASE_INSENSITIVE_ORDER);
        TreeMap<String, Integer> expected = new TreeMap<>(CASE_INSENSITIVE_ORDER);
        actual.put("one", 1);
        expected.put("ONE", 1);
        /*
         * Our contract doesn't guarantee that the following test will pass. It *currently* does,
         * though, and if we change that behavior, we want this test to let us know.
         */
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void isEqualToNotConsistentWithEquals_failure() {
        TreeMap<String, Integer> actual = new TreeMap<>(CASE_INSENSITIVE_ORDER);
        TreeMap<String, Integer> expected = new TreeMap<>(CASE_INSENSITIVE_ORDER);
        actual.put("one", 1);
        expected.put("ONE", 1);
        actual.put("two", 2);
        assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .isEqualTo(expected));
        // The exact message generated is unspecified.
    }

    @Test
    void isEqualToActualNullOtherMap() {
        Map<?, ?> nullReference = null;
        assertThrows(
                AssertionError.class,
                () -> assertThat(nullReference)
                        .isEqualTo(ImmutableMap.of()));
    }

    @Test
    void isEqualToActualMapOtherNull() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(ImmutableMap.of())
                        .isEqualTo(null));
    }

    @Test
    void isNotEqualTo() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
        ImmutableMap<String, Integer> unexpected = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
        assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .isNotEqualTo(unexpected));
    }

    @Test
    void isEmpty() {
        ImmutableMap<String, String> actual = ImmutableMap.of();
        assertThat(actual).isEmpty();
    }

    @Test
    void isEmptyWithFailure() {
        ImmutableMap<Integer, Integer> actual = ImmutableMap.of(1, 5);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .isEmpty());
        assertFailureKeys(
                failure,
                "expected to be empty", "but was");
    }

    @Test
    void isNotEmpty() {
        ImmutableMap<Integer, Integer> actual = ImmutableMap.of(1, 5);
        assertThat(actual).isNotEmpty();
    }

    @Test
    void isNotEmptyWithFailure() {
        ImmutableMap<Integer, Integer> actual = ImmutableMap.of();
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .isNotEmpty());
        assertFailureKeys(
                failure,
                "expected not to be empty");
    }

    @Test
    void hasSize() {
        assertThat(ImmutableMap.of(1, 2, 3, 4)).hasSize(2);
    }

    @Test
    void hasSizeZero() {
        assertThat(ImmutableMap.of()).hasSize(0);
    }

    @Test
    void hasSizeNegative() {
        try {
            assertThat(ImmutableMap.of(1, 2)).hasSize(-1);
            fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    void containsKey() {
        ImmutableMap<String, String> actual = ImmutableMap.of("kurt", "kluever");
        assertThat(actual).containsKey("kurt");
    }

    @Test
    void containsKeyFailure() {
        ImmutableMap<String, String> actual = ImmutableMap.of("kurt", "kluever");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsKey("greg"));
        assertFailureKeys(
                failure,
                "value of", "expected to contain", "but was", "map was");
        assertFailureValue(
                failure,
                "value of", "map.keySet()");
        assertFailureValue(
                failure,
                "expected to contain", "greg");
        assertFailureValue(
                failure,
                "but was", "[kurt]");
    }

    @Test
    void containsKeyNullFailure() {
        ImmutableMap<String, String> actual = ImmutableMap.of("kurt", "kluever");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsKey(null));
        assertFailureKeys(
                failure,
                "value of", "expected to contain", "but was", "map was");
        assertFailureValue(
                failure,
                "value of", "map.keySet()");
        assertFailureValue(
                failure,
                "expected to contain", "null");
        assertFailureValue(
                failure,
                "but was", "[kurt]");
    }

    @Test
    void containsKey_failsWithSameToString() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(ImmutableMap.of(1L, "value1", 2L, "value2", "1", "value3"))
                        .containsKey(1));
        assertFailureKeys(
                failure,
                "value of",
                "expected to contain",
                "an instance of",
                "but did not",
                "though it did contain",
                "full contents",
                "map was");
        assertFailureValue(
                failure,
                "value of", "map.keySet()");
        assertFailureValue(
                failure,
                "expected to contain", "1");
    }

    @Test
    void containsKey_failsWithNullStringAndNull() {
        Map<String, String> actual = new HashMap<>();
        actual.put("null", "value1");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsKey(null));
        assertFailureKeys(
                failure,
                "value of",
                "expected to contain",
                "an instance of",
                "but did not",
                "though it did contain",
                "full contents",
                "map was");
        assertFailureValue(
                failure,
                "value of", "map.keySet()");
        assertFailureValue(
                failure,
                "expected to contain", "null");
    }

    @Test
    void containsNullKey() {
        Map<String, String> actual = new HashMap<>();
        actual.put(null, "null");
        assertThat(actual).containsKey(null);
    }

    @Test
    void doesNotContainKey() {
        ImmutableMap<String, String> actual = ImmutableMap.of("kurt", "kluever");
        assertThat(actual).doesNotContainKey("greg");
        assertThat(actual).doesNotContainKey(null);
    }

    @Test
    void doesNotContainKeyFailure() {
        ImmutableMap<String, String> actual = ImmutableMap.of("kurt", "kluever");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .doesNotContainKey("kurt"));
        assertFailureKeys(
                failure,
                "value of", "expected not to contain", "but was", "map was");
        assertFailureValue(
                failure,
                "value of", "map.keySet()");
        assertFailureValue(
                failure,
                "expected not to contain", "kurt");
        assertFailureValue(
                failure,
                "but was", "[kurt]");
    }

    @Test
    void doesNotContainNullKey() {
        Map<String, String> actual = new HashMap<>();
        actual.put(null, "null");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .doesNotContainKey(null));
        assertFailureKeys(
                failure,
                "value of", "expected not to contain", "but was", "map was");
        assertFailureValue(
                failure,
                "value of", "map.keySet()");
        assertFailureValue(
                failure,
                "expected not to contain", "null");
        assertFailureValue(
                failure,
                "but was", "[null]");
    }

    @Test
    void containsEntry() {
        ImmutableMap<String, String> actual = ImmutableMap.of("kurt", "kluever");
        assertThat(actual).containsEntry("kurt", "kluever");
    }

    @Test
    void containsEntryFailure() {
        ImmutableMap<String, String> actual = ImmutableMap.of("kurt", "kluever");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsEntry("greg", "kick"));
        assertFailureKeys(
                failure,
                "expected to contain entry", "but was");
        assertFailureValue(
                failure,
                "expected to contain entry", "greg=kick");
        assertFailureValue(
                failure,
                "but was", "{kurt=kluever}");
    }

    @Test
    void containsEntry_failsWithSameToStringOfKey() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(ImmutableMap.of(1L, "value1", 2L, "value2"))
                        .containsEntry(1, "value1"));
        assertFailureKeys(
                failure,
                "expected to contain entry",
                "an instance of",
                "but did not",
                "though it did contain keys",
                "full contents");
        assertFailureValue(
                failure,
                "an instance of", "Map.Entry<java.lang.Integer, java.lang.String>");
        assertFailureValue(
                failure,
                "though it did contain keys", "[1] (java.lang.Long)");
    }

    @Test
    void containsEntry_failsWithSameToStringOfValue() {
        // Does not contain the correct key, but does contain a value which matches by toString.
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(ImmutableMap.of(1, "null"))
                        .containsEntry(2, null));
        assertFailureKeys(
                failure,
                "expected to contain entry",
                "an instance of",
                "but did not",
                "though it did contain values",
                "full contents");
        assertFailureValue(
                failure,
                "an instance of", "Map.Entry<java.lang.Integer, null type>");
        assertFailureValue(
                failure,
                "though it did contain values", "[null] (java.lang.String)");
    }

    @Test
    void containsNullKeyAndValue() {
        ImmutableMap<String, String> actual = ImmutableMap.of("kurt", "kluever");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsEntry(null, null));
        assertFailureKeys(
                failure,
                "expected to contain entry", "but was");
        assertFailureValue(
                failure,
                "expected to contain entry", "null=null");
        assertFailureValue(
                failure,
                "but was", "{kurt=kluever}");
    }

    @Test
    void containsNullEntry() {
        Map<String, String> actual = new HashMap<>();
        actual.put(null, null);
        assertThat(actual).containsEntry(null, null);
    }

    @Test
    void containsNullEntryValue() {
        Map<String, String> actual = new HashMap<>();
        actual.put(null, null);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsEntry("kurt", null));
        assertFailureKeys(
                failure,
                "expected to contain entry",
                "but did not",
                "though it did contain keys with that value",
                "full contents");
        assertFailureValue(
                failure,
                "expected to contain entry", "kurt=null");
        assertFailureValue(
                failure,
                "though it did contain keys with that value", "[null]");
    }

    private static final String KEY_IS_PRESENT_WITH_DIFFERENT_VALUE =
            "key is present but with a different value";

    @Test
    void containsNullEntryKey() {
        Map<String, String> actual = new HashMap<>();
        actual.put(null, null);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsEntry(null, "kluever"));
        assertFailureValue(
                failure,
                "value of", "map.get(null)");
        assertFailureValue(
                failure,
                "expected", "kluever");
        assertFailureValue(
                failure,
                "but was", "null");
        assertFailureValue(
                failure,
                "map was", "{null=null}");
        assertThat(failure)
                .hasMessageThat()
                .contains(KEY_IS_PRESENT_WITH_DIFFERENT_VALUE);
    }

    @Test
    void containsExactly_bothExactAndToStringKeyMatches_showsExactKeyMatch() {
        ImmutableMap<Number, String> actual = ImmutableMap.of(1, "actual int", 1L, "actual long");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsEntry(1L, "expected long"));
        // should show the exact key match, 1="actual int", not the toString key match, 1L="actual long"
        assertFailureKeys(
                failure,
                "value of", "expected", "but was", "map was");
        assertFailureValue(
                failure,
                "value of", "map.get(1)");
        assertFailureValue(
                failure,
                "expected", "expected long");
        assertFailureValue(
                failure,
                "but was", "actual long");
    }

    @Test
    void doesNotContainEntry() {
        ImmutableMap<String, String> actual = ImmutableMap.of("kurt", "kluever");
        assertThat(actual).doesNotContainEntry("greg", "kick");
        assertThat(actual).doesNotContainEntry(null, null);
        assertThat(actual).doesNotContainEntry("kurt", null);
        assertThat(actual).doesNotContainEntry(null, "kluever");
    }

    @Test
    void doesNotContainEntryFailure() {
        ImmutableMap<String, String> actual = ImmutableMap.of("kurt", "kluever");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .doesNotContainEntry("kurt", "kluever"));
        assertFailureKeys(
                failure,
                "value of", "expected not to contain", "but was");
        assertFailureValue(
                failure,
                "value of", "map.entrySet()");
        assertFailureValue(
                failure,
                "expected not to contain", "kurt=kluever");
        assertFailureValue(
                failure,
                "but was", "[kurt=kluever]");
    }

    @Test
    void doesNotContainNullEntry() {
        Map<String, String> actual = new HashMap<>();
        actual.put(null, null);
        assertThat(actual).doesNotContainEntry("kurt", null);
        assertThat(actual).doesNotContainEntry(null, "kluever");
    }

    @Test
    void doesNotContainNullEntryFailure() {
        Map<String, String> actual = new HashMap<>();
        actual.put(null, null);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .doesNotContainEntry(null, null));
        assertFailureKeys(
                failure,
                "value of", "expected not to contain", "but was");
        assertFailureValue(
                failure,
                "value of", "map.entrySet()");
        assertFailureValue(
                failure,
                "expected not to contain", "null=null");
        assertFailureValue(
                failure,
                "but was", "[null=null]");
    }

    @Test
    void failMapContainsKey() {
        ImmutableMap<String, String> actual = ImmutableMap.of("a", "A");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsKey("b"));
        assertFailureKeys(
                failure,
                "value of", "expected to contain", "but was", "map was");
        assertFailureValue(
                failure,
                "value of", "map.keySet()");
        assertFailureValue(
                failure,
                "expected to contain", "b");
        assertFailureValue(
                failure,
                "but was", "[a]");
    }

    @Test
    void failMapContainsKeyWithNull() {
        ImmutableMap<String, String> actual = ImmutableMap.of("a", "A");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsKey(null));
        assertFailureKeys(
                failure,
                "value of", "expected to contain", "but was", "map was");
        assertFailureValue(
                failure,
                "value of", "map.keySet()");
        assertFailureValue(
                failure,
                "expected to contain", "null");
        assertFailureValue(
                failure,
                "but was", "[a]");
    }

    @Test
    void failMapLacksKey() {
        ImmutableMap<String, String> actual = ImmutableMap.of("a", "A");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .doesNotContainKey("a"));
        assertFailureKeys(
                failure,
                "value of", "expected not to contain", "but was", "map was");
        assertFailureValue(
                failure,
                "value of", "map.keySet()");
        assertFailureValue(
                failure,
                "expected not to contain", "a");
        assertFailureValue(
                failure,
                "but was", "[a]");
    }

    @Test
    void containsKeyWithValue() {
        ImmutableMap<String, String> actual = ImmutableMap.of("a", "A");
        assertThat(actual).containsEntry("a", "A");
    }

    @Test
    void containsKeyWithNullValueNullExpected() {
        Map<String, String> actual = new HashMap<>();
        actual.put("a", null);
        assertThat(actual).containsEntry("a", null);
    }

    @Test
    void failMapContainsKeyWithValue() {
        ImmutableMap<String, String> actual = ImmutableMap.of("a", "A");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual).containsEntry("a", "a"));
        assertFailureValue(failure, "value of", "map.get(a)");
        assertFailureValue(failure, "expected", "a");
        assertFailureValue(failure, "but was", "A");
        assertFailureValue(failure, "map was", "{a=A}");
        assertThat(failure)
                .hasMessageThat()
                .doesNotContain(KEY_IS_PRESENT_WITH_DIFFERENT_VALUE);
    }

    @Test
    void failMapContainsKeyWithNullValuePresentExpected() {
        Map<String, String> actual = new HashMap<>();
        actual.put("a", null);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual).containsEntry("a", "A"));
        assertFailureValue(failure, "value of", "map.get(a)");
        assertFailureValue(failure, "expected", "A");
        assertFailureValue(failure, "but was", "null");
        assertFailureValue(failure, "map was", "{a=null}");
        assertThat(failure)
                .hasMessageThat()
                .contains(KEY_IS_PRESENT_WITH_DIFFERENT_VALUE);
    }

    @Test
    void failMapContainsKeyWithPresentValueNullExpected() {
        ImmutableMap<String, String> actual = ImmutableMap.of("a", "A");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual).containsEntry("a", null));
        assertFailureValue(failure, "value of", "map.get(a)");
        assertFailureValue(failure, "expected", "null");
        assertFailureValue(failure, "but was", "A");
        assertFailureValue(failure, "map was", "{a=A}");
        assertThat(failure)
                .hasMessageThat()
                .contains(KEY_IS_PRESENT_WITH_DIFFERENT_VALUE);
    }

    @Test
    void comparingValuesUsing_containsEntry_success() {
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsEntry("def", 456);
    }

    @Test
    void comparingValuesUsing_containsEntry_failsExpectedKeyHasWrongValue() {
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "+123", "def", "+456");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsEntry("def", 123));
        assertFailureKeys(failure, "for key", "expected value", "testing whether", "but got value", "full map");
        assertFailureValue(failure, "for key", "def");
        assertFailureValue(failure, "expected value", "123");
        assertFailureValue(failure, "testing whether", "actual value parses to expected value");
        assertFailureValue(failure, "but got value", "+456");
        assertFailureValue(failure, "full map", "{abc=+123, def=+456}");
    }

    @Test
    void comparingValuesUsing_containsEntry_failsWrongKeyHasExpectedValue() {
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "+123", "def", "+456");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsEntry("xyz", 456));
        assertFailureKeys(
                failure,
                "for key",
                "expected value",
                "testing whether",
                "but was missing",
                "other keys with matching values",
                "full map");
        assertFailureValue(
                failure,
                "other keys with matching values", "[def]");
    }

    @Test
    void comparingValuesUsing_containsEntry_failsMissingExpectedKeyAndValue() {
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "+123", "def", "+456");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsEntry("xyz", 321));
        assertFailureKeys(
                failure,
                "for key", "expected value", "testing whether", "but was missing", "full map");
    }

    @Test
    void comparingValuesUsing_containsEntry_diffExpectedKeyHasWrongValue() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("abc", 35, "def", 71);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(WITHIN_10_OF)
                        .containsEntry("def", 60));
        assertFailureKeys(
                failure,
                "for key", "expected value", "testing whether", "but got value", "diff", "full map");
        assertFailureValue(failure, "for key", "def");
        assertFailureValue(failure, "expected value", "60");
        assertFailureValue(failure, "but got value", "71");
        assertFailureValue(failure, "diff", "11");
    }

    @Test
    void comparingValuesUsing_containsEntry_handlesFormatDiffExceptions() {
        Map<String, Integer> actual = new LinkedHashMap<>();
        actual.put("abc", 35);
        actual.put("def", null);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(WITHIN_10_OF)
                        .containsEntry("def", 60));
        assertFailureKeys(
                failure,
                "for key",
                "expected value",
                "testing whether",
                "but got value",
                "full map",
                "additionally, one or more exceptions were thrown while comparing values",
                "first exception",
                "additionally, one or more exceptions were thrown while formatting diffs",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception", 0)
                .startsWith("compare(null, 60) threw java.lang.NullPointerException");
        assertThatFailure(failure)
                .factValue("first exception", 1)
                .startsWith("formatDiff(null, 60) threw java.lang.NullPointerException");
    }

    @Test
    void comparingValuesUsing_containsEntry_handlesExceptions_expectedKeyHasWrongValue() {
        Map<Integer, String> actual = new LinkedHashMap<>();
        actual.put(1, "one");
        actual.put(2, null);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(CASE_INSENSITIVE_EQUALITY)
                        .containsEntry(2, "TWO"));
        // The test fails because the expected key has a null value which causes compare() to throw.
        // We should report that the key has the wrong value, and also that we saw an exception.
        assertFailureKeys(
                failure,
                "for key",
                "expected value",
                "testing whether",
                "but got value",
                "full map",
                "additionally, one or more exceptions were thrown while comparing values",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(null, TWO) threw java.lang.NullPointerException");
    }

    @Test
    void comparingValuesUsing_containsEntry_handlesExceptions_wrongKeyHasExpectedValue() {
        Map<Integer, String> actual = new LinkedHashMap<>();
        actual.put(1, null);
        actual.put(2, "three");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(CASE_INSENSITIVE_EQUALITY)
                        .containsEntry(3, "THREE"));
        // The test fails and does not contain the expected key, but does contain the expected value for
        // a different key. No reasonable implementation would find this value in the second entry
        // without hitting the exception from trying the first entry (which has a null value), so we
        // should report the exception as well.
        assertFailureKeys(
                failure,
                "for key",
                "expected value",
                "testing whether",
                "but was missing",
                "other keys with matching values",
                "full map",
                "additionally, one or more exceptions were thrown while comparing values",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(null, THREE) threw java.lang.NullPointerException");
    }

    @Test
    void comparingValuesUsing_doesNotContainEntry_successExcludedKeyHasWrongValues() {
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "+123", "def", "+456");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .doesNotContainEntry("def", 123);
    }

    @Test
    void comparingValuesUsing_doesNotContainEntry_successWrongKeyHasExcludedValue() {
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "+123", "def", "+456");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .doesNotContainEntry("xyz", 456);
    }

    @Test
    void comparingValuesUsing_doesNotContainEntry_successMissingExcludedKeyAndValue() {
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .doesNotContainEntry("xyz", 321);
    }

    @Test
    void comparingValuesUsing_doesNotContainEntry_failure() {
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "+123", "def", "+456");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .doesNotContainEntry("def", 456));
        assertFailureKeys(
                failure,
                "expected not to contain", "testing whether", "but contained", "full map");
        assertFailureValue(
                failure,
                "expected not to contain", "def=456");
        assertFailureValue(
                failure,
                "but contained", "def=+456");
        assertFailureValue(
                failure,
                "full map", "{abc=+123, def=+456}");
    }

    @Test
    void comparingValuesUsing_doesNotContainEntry_handlesException() {
        Map<Integer, String> actual = new LinkedHashMap<>();
        actual.put(1, "one");
        actual.put(2, null);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(CASE_INSENSITIVE_EQUALITY)
                        .doesNotContainEntry(2, "TWO"));
        // This test would pass if compare(null, "TWO") returned false. But it actually throws, so the
        // test must fail.
        assertFailureKeys(
                failure,
                "one or more exceptions were thrown while comparing values",
                "first exception",
                "expected not to contain",
                "testing whether",
                "found no match (but failing because of exception)",
                "full map");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(null, TWO) threw java.lang.NullPointerException");
    }

    @Test
    void comparingValuesUsing_containsExactly_success() {
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactly("def", 456, "abc", 123);
    }

    @Test
    void comparingValuesUsing_containsExactly_inOrder_success() {
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactly("abc", 123, "def", 456)
                .inOrder();
    }

    @Test
    void comparingValuesUsing_containsExactly_failsExtraEntry() {
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactly("def", 456));
        assertFailureKeys(
                failure,
                "unexpected keys",
                "for key",
                "unexpected value",
                "---",
                "expected",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "for key", "abc");
        assertFailureValue(
                failure,
                "unexpected value", "123");
        assertFailureValue(
                failure,
                "expected", "{def=456}");
        assertFailureValue(
                failure,
                "testing whether", "actual value parses to expected value");
        assertFailureValue(
                failure,
                "but was", "{abc=123, def=456}");
    }

    @Test
    void comparingValuesUsing_containsExactly_failsMissingEntry() {
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactly("def", 456, "xyz", 999, "abc", 123));
        assertFailureKeys(
                failure,
                "missing keys",
                "for key",
                "expected value",
                "---",
                "expected",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "for key", "xyz");
        assertFailureValue(
                failure,
                "expected value", "999");
    }

    @Test
    void comparingValuesUsing_containsExactly_failsWrongKey() {
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactly("def", 456, "cab", 123));
        assertFailureKeys(
                failure,
                "missing keys",
                "for key",
                "expected value",
                "unexpected keys",
                "for key",
                "unexpected value",
                "---",
                "expected",
                "testing whether",
                "but was");
        assertFailureValueIndexed(
                failure,
                "for key", 0, "cab");
        assertFailureValue(
                failure,
                "expected value", "123");
        assertFailureValueIndexed(
                failure,
                "for key", 1, "abc");
        assertFailureValue(
                failure,
                "unexpected value", "123");
    }

    @Test
    void comparingValuesUsing_containsExactly_failsWrongValue() {
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactly("def", 456, "abc", 321));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "---",
                "expected",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "for key", "abc");
        assertFailureValue(
                failure,
                "expected value", "321");
        assertFailureValue(
                failure,
                "but got value", "123");
    }

    @Test
    void comparingValuesUsing_containsExactly_handlesExceptions() {
        Map<Integer, String> actual = new LinkedHashMap<>();
        actual.put(1, "one");
        actual.put(2, null);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(CASE_INSENSITIVE_EQUALITY)
                        .containsExactly(1, "ONE", 2, "TWO"));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "---",
                "expected",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing values",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(null, TWO) threw java.lang.NullPointerException");
    }

    @Test
    void comparingValuesUsing_containsExactly_inOrder_failsOutOfOrder() {
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactly("def", 456, "abc", 123)
                        .inOrder());
        assertFailureKeys(
                failure,
                "entries match, but order was wrong", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "expected", "{def=456, abc=123}");
        assertFailureValue(
                failure,
                "but was", "{abc=123, def=456}");
    }

    @Test
    void comparingValuesUsing_containsExactly_wrongValueTypeInActual() {
        ImmutableMap<String, Object> actual = ImmutableMap.of("abc", "123", "def", 456);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactly("def", 456, "abc", 123));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "---",
                "expected",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing values",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(456, 456) threw java.lang.ClassCastException");
    }

    @Test
    void comparingValuesUsing_containsExactly_wrongValueTypeInExpected() {
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactly("def", 456, "abc", 123L));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "---",
                "expected",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing values",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(123, 123) threw java.lang.ClassCastException");
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_success() {
        ImmutableMap<String, Integer> expected = ImmutableMap.of("def", 456, "abc", 123);
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactlyEntriesIn(expected);
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_inOrder_success() {
        ImmutableMap<String, Integer> expected = ImmutableMap.of("abc", 123, "def", 456);
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactlyEntriesIn(expected)
                .inOrder();
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_failsExtraEntry() {
        ImmutableMap<String, Integer> expected = ImmutableMap.of("def", 456);
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyEntriesIn(expected));
        assertFailureKeys(
                failure,
                "unexpected keys",
                "for key",
                "unexpected value",
                "---",
                "expected",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "for key", "abc");
        assertFailureValue(
                failure,
                "unexpected value", "123");
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_failsMissingEntry() {
        ImmutableMap<String, Integer> expected = ImmutableMap.of("def", 456, "xyz", 999, "abc", 123);
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyEntriesIn(expected));
        assertFailureKeys(
                failure,
                "missing keys",
                "for key",
                "expected value",
                "---",
                "expected",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "for key", "xyz");
        assertFailureValue(
                failure,
                "expected value", "999");
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_failsWrongKey() {
        ImmutableMap<String, Integer> expected = ImmutableMap.of("def", 456, "cab", 123);
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyEntriesIn(expected));
        assertFailureKeys(
                failure,
                "missing keys",
                "for key",
                "expected value",
                "unexpected keys",
                "for key",
                "unexpected value",
                "---",
                "expected",
                "testing whether",
                "but was");
        assertFailureValueIndexed(
                failure,
                "for key", 0, "cab");
        assertFailureValue(
                failure,
                "expected value", "123");
        assertFailureValueIndexed(
                failure,
                "for key", 1, "abc");
        assertFailureValue(
                failure,
                "unexpected value", "123");
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_failsWrongValue() {
        ImmutableMap<String, Integer> expected = ImmutableMap.of("def", 456, "abc", 321);
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyEntriesIn(expected));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "---",
                "expected",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "for key", "abc");
        assertFailureValue(
                failure,
                "expected value", "321");
        assertFailureValue(
                failure,
                "but got value", "123");
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_diffMissingAndExtraAndWrongValue() {
        ImmutableMap<String, Integer> expected = ImmutableMap.of("abc", 30, "def", 60, "ghi", 90);
        ImmutableMap<String, Integer> actual = ImmutableMap.of("abc", 35, "fed", 60, "ghi", 101);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(WITHIN_10_OF)
                        .containsExactlyEntriesIn(expected));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "diff",
                "missing keys",
                "for key",
                "expected value",
                "unexpected keys",
                "for key",
                "unexpected value",
                "---",
                "expected",
                "testing whether",
                "but was");
        assertFailureValueIndexed(
                failure,
                "for key", 0, "ghi");
        assertFailureValueIndexed(
                failure,
                "expected value", 0, "90");
        assertFailureValue(
                failure,
                "but got value", "101");
        assertFailureValue(
                failure,
                "diff", "11");
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_handlesFormatDiffExceptions() {
        ImmutableMap<String, Integer> expected = ImmutableMap.of("abc", 30, "def", 60, "ghi", 90);
        Map<String, Integer> actual = new LinkedHashMap<>();
        actual.put("abc", 35);
        actual.put("def", null);
        actual.put("ghi", 95);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(WITHIN_10_OF)
                        .containsExactlyEntriesIn(expected));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "---",
                "expected",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing values",
                "first exception",
                "additionally, one or more exceptions were thrown while formatting diffs",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception", 0)
                .startsWith("compare(null, 60) threw java.lang.NullPointerException");
        assertThatFailure(failure)
                .factValue("first exception", 1)
                .startsWith("formatDiff(null, 60) threw java.lang.NullPointerException");
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_inOrder_failsOutOfOrder() {
        ImmutableMap<String, Integer> expected = ImmutableMap.of("def", 456, "abc", 123);
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyEntriesIn(expected)
                        .inOrder());
        assertFailureKeys(
                failure,
                "entries match, but order was wrong", "expected", "testing whether", "but was");
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_empty() {
        ImmutableMap<String, Integer> expected = ImmutableMap.of();
        ImmutableMap<String, String> actual = ImmutableMap.of();
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactlyEntriesIn(expected);
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_failsEmpty() {
        ImmutableMap<String, Integer> expected = ImmutableMap.of();
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyEntriesIn(expected));
        assertFailureKeys(
                failure,
                "expected to be empty", "but was");
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_wrongValueTypeInActual() {
        ImmutableMap<String, Integer> expected = ImmutableMap.of("def", 456, "abc", 123);
        ImmutableMap<String, Object> actual = ImmutableMap.of("abc", "123", "def", 456);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyEntriesIn(expected));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "---",
                "expected",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing values",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(456, 456) threw java.lang.ClassCastException");
    }

    @Test
    void comparingValuesUsing_containsAtLeast_success() {
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456", "ghi", "789");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeast("def", 456, "abc", 123);
    }

    @Test
    void comparingValuesUsing_containsAtLeast_inOrder_success() {
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "ghi", "789", "def", "456");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeast("abc", 123, "def", 456)
                .inOrder();
    }

    @Test
    void comparingValuesUsing_containsAtLeast_failsMissingEntry() {
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456", "ghi", "789");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeast("def", 456, "xyz", 999, "abc", 123));
        assertFailureKeys(
                failure,
                "missing keys",
                "for key",
                "expected value",
                "---",
                "expected to contain at least",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "for key", "xyz");
        assertFailureValue(
                failure,
                "expected value", "999");
        assertFailureValue(
                failure,
                "expected to contain at least", "{def=456, xyz=999, abc=123}");
    }

    @Test
    void comparingValuesUsing_containsAtLeast_failsWrongKey() {
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeast("def", 456, "cab", 123));
        assertFailureKeys(
                failure,
                "missing keys",
                "for key",
                "expected value",
                "---",
                "expected to contain at least",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "for key", "cab");
        assertFailureValue(
                failure,
                "expected value", "123");
    }

    @Test
    void comparingValuesUsing_containsAtLeast_failsWrongValue() {
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeast("abc", 321));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "---",
                "expected to contain at least",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "for key", "abc");
        assertFailureValue(
                failure,
                "expected value", "321");
        assertFailureValue(
                failure,
                "but got value", "123");
    }

    @Test
    void comparingValuesUsing_containsAtLeast_handlesExceptions() {
        Map<Integer, String> actual = new LinkedHashMap<>();
        actual.put(1, "one");
        actual.put(2, null);
        actual.put(3, "three");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(CASE_INSENSITIVE_EQUALITY)
                        .containsAtLeast(1, "ONE", 2, "TWO"));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "---",
                "expected to contain at least",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing values",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(null, TWO) threw java.lang.NullPointerException");
    }

    @Test
    void comparingValuesUsing_containsAtLeast_inOrder_failsOutOfOrder() {
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456", "ghi", "789");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeast("def", 456, "abc", 123)
                        .inOrder());
        assertFailureKeys(
                failure,
                "required entries were all found, but order was wrong",
                "expected to contain at least",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "expected to contain at least", "{def=456, abc=123}");
        assertFailureValue(
                failure,
                "but was", "{abc=123, def=456, ghi=789}");
    }

    @Test
    void comparingValuesUsing_containsAtLeast_wrongValueTypeInExpectedActual() {
        ImmutableMap<String, Object> actual = ImmutableMap.of("abc", "123", "def", 456);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeast("def", 456));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "---",
                "expected to contain at least",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing values",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(456, 456) threw java.lang.ClassCastException");
    }

    @Test
    void comparingValuesUsing_containsAtLeast_wrongValueTypeInUnexpectedActual_success() {
        ImmutableMap<String, Object> actual = ImmutableMap.of("abc", "123", "def", 456);
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeast("abc", 123);
    }

    @Test
    void comparingValuesUsing_containsAtLeast_wrongValueTypeInExpected() {
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456", "ghi", "789");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeast("def", 456, "abc", 123L));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "---",
                "expected to contain at least",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing values",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(123, 123) threw java.lang.ClassCastException");
    }

    @Test
    void comparingValuesUsing_containsAtLeastEntriesIn_success() {
        ImmutableMap<String, Integer> expected = ImmutableMap.of("def", 456, "abc", 123);
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456", "ghi", "789");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeastEntriesIn(expected);
    }

    @Test
    void comparingValuesUsing_containsAtLeastEntriesIn_inOrder_success() {
        ImmutableMap<String, Integer> expected = ImmutableMap.of("abc", 123, "ghi", 789);
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456", "ghi", "789");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeastEntriesIn(expected)
                .inOrder();
    }

    @Test
    void comparingValuesUsing_containsAtLeastEntriesIn_failsMissingEntry() {
        ImmutableMap<String, Integer> expected = ImmutableMap.of("def", 456, "xyz", 999, "abc", 123);
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456", "ghi", "789");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeastEntriesIn(expected));
        assertFailureKeys(
                failure,
                "missing keys",
                "for key",
                "expected value",
                "---",
                "expected to contain at least",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "for key", "xyz");
        assertFailureValue(
                failure,
                "expected value", "999");
    }

    @Test
    void comparingValuesUsing_containsAtLeastEntriesIn_failsWrongKey() {
        ImmutableMap<String, Integer> expected = ImmutableMap.of("def", 456, "cab", 123);
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeastEntriesIn(expected));
        assertFailureKeys(
                failure,
                "missing keys",
                "for key",
                "expected value",
                "---",
                "expected to contain at least",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "for key", "cab");
        assertFailureValue(
                failure,
                "expected value", "123");
    }

    @Test
    void comparingValuesUsing_containsAtLeastEntriesIn_failsWrongValue() {
        ImmutableMap<String, Integer> expected = ImmutableMap.of("def", 456, "abc", 321);
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456", "ghi", "789");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeastEntriesIn(expected));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "---",
                "expected to contain at least",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "for key", "abc");
        assertFailureValue(
                failure,
                "expected value", "321");
        assertFailureValue(
                failure,
                "but got value", "123");
    }

    @Test
    void comparingValuesUsing_containsAtLeastEntriesIn_diffMissingAndWrongValue() {
        ImmutableMap<String, Integer> expected = ImmutableMap.of("abc", 30, "def", 60, "ghi", 90);
        ImmutableMap<String, Integer> actual = ImmutableMap.of("abc", 35, "fed", 60, "ghi", 101);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(WITHIN_10_OF)
                        .containsAtLeastEntriesIn(expected));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "diff",
                "missing keys",
                "for key",
                "expected value",
                "---",
                "expected to contain at least",
                "testing whether",
                "but was");
        assertFailureValueIndexed(
                failure,
                "for key", 0, "ghi");
        assertFailureValueIndexed(
                failure,
                "expected value", 0, "90");
        assertFailureValue(
                failure,
                "but got value", "101");
        assertFailureValue(
                failure,
                "diff", "11");
        assertFailureValueIndexed(
                failure,
                "for key", 1, "def");
        assertFailureValueIndexed(
                failure,
                "expected value", 1, "60");
    }

    @Test
    void comparingValuesUsing_containsAtLeastEntriesIn_handlesFormatDiffExceptions() {
        ImmutableMap<String, Integer> expected = ImmutableMap.of("abc", 30, "def", 60, "ghi", 90);
        Map<String, Integer> actual = new LinkedHashMap<>();
        actual.put("abc", 35);
        actual.put("def", null);
        actual.put("ghi", 95);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(WITHIN_10_OF)
                        .containsAtLeastEntriesIn(expected));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "---",
                "expected to contain at least",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing values",
                "first exception",
                "additionally, one or more exceptions were thrown while formatting diffs",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception", 0)
                .startsWith("compare(null, 60) threw java.lang.NullPointerException");
        assertThatFailure(failure)
                .factValue("first exception", 1)
                .startsWith("formatDiff(null, 60) threw java.lang.NullPointerException");
    }

    @Test
    void comparingValuesUsing_containsAtLeastEntriesIn_inOrder_failsOutOfOrder() {
        ImmutableMap<String, Integer> expected = ImmutableMap.of("ghi", 789, "abc", 123);
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456", "ghi", "789");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeastEntriesIn(expected)
                        .inOrder());
        assertFailureKeys(
                failure,
                "required entries were all found, but order was wrong",
                "expected to contain at least",
                "testing whether",
                "but was");
    }

    @Test
    void comparingValuesUsing_containsAtLeastEntriesIn_empty() {
        ImmutableMap<String, Integer> expected = ImmutableMap.of();
        ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeastEntriesIn(expected);
    }

    @Test
    void comparingValuesUsing_containsAtLeastEntriesIn_wrongValueTypeInExpectedActual() {
        ImmutableMap<String, Integer> expected = ImmutableMap.of("def", 456);
        ImmutableMap<String, Object> actual = ImmutableMap.of("abc", "123", "def", 456);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeastEntriesIn(expected));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "---",
                "expected to contain at least",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing values",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(456, 456) threw java.lang.ClassCastException");
    }

    @Test
    void comparingValuesUsing_containsAtLeastEntriesIn_wrongValueTypeInUnexpectedActual_success() {
        ImmutableMap<String, Integer> expected = ImmutableMap.of("abc", 123);
        ImmutableMap<String, Object> actual = ImmutableMap.of("abc", "123", "def", 456);
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeastEntriesIn(expected);
    }

    @Test
    void formattingDiffsUsing_success() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("ghi", 300, "def", 200, "abc", 100);
        assertThat(actual)
                .formattingDiffsUsing(INT_DIFF_FORMATTER)
                .containsExactly("abc", 100, "def", 200, "ghi", 300);
    }

    @Test
    void formattingDiffsUsing_failure() {
        ImmutableMap<String, Integer> actual = ImmutableMap.of("ghi", 300, "def", 201, "abc", 100);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .formattingDiffsUsing(INT_DIFF_FORMATTER)
                        .containsExactly("abc", 100, "def", 200, "ghi", 300));
        assertFailureKeys(
                failure,
                "keys with wrong values",
                "for key",
                "expected value",
                "but got value",
                "diff",
                "---",
                "expected",
                "but was");
        assertFailureValue(
                failure,
                "expected value", "200");
        assertFailureValue(
                failure,
                "but got value", "201");
        assertFailureValue(
                failure,
                "diff", "1");
    }
}
