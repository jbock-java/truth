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
package io.jbock.common.truth;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import static io.jbock.common.truth.TestCorrespondences.CASE_INSENSITIVE_EQUALITY;
import static io.jbock.common.truth.TestCorrespondences.INT_DIFF_FORMATTER;
import static io.jbock.common.truth.TestCorrespondences.STRING_PARSES_TO_INTEGER_CORRESPONDENCE;
import static io.jbock.common.truth.TestCorrespondences.WITHIN_10_OF;
import static io.jbock.common.truth.Truth.assertThat;
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
        Map<String, Integer> actual = mapOf();

        assertThat(actual).containsExactly();
        assertThat(actual).containsExactly().inOrder();
        assertThat(actual).containsExactlyEntriesIn(actual);
        assertThat(actual).containsExactlyEntriesIn(actual).inOrder();
    }

    @Test
    void containsExactlyEmpty_fails() {
        Map<String, Integer> actual = mapOf("jan", 1);
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
        Map<String, Integer> actual = mapOf("jan", 1);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactlyEntriesIn(mapOf()));
        assertFailureKeys(
                failure,
                "expected to be empty", "but was");
    }

    @Test
    void containsExactlyOneEntry() {
        Map<String, Integer> actual = mapOf("jan", 1);

        assertThat(actual).containsExactly("jan", 1);
        assertThat(actual).containsExactly("jan", 1).inOrder();
        assertThat(actual).containsExactlyEntriesIn(actual);
        assertThat(actual).containsExactlyEntriesIn(actual).inOrder();
    }

    @Test
    void containsExactlyMultipleEntries() {
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2, "march", 3);

        assertThat(actual).containsExactly("march", 3, "jan", 1, "feb", 2);
        assertThat(actual).containsExactly("jan", 1, "feb", 2, "march", 3).inOrder();
        assertThat(actual).containsExactlyEntriesIn(actual);
        assertThat(actual).containsExactlyEntriesIn(actual).inOrder();
    }

    @Test
    void containsExactlyDuplicateKeys() {
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2, "march", 3);

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
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2, "march", 3);

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
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2, "march", 3);
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
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2, "march", 3);
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
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2);
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
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2, "march", 3);
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
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2, "march", 3);
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
        Map<String, Integer> actual = mapOf("jan", 1, "march", 3);
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
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2, "march", 3);
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
        Map<String, Integer> actual = mapOf("jan", 1, "march", 3);
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
        Map<String, Integer> actual = mapOf("jan", 1, "march", 3);
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
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2, "march", 3);
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
        Map<String, Integer> actual =
                mapOf("jan", 1, "feb", 2, "march", 3, "april", 4, "may", 5);
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
                () -> assertThat(mapOf("jan", 1L, "feb", 2L))
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
                () -> assertThat(mapOf(1L, "jan", 1, "feb"))
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
                () -> assertThat(mapOf(1L, "jan", 2, "feb"))
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
        Map<String, Integer> actual = mapOf("key", 1);

        assertThat(actual).containsAtLeastEntriesIn(mapOf());
        assertThat(actual).containsAtLeastEntriesIn(mapOf()).inOrder();
    }

    @Test
    void containsAtLeastOneEntry() {
        Map<String, Integer> actual = mapOf("jan", 1);

        assertThat(actual).containsAtLeast("jan", 1);
        assertThat(actual).containsAtLeast("jan", 1).inOrder();
        assertThat(actual).containsAtLeastEntriesIn(actual);
        assertThat(actual).containsAtLeastEntriesIn(actual).inOrder();
    }

    @Test
    void containsAtLeastMultipleEntries() {
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2, "mar", 3, "apr", 4);

        assertThat(actual).containsAtLeast("apr", 4, "jan", 1, "feb", 2);
        assertThat(actual).containsAtLeast("jan", 1, "feb", 2, "apr", 4).inOrder();
        assertThat(actual).containsAtLeastEntriesIn(mapOf("apr", 4, "jan", 1, "feb", 2));
        assertThat(actual).containsAtLeastEntriesIn(actual).inOrder();
    }

    @Test
    void containsAtLeastDuplicateKeys() {
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2, "march", 3);

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
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2, "march", 3);

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
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2);
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
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2, "march", 3);
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
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2, "march", 3);
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
        Map<String, Integer> actual = mapOf("jan", 1, "march", 3);
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
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2, "march", 3);
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
        Map<String, Integer> actual =
                mapOf("jan", 1, "feb", 2, "march", 3, "april", 4, "may", 5);

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
                () -> assertThat(mapOf("jan", 1L, "feb", 2L, "mar", 3L))
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
                () -> assertThat(mapOf(1L, "jan", 1, "feb"))
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
                () -> assertThat(mapOf(1L, "jan", 2, "feb"))
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
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2, "march", 3);
        Map<String, Integer> expectedMap = mapOf("jan", 1, "feb", 2, "march", 3);

        assertThat(actual).isEqualTo(expectedMap);
    }

    @Test
    void isEqualToFailureExtraMissingAndDiffering() {
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2, "march", 3);
        Map<String, Integer> expectedMap = mapOf("jan", 1, "april", 4, "march", 5);
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
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2, "march", 3);
        Map<String, Integer> expectedMap = mapOf("jan", 1, "feb", 2, "march", 4);
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
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2, "march", 3);
        Map<String, Integer> expectedMap = mapOf("jan", 1, "feb", 2);
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
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2);
        Map<String, Integer> expectedMap = mapOf("jan", 1, "feb", 2, "march", 3);

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
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2, "march", 3);
        Map<String, Integer> expectedMap = mapOf("jan", 1, "feb", 2, "mar", 3);
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
        Map<String, Number> actual =
                mapOf("jan", 1, "feb", 2, "march", 3L);
        Map<String, Integer> expectedMap = mapOf("jan", 1, "feb", 2, "march", 3);
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
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2, "march", 3);
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
                        .isEqualTo(mapOf()));
    }

    @Test
    void isEqualToActualMapOtherNull() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(mapOf())
                        .isEqualTo(null));
    }

    @Test
    void isNotEqualTo() {
        Map<String, Integer> actual = mapOf("jan", 1, "feb", 2, "march", 3);
        Map<String, Integer> unexpected = mapOf("jan", 1, "feb", 2, "march", 3);
        assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .isNotEqualTo(unexpected));
    }

    @Test
    void isEmpty() {
        Map<String, String> actual = mapOf();
        assertThat(actual).isEmpty();
    }

    @Test
    void isEmptyWithFailure() {
        Map<Integer, Integer> actual = mapOf(1, 5);
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
        Map<Integer, Integer> actual = mapOf(1, 5);
        assertThat(actual).isNotEmpty();
    }

    @Test
    void isNotEmptyWithFailure() {
        Map<Integer, Integer> actual = mapOf();
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
        assertThat(mapOf(1, 2, 3, 4)).hasSize(2);
    }

    @Test
    void hasSizeZero() {
        assertThat(mapOf()).hasSize(0);
    }

    @Test
    void hasSizeNegative() {
        try {
            assertThat(mapOf(1, 2)).hasSize(-1);
            fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    void containsKey() {
        Map<String, String> actual = mapOf("kurt", "kluever");
        assertThat(actual).containsKey("kurt");
    }

    @Test
    void containsKeyFailure() {
        Map<String, String> actual = mapOf("kurt", "kluever");
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
        Map<String, String> actual = mapOf("kurt", "kluever");
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
                () -> assertThat(mapOf(1L, "value1", 2L, "value2", "1", "value3"))
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
        Map<String, String> actual = mapOf("kurt", "kluever");
        assertThat(actual).doesNotContainKey("greg");
        assertThat(actual).doesNotContainKey(null);
    }

    @Test
    void doesNotContainKeyFailure() {
        Map<String, String> actual = mapOf("kurt", "kluever");
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
        Map<String, String> actual = mapOf("kurt", "kluever");
        assertThat(actual).containsEntry("kurt", "kluever");
    }

    @Test
    void containsEntryFailure() {
        Map<String, String> actual = mapOf("kurt", "kluever");
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
                () -> assertThat(mapOf(1L, "value1", 2L, "value2"))
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
                () -> assertThat(mapOf(1, "null"))
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
        Map<String, String> actual = mapOf("kurt", "kluever");
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
        Map<Number, String> actual = mapOf(1, "actual int", 1L, "actual long");
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
        Map<String, String> actual = mapOf("kurt", "kluever");
        assertThat(actual).doesNotContainEntry("greg", "kick");
        assertThat(actual).doesNotContainEntry(null, null);
        assertThat(actual).doesNotContainEntry("kurt", null);
        assertThat(actual).doesNotContainEntry(null, "kluever");
    }

    @Test
    void doesNotContainEntryFailure() {
        Map<String, String> actual = mapOf("kurt", "kluever");
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
        Map<String, String> actual = mapOf("a", "A");
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
        Map<String, String> actual = mapOf("a", "A");
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
        Map<String, String> actual = mapOf("a", "A");
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
        Map<String, String> actual = mapOf("a", "A");
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
        Map<String, String> actual = mapOf("a", "A");
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
        Map<String, String> actual = mapOf("a", "A");
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
        Map<String, String> actual = mapOf("abc", "123", "def", "456");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsEntry("def", 456);
    }

    @Test
    void comparingValuesUsing_containsEntry_failsExpectedKeyHasWrongValue() {
        Map<String, String> actual = mapOf("abc", "+123", "def", "+456");
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
        Map<String, String> actual = mapOf("abc", "+123", "def", "+456");
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
        Map<String, String> actual = mapOf("abc", "+123", "def", "+456");
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
        Map<String, Integer> actual = mapOf("abc", 35, "def", 71);
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
        Map<String, String> actual = mapOf("abc", "+123", "def", "+456");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .doesNotContainEntry("def", 123);
    }

    @Test
    void comparingValuesUsing_doesNotContainEntry_successWrongKeyHasExcludedValue() {
        Map<String, String> actual = mapOf("abc", "+123", "def", "+456");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .doesNotContainEntry("xyz", 456);
    }

    @Test
    void comparingValuesUsing_doesNotContainEntry_successMissingExcludedKeyAndValue() {
        Map<String, String> actual = mapOf("abc", "123", "def", "456");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .doesNotContainEntry("xyz", 321);
    }

    @Test
    void comparingValuesUsing_doesNotContainEntry_failure() {
        Map<String, String> actual = mapOf("abc", "+123", "def", "+456");
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
        Map<String, String> actual = mapOf("abc", "123", "def", "456");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactly("def", 456, "abc", 123);
    }

    @Test
    void comparingValuesUsing_containsExactly_inOrder_success() {
        Map<String, String> actual = mapOf("abc", "123", "def", "456");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactly("abc", 123, "def", 456)
                .inOrder();
    }

    @Test
    void comparingValuesUsing_containsExactly_failsExtraEntry() {
        Map<String, String> actual = mapOf("abc", "123", "def", "456");
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
        Map<String, String> actual = mapOf("abc", "123", "def", "456");
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
        Map<String, String> actual = mapOf("abc", "123", "def", "456");
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
        Map<String, String> actual = mapOf("abc", "123", "def", "456");
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
        Map<String, String> actual = mapOf("abc", "123", "def", "456");
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
        Map<String, Object> actual = mapOf("abc", "123", "def", 456);
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
        Map<String, String> actual = mapOf("abc", "123", "def", "456");
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
        Map<String, Integer> expected = mapOf("def", 456, "abc", 123);
        Map<String, String> actual = mapOf("abc", "123", "def", "456");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactlyEntriesIn(expected);
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_inOrder_success() {
        Map<String, Integer> expected = mapOf("abc", 123, "def", 456);
        Map<String, String> actual = mapOf("abc", "123", "def", "456");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactlyEntriesIn(expected)
                .inOrder();
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_failsExtraEntry() {
        Map<String, Integer> expected = mapOf("def", 456);
        Map<String, String> actual = mapOf("abc", "123", "def", "456");
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
        Map<String, Integer> expected = mapOf("def", 456, "xyz", 999, "abc", 123);
        Map<String, String> actual = mapOf("abc", "123", "def", "456");
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
        Map<String, Integer> expected = mapOf("def", 456, "cab", 123);
        Map<String, String> actual = mapOf("abc", "123", "def", "456");
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
        Map<String, Integer> expected = mapOf("def", 456, "abc", 321);
        Map<String, String> actual = mapOf("abc", "123", "def", "456");
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
        Map<String, Integer> expected = mapOf("abc", 30, "def", 60, "ghi", 90);
        Map<String, Integer> actual = mapOf("abc", 35, "fed", 60, "ghi", 101);
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
        Map<String, Integer> expected = mapOf("abc", 30, "def", 60, "ghi", 90);
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
        Map<String, Integer> expected = mapOf("def", 456, "abc", 123);
        Map<String, String> actual = mapOf("abc", "123", "def", "456");
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
        Map<String, Integer> expected = mapOf();
        Map<String, String> actual = mapOf();
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactlyEntriesIn(expected);
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_failsEmpty() {
        Map<String, Integer> expected = mapOf();
        Map<String, String> actual = mapOf("abc", "123");
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
        Map<String, Integer> expected = mapOf("def", 456, "abc", 123);
        Map<String, Object> actual = mapOf("abc", "123", "def", 456);
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
        Map<String, String> actual = mapOf("abc", "123", "def", "456", "ghi", "789");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeast("def", 456, "abc", 123);
    }

    @Test
    void comparingValuesUsing_containsAtLeast_inOrder_success() {
        Map<String, String> actual = mapOf("abc", "123", "ghi", "789", "def", "456");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeast("abc", 123, "def", 456)
                .inOrder();
    }

    @Test
    void comparingValuesUsing_containsAtLeast_failsMissingEntry() {
        Map<String, String> actual = mapOf("abc", "123", "def", "456", "ghi", "789");
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
        Map<String, String> actual = mapOf("abc", "123", "def", "456");
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
        Map<String, String> actual = mapOf("abc", "123", "def", "456");
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
        Map<String, String> actual = mapOf("abc", "123", "def", "456", "ghi", "789");
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
        Map<String, Object> actual = mapOf("abc", "123", "def", 456);
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
        Map<String, Object> actual = mapOf("abc", "123", "def", 456);
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeast("abc", 123);
    }

    @Test
    void comparingValuesUsing_containsAtLeast_wrongValueTypeInExpected() {
        Map<String, String> actual = mapOf("abc", "123", "def", "456", "ghi", "789");
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
        Map<String, Integer> expected = mapOf("def", 456, "abc", 123);
        Map<String, String> actual = mapOf("abc", "123", "def", "456", "ghi", "789");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeastEntriesIn(expected);
    }

    @Test
    void comparingValuesUsing_containsAtLeastEntriesIn_inOrder_success() {
        Map<String, Integer> expected = mapOf("abc", 123, "ghi", 789);
        Map<String, String> actual = mapOf("abc", "123", "def", "456", "ghi", "789");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeastEntriesIn(expected)
                .inOrder();
    }

    @Test
    void comparingValuesUsing_containsAtLeastEntriesIn_failsMissingEntry() {
        Map<String, Integer> expected = mapOf("def", 456, "xyz", 999, "abc", 123);
        Map<String, String> actual = mapOf("abc", "123", "def", "456", "ghi", "789");
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
        Map<String, Integer> expected = mapOf("def", 456, "cab", 123);
        Map<String, String> actual = mapOf("abc", "123", "def", "456");
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
        Map<String, Integer> expected = mapOf("def", 456, "abc", 321);
        Map<String, String> actual = mapOf("abc", "123", "def", "456", "ghi", "789");
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
        Map<String, Integer> expected = mapOf("abc", 30, "def", 60, "ghi", 90);
        Map<String, Integer> actual = mapOf("abc", 35, "fed", 60, "ghi", 101);
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
        Map<String, Integer> expected = mapOf("abc", 30, "def", 60, "ghi", 90);
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
        Map<String, Integer> expected = mapOf("ghi", 789, "abc", 123);
        Map<String, String> actual = mapOf("abc", "123", "def", "456", "ghi", "789");
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
        Map<String, Integer> expected = mapOf();
        Map<String, String> actual = mapOf("abc", "123", "def", "456");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeastEntriesIn(expected);
    }

    @Test
    void comparingValuesUsing_containsAtLeastEntriesIn_wrongValueTypeInExpectedActual() {
        Map<String, Integer> expected = mapOf("def", 456);
        Map<String, Object> actual = mapOf("abc", "123", "def", 456);
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
        Map<String, Integer> expected = mapOf("abc", 123);
        Map<String, Object> actual = mapOf("abc", "123", "def", 456);
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeastEntriesIn(expected);
    }

    @Test
    void formattingDiffsUsing_success() {
        Map<String, Integer> actual = mapOf("ghi", 300, "def", 200, "abc", 100);
        assertThat(actual)
                .formattingDiffsUsing(INT_DIFF_FORMATTER)
                .containsExactly("abc", 100, "def", 200, "ghi", 300);
    }

    @Test
    void formattingDiffsUsing_failure() {
        Map<String, Integer> actual = mapOf("ghi", 300, "def", 201, "abc", 100);
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

    private static <K, V> Map<K, V> mapOf() {
        return Map.of();
    }

    @SuppressWarnings("unchecked")
    private static <K, V> Map<K, V> mapOf(K k1, V v1, Object... rest) {
        Map<Object, Object> result = new LinkedHashMap<>();
        result.put(k1, v1);
        for (int i = 0, restLength = rest.length; i < restLength; i += 2) {
            Object k = rest[i];
            Object v = rest[i + 1];
            result.put(k, v);
        }
        return (Map<K, V>) result;
    }
}
