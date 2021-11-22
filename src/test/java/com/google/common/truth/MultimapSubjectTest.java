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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.TestCorrespondences.CASE_INSENSITIVE_EQUALITY;
import static com.google.common.truth.TestCorrespondences.CASE_INSENSITIVE_EQUALITY_HALF_NULL_SAFE;
import static com.google.common.truth.TestCorrespondences.STRING_PARSES_TO_INTEGER_CORRESPONDENCE;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for Multimap Subjects.
 *
 * @author Daniel Ploch
 * @author Kurt Alfred Kluever
 */
class MultimapSubjectTest extends BaseSubjectTestCase {

    @Test
    void listMultimapIsEqualTo_passes() {
        ImmutableListMultimap<String, String> multimapA =
                ImmutableListMultimap.<String, String>builder()
                        .putAll("kurt", "kluever", "russell", "cobain")
                        .build();
        ImmutableListMultimap<String, String> multimapB =
                ImmutableListMultimap.<String, String>builder()
                        .putAll("kurt", "kluever", "russell", "cobain")
                        .build();

        assertThat(multimapA.equals(multimapB)).isTrue();

        assertThat(multimapA).isEqualTo(multimapB);
    }

    @Test
    void listMultimapIsEqualTo_fails() {
        ImmutableListMultimap<String, String> multimapA =
                ImmutableListMultimap.<String, String>builder()
                        .putAll("kurt", "kluever", "russell", "cobain")
                        .build();
        ImmutableListMultimap<String, String> multimapB =
                ImmutableListMultimap.<String, String>builder()
                        .putAll("kurt", "kluever", "cobain", "russell")
                        .build();
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(multimapA)
                        .isEqualTo(multimapB));
        assertFailureKeys(
                failure,
                "contents match, but order was wrong",
                "keys with out-of-order values",
                "---",
                "expected",
                "but was");
        assertFailureValue(
                failure,
                "keys with out-of-order values", "[kurt]");
        assertFailureValue(
                failure,
                "expected", "{kurt=[kluever, cobain, russell]}");
        assertFailureValue(
                failure,
                "but was", "{kurt=[kluever, russell, cobain]}");
    }

    @Test
    void setMultimapIsEqualTo_passes() {
        ImmutableSetMultimap<String, String> multimapA =
                ImmutableSetMultimap.<String, String>builder()
                        .putAll("kurt", "kluever", "russell", "cobain")
                        .build();
        ImmutableSetMultimap<String, String> multimapB =
                ImmutableSetMultimap.<String, String>builder()
                        .putAll("kurt", "kluever", "cobain", "russell")
                        .build();

        assertThat(multimapA.equals(multimapB)).isTrue();

        assertThat(multimapA).isEqualTo(multimapB);
    }

    @Test
    void setMultimapIsEqualTo_fails() {
        ImmutableSetMultimap<String, String> multimapA =
                ImmutableSetMultimap.<String, String>builder()
                        .putAll("kurt", "kluever", "russell", "cobain")
                        .build();
        ImmutableSetMultimap<String, String> multimapB =
                ImmutableSetMultimap.<String, String>builder().putAll("kurt", "kluever", "russell").build();
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(multimapA)
                        .isEqualTo(multimapB));
        assertFailureKeys(
                failure,
                "unexpected", "---", "expected", "but was");
        assertFailureValue(
                failure,
                "unexpected", "{kurt=[cobain]}");
        assertFailureValue(
                failure,
                "expected", "{kurt=[kluever, russell]}");
        assertFailureValue(
                failure,
                "but was", "{kurt=[kluever, russell, cobain]}");
    }

    @Test
    void setMultimapIsEqualToListMultimap_fails() {
        ImmutableSetMultimap<String, String> multimapA =
                ImmutableSetMultimap.<String, String>builder()
                        .putAll("kurt", "kluever", "russell", "cobain")
                        .build();
        ImmutableListMultimap<String, String> multimapB =
                ImmutableListMultimap.<String, String>builder()
                        .putAll("kurt", "kluever", "russell", "cobain")
                        .build();
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(multimapA)
                        .isEqualTo(multimapB));
        assertFailureKeys(
                failure,
                "expected",
                "an instance of",
                "but was",
                "an instance of",
                "a SetMultimap cannot equal a ListMultimap if either is non-empty");
        assertFailureValueIndexed(
                failure,
                "an instance of", 0, "ListMultimap");
        assertFailureValueIndexed(
                failure,
                "an instance of", 1, "SetMultimap");
    }

    @Test
    void isEqualTo_failsWithSameToString() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(ImmutableMultimap.of(1, "a", 1, "b", 2, "c"))
                        .isEqualTo(ImmutableMultimap.of(1L, "a", 1L, "b", 2L, "c")));
        assertFailureKeys(
                failure,
                "missing", "unexpected", "---", "expected", "but was");
        assertFailureValue(
                failure,
                "missing", "[1=a, 1=b, 2=c] (Map.Entry<java.lang.Long, java.lang.String>)");
        assertFailureValue(
                failure,
                "unexpected", "[1=a, 1=b, 2=c] (Map.Entry<java.lang.Integer, java.lang.String>)");
        assertFailureValue(
                failure,
                "expected", "{1=[a, b], 2=[c]}");
        assertFailureValue(
                failure,
                "but was", "{1=[a, b], 2=[c]}");
    }

    @Test
    void multimapIsEmpty() {
        ImmutableMultimap<String, String> multimap = ImmutableMultimap.of();
        assertThat(multimap).isEmpty();
    }

    @Test
    void multimapIsEmptyWithFailure() {
        ImmutableMultimap<Integer, Integer> multimap = ImmutableMultimap.of(1, 5);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(multimap)
                        .isEmpty());
        assertFailureKeys(
                failure,
                "expected to be empty", "but was");
    }

    @Test
    void multimapIsNotEmpty() {
        ImmutableMultimap<Integer, Integer> multimap = ImmutableMultimap.of(1, 5);
        assertThat(multimap).isNotEmpty();
    }

    @Test
    void multimapIsNotEmptyWithFailure() {
        ImmutableMultimap<Integer, Integer> multimap = ImmutableMultimap.of();
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(multimap)
                        .isNotEmpty());
        assertFailureKeys(
                failure,
                "expected not to be empty");
    }

    @Test
    void hasSize() {
        assertThat(ImmutableMultimap.of(1, 2, 3, 4)).hasSize(2);
    }

    @Test
    void hasSizeZero() {
        assertThat(ImmutableMultimap.of()).hasSize(0);
    }

    @Test
    void hasSizeNegative() {
        try {
            assertThat(ImmutableMultimap.of(1, 2)).hasSize(-1);
            fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    void containsKey() {
        ImmutableMultimap<String, String> multimap = ImmutableMultimap.of("kurt", "kluever");
        assertThat(multimap).containsKey("kurt");
    }

    @Test
    void containsKeyFailure() {
        ImmutableMultimap<String, String> multimap = ImmutableMultimap.of("kurt", "kluever");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(multimap)
                        .containsKey("daniel"));
        assertFailureKeys(
                failure,
                "value of", "expected to contain", "but was", "multimap was");
        assertFailureValue(
                failure,
                "value of", "multimap.keySet()");
        assertFailureValue(
                failure,
                "expected to contain", "daniel");
        assertFailureValue(
                failure,
                "but was", "[kurt]");
    }

    @Test
    void containsKeyNull() {
        Multimap<String, String> multimap = HashMultimap.create();
        multimap.put(null, "null");
        assertThat(multimap).containsKey(null);
    }

    @Test
    void containsKeyNullFailure() {
        ImmutableMultimap<String, String> multimap = ImmutableMultimap.of("kurt", "kluever");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(multimap)
                        .containsKey(null));
        assertFailureKeys(
                failure,
                "value of", "expected to contain", "but was", "multimap was");
        assertFailureValue(
                failure,
                "value of", "multimap.keySet()");
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
                () -> assertThat(ImmutableMultimap.of(1L, "value1a", 1L, "value1b", 2L, "value2", "1", "value3"))
                        .containsKey(1));
        assertFailureKeys(
                failure,
                "value of",
                "expected to contain",
                "an instance of",
                "but did not",
                "though it did contain",
                "full contents",
                "multimap was");
        assertFailureValue(
                failure,
                "value of", "multimap.keySet()");
        assertFailureValue(
                failure,
                "expected to contain", "1");
    }

    @Test
    void doesNotContainKey() {
        ImmutableMultimap<String, String> multimap = ImmutableMultimap.of("kurt", "kluever");
        assertThat(multimap).doesNotContainKey("daniel");
        assertThat(multimap).doesNotContainKey(null);
    }

    @Test
    void doesNotContainKeyFailure() {
        ImmutableMultimap<String, String> multimap = ImmutableMultimap.of("kurt", "kluever");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(multimap)
                        .doesNotContainKey("kurt"));
        assertFailureKeys(
                failure,
                "value of", "expected not to contain", "but was", "multimap was");
        assertFailureValue(
                failure,
                "value of", "multimap.keySet()");
        assertFailureValue(
                failure,
                "expected not to contain", "kurt");
        assertFailureValue(
                failure,
                "but was", "[kurt]");
    }

    @Test
    void doesNotContainNullKeyFailure() {
        Multimap<String, String> multimap = HashMultimap.create();
        multimap.put(null, "null");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(multimap)
                        .doesNotContainKey(null));
        assertFailureKeys(
                failure,
                "value of", "expected not to contain", "but was", "multimap was");
        assertFailureValue(
                failure,
                "value of", "multimap.keySet()");
        assertFailureValue(
                failure,
                "expected not to contain", "null");
        assertFailureValue(
                failure,
                "but was", "[null]");
    }

    @Test
    void containsEntry() {
        ImmutableMultimap<String, String> multimap = ImmutableMultimap.of("kurt", "kluever");
        assertThat(multimap).containsEntry("kurt", "kluever");
    }

    @Test
    void containsEntryFailure() {
        ImmutableMultimap<String, String> multimap = ImmutableMultimap.of("kurt", "kluever");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(multimap)
                        .containsEntry("daniel", "ploch"));
        assertFailureKeys(
                failure,
                "expected to contain entry", "but was");
        assertFailureValue(
                failure,
                "expected to contain entry", "daniel=ploch");
        assertFailureValue(
                failure,
                "but was", "{kurt=[kluever]}");
    }

    @Test
    void containsEntryWithNullValueNullExpected() {
        ListMultimap<String, String> actual = ArrayListMultimap.create();
        actual.put("a", null);
        assertThat(actual).containsEntry("a", null);
    }

    @Test
    void failContainsEntry() {
        ImmutableMultimap<String, String> actual = ImmutableMultimap.of("a", "A");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsEntry("b", "B"));
        assertFailureKeys(
                failure,
                "expected to contain entry", "but was");
        assertFailureValue(
                failure,
                "expected to contain entry", "b=B");
        assertFailureValue(
                failure,
                "but was", "{a=[A]}");
    }

    @Test
    void failContainsEntryFailsWithWrongValueForKey() {
        ImmutableMultimap<String, String> actual = ImmutableMultimap.of("a", "A");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsEntry("a", "a"));
        assertFailureKeys(
                failure,
                "expected to contain entry",
                "but did not",
                "though it did contain values with that key",
                "full contents");
        assertFailureValue(
                failure,
                "though it did contain values with that key", "[A]");
    }

    @Test
    void failContainsEntryWithNullValuePresentExpected() {
        ListMultimap<String, String> actual = ArrayListMultimap.create();
        actual.put("a", null);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsEntry("a", "A"));
        assertFailureKeys(
                failure,
                "expected to contain entry",
                "but did not",
                "though it did contain values with that key",
                "full contents");
        assertFailureValue(
                failure,
                "though it did contain values with that key", "[null]");
    }

    @Test
    void failContainsEntryWithPresentValueNullExpected() {
        ImmutableMultimap<String, String> actual = ImmutableMultimap.of("a", "A");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsEntry("a", null));
        assertFailureKeys(
                failure,
                "expected to contain entry",
                "but did not",
                "though it did contain values with that key",
                "full contents");
        assertFailureValue(
                failure,
                "expected to contain entry", "a=null");
    }

    @Test
    void failContainsEntryFailsWithWrongKeyForValue() {
        ImmutableMultimap<String, String> actual = ImmutableMultimap.of("a", "A");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsEntry("b", "A"));
        assertFailureKeys(
                failure,
                "expected to contain entry",
                "but did not",
                "though it did contain keys with that value",
                "full contents");
        assertFailureValue(
                failure,
                "though it did contain keys with that value", "[a]");
    }

    @Test
    void containsEntry_failsWithSameToString() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(ImmutableMultimap.builder()
                        .put(1, "1")
                        .put(1, 1L)
                        .put(1L, 1)
                        .put(2, 3)
                        .build())
                        .containsEntry(1, 1));
        assertFailureKeys(
                failure,
                "expected to contain entry",
                "an instance of",
                "but did not",
                "though it did contain",
                "full contents");
        assertFailureValue(
                failure,
                "expected to contain entry", "1=1");
        assertFailureValue(
                failure,
                "an instance of", "Map.Entry<java.lang.Integer, java.lang.Integer>");
        assertFailureValue(
                failure,
                "though it did contain",
                "[1=1 (Map.Entry<java.lang.Integer, java.lang.String>), "
                        + "1=1 (Map.Entry<java.lang.Integer, java.lang.Long>), "
                        + "1=1 (Map.Entry<java.lang.Long, java.lang.Integer>)]");
    }

    @Test
    void doesNotContainEntry() {
        ImmutableMultimap<String, String> multimap = ImmutableMultimap.of("kurt", "kluever");
        assertThat(multimap).doesNotContainEntry("daniel", "ploch");
    }

    @Test
    void doesNotContainEntryFailure() {
        ImmutableMultimap<String, String> multimap = ImmutableMultimap.of("kurt", "kluever");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(multimap)
                        .doesNotContainEntry("kurt", "kluever"));
        assertFailureKeys(
                failure,
                "value of", "expected not to contain", "but was");
        assertFailureValue(
                failure,
                "value of", "multimap.entries()");
        assertFailureValue(
                failure,
                "expected not to contain", "kurt=kluever");
        assertFailureValue(
                failure,
                "but was", "[kurt=kluever]");
    }

    @Test
    void valuesForKey() {
        ImmutableMultimap<Integer, String> multimap =
                ImmutableMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");

        assertThat(multimap).valuesForKey(3).hasSize(3);
        assertThat(multimap).valuesForKey(4).containsExactly("four", "five");
        assertThat(multimap).valuesForKey(3).containsAtLeast("one", "six").inOrder();
        assertThat(multimap).valuesForKey(5).isEmpty();
    }

    @Test
    void valuesForKeyListMultimap() {
        ImmutableListMultimap<Integer, String> multimap =
                ImmutableListMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");

        assertThat(multimap).valuesForKey(4).isInStrictOrder();
    }

    @Test
    void containsExactlyEntriesIn() {
        ImmutableListMultimap<Integer, String> listMultimap =
                ImmutableListMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");
        ImmutableSetMultimap<Integer, String> setMultimap = ImmutableSetMultimap.copyOf(listMultimap);

        assertThat(listMultimap).containsExactlyEntriesIn(setMultimap);
    }

    @Test
    void containsExactlyNoArg() {
        ImmutableMultimap<Integer, String> actual = ImmutableMultimap.of();

        assertThat(actual).containsExactly();
        assertThat(actual).containsExactly().inOrder();

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(ImmutableMultimap.of(42, "Answer", 42, "6x7"))
                        .containsExactly());
        assertFailureKeys(
                failure,
                "expected to be empty", "but was");
    }

    @Test
    void containsExactlyEmpty() {
        ImmutableListMultimap<Integer, String> actual = ImmutableListMultimap.of();
        ImmutableSetMultimap<Integer, String> expected = ImmutableSetMultimap.of();

        assertThat(actual).containsExactlyEntriesIn(expected);
        assertThat(actual).containsExactlyEntriesIn(expected).inOrder();
    }

    @Test
    void containsExactlyRejectsNull() {
        ImmutableMultimap<Integer, String> multimap =
                ImmutableMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");

        try {
            assertThat(multimap).containsExactlyEntriesIn(null);
            fail("Should have thrown.");
        } catch (NullPointerException expected) {
        }
    }

    @Test
    void containsExactlyRespectsDuplicates() {
        ImmutableListMultimap<Integer, String> actual =
                ImmutableListMultimap.of(3, "one", 3, "two", 3, "one", 4, "five", 4, "five");
        ImmutableListMultimap<Integer, String> expected =
                ImmutableListMultimap.of(3, "two", 4, "five", 3, "one", 4, "five", 3, "one");

        assertThat(actual).containsExactlyEntriesIn(expected);
    }

    @Test
    void containsExactlyRespectsDuplicatesFailure() {
        ImmutableListMultimap<Integer, String> actual =
                ImmutableListMultimap.of(3, "one", 3, "two", 3, "one", 4, "five", 4, "five");
        ImmutableSetMultimap<Integer, String> expected = ImmutableSetMultimap.copyOf(actual);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactlyEntriesIn(expected));
        assertFailureKeys(
                failure,
                "unexpected", "---", "expected", "but was");
        assertFailureValue(
                failure,
                "unexpected", "{3=[one], 4=[five]}");
        assertFailureValue(
                failure,
                "expected", "{3=[one, two], 4=[five]}");
        assertFailureValue(
                failure,
                "but was", "{3=[one, two, one], 4=[five, five]}");
    }

    @Test
    void containsExactlyFailureMissing() {
        ImmutableMultimap<Integer, String> expected =
                ImmutableMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");
        ListMultimap<Integer, String> actual = LinkedListMultimap.create(expected);
        actual.remove(3, "six");
        actual.remove(4, "five");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactlyEntriesIn(expected));
        assertFailureKeys(
                failure,
                "missing", "---", "expected", "but was");
        assertFailureValue(
                failure,
                "missing", "{3=[six], 4=[five]}");
    }

    @Test
    void containsExactlyFailureExtra() {
        ImmutableMultimap<Integer, String> expected =
                ImmutableMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");
        ListMultimap<Integer, String> actual = LinkedListMultimap.create(expected);
        actual.put(4, "nine");
        actual.put(5, "eight");

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactlyEntriesIn(expected));
        assertFailureKeys(
                failure,
                "unexpected", "---", "expected", "but was");
        assertFailureValue(
                failure,
                "unexpected", "{4=[nine], 5=[eight]}");
    }

    @Test
    void containsExactlyFailureBoth() {
        ImmutableMultimap<Integer, String> expected =
                ImmutableMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");
        ListMultimap<Integer, String> actual = LinkedListMultimap.create(expected);
        actual.remove(3, "six");
        actual.remove(4, "five");
        actual.put(4, "nine");
        actual.put(5, "eight");

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactlyEntriesIn(expected));
        assertFailureKeys(
                failure,
                "missing", "unexpected", "---", "expected", "but was");
        assertFailureValue(
                failure,
                "missing", "{3=[six], 4=[five]}");
        assertFailureValue(
                failure,
                "unexpected", "{4=[nine], 5=[eight]}");
    }

    @Test
    void containsExactlyFailureWithEmptyStringMissing() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(ImmutableMultimap.of())
                        .containsExactly("", "a"));
        assertFailureKeys(
                failure,
                "missing", "---", "expected", "but was");
        assertFailureValue(
                failure,
                "missing", "{\"\" (empty String)=[a]}");
        assertFailureValue(
                failure,
                "expected", "{\"\" (empty String)=[a]}");
        assertFailureValue(
                failure,
                "but was", "{}");
    }

    @Test
    void containsExactlyFailureWithEmptyStringExtra() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(ImmutableMultimap.of("a", "", "", ""))
                        .containsExactly("a", ""));
        assertFailureKeys(
                failure,
                "unexpected", "---", "expected", "but was");
        assertFailureValue(
                failure,
                "unexpected", "{\"\" (empty String)=[\"\" (empty String)]}");
        assertFailureValue(
                failure,
                "expected", "{a=[\"\" (empty String)]}");
        assertFailureValue(
                failure,
                "but was", "{a=[], =[]}");
    }

    @Test
    void containsExactlyFailureWithEmptyStringBoth() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(ImmutableMultimap.of("a", ""))
                        .containsExactly("", "a"));
        assertFailureKeys(
                failure,
                "missing", "unexpected", "---", "expected", "but was");
        assertFailureValue(
                failure,
                "missing", "{\"\" (empty String)=[a]}");
        assertFailureValue(
                failure,
                "unexpected", "{a=[\"\" (empty String)]}");
        assertFailureValue(
                failure,
                "expected", "{\"\" (empty String)=[a]}");
        assertFailureValue(
                failure,
                "but was", "{a=[]}");
    }

    @Test
    void containsExactlyInOrder() {
        ImmutableMultimap<Integer, String> actual =
                ImmutableMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");
        ImmutableMultimap<Integer, String> expected =
                ImmutableMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");

        assertThat(actual).containsExactlyEntriesIn(expected).inOrder();
    }

    @Test
    void containsExactlyInOrderDifferentTypes() {
        ImmutableListMultimap<Integer, String> listMultimap =
                ImmutableListMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");
        ImmutableSetMultimap<Integer, String> setMultimap = ImmutableSetMultimap.copyOf(listMultimap);

        assertThat(listMultimap).containsExactlyEntriesIn(setMultimap).inOrder();
    }

    @Test
    void containsExactlyInOrderFailure() {
        ImmutableMultimap<Integer, String> actual =
                ImmutableMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");
        ImmutableMultimap<Integer, String> expected =
                ImmutableMultimap.of(4, "four", 3, "six", 4, "five", 3, "two", 3, "one");

        assertThat(actual).containsExactlyEntriesIn(expected);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactlyEntriesIn(expected).inOrder());
        assertFailureKeys(
                failure,
                "contents match, but order was wrong",
                "keys are not in order",
                "keys with out-of-order values",
                "---",
                "expected",
                "but was");
        assertFailureValue(
                failure,
                "keys with out-of-order values", "[4, 3]");
    }

    @Test
    void containsExactlyInOrderFailureValuesOnly() {
        ImmutableMultimap<Integer, String> actual =
                ImmutableMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");
        ImmutableMultimap<Integer, String> expected =
                ImmutableMultimap.of(3, "six", 3, "two", 3, "one", 4, "five", 4, "four");

        assertThat(actual).containsExactlyEntriesIn(expected);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactlyEntriesIn(expected)
                        .inOrder());
        assertFailureKeys(
                failure,
                "contents match, but order was wrong",
                "keys with out-of-order values",
                "---",
                "expected",
                "but was");
        assertFailureValue(
                failure,
                "keys with out-of-order values", "[3]");
    }

    @Test
    void containsExactlyVararg() {
        ImmutableListMultimap<Integer, String> listMultimap =
                ImmutableListMultimap.of(1, "one", 3, "six", 3, "two");

        assertThat(listMultimap).containsExactly(1, "one", 3, "six", 3, "two");
    }

    @Test
    void containsExactlyVarargWithNull() {
        Multimap<Integer, String> listMultimap =
                LinkedListMultimap.create(ImmutableListMultimap.of(1, "one", 3, "six", 3, "two"));
        listMultimap.put(4, null);

        assertThat(listMultimap).containsExactly(1, "one", 3, "six", 3, "two", 4, null);
    }

    @Test
    void containsExactlyVarargFailureMissing() {
        ImmutableMultimap<Integer, String> expected =
                ImmutableMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");
        ListMultimap<Integer, String> actual = LinkedListMultimap.create(expected);
        actual.remove(3, "six");
        actual.remove(4, "five");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .isEqualTo(expected));
        assertFailureKeys(
                failure,
                "missing", "---", "expected", "but was");
        assertFailureValue(
                failure,
                "missing", "{3=[six], 4=[five]}");
        assertFailureValue(
                failure,
                "expected", "{3=[one, six, two], 4=[five, four]}");
        assertFailureValue(
                failure,
                "but was", "{3=[one, two], 4=[four]}");
    }

    @Test
    void containsExactlyVarargFailureExtra() {
        ImmutableMultimap<Integer, String> expected =
                ImmutableMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");
        ListMultimap<Integer, String> actual = LinkedListMultimap.create(expected);
        actual.put(4, "nine");
        actual.put(5, "eight");

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactly(3, "one", 3, "six", 3, "two", 4, "five", 4, "four"));
        assertFailureKeys(
                failure,
                "unexpected", "---", "expected", "but was");
        assertFailureValue(
                failure,
                "unexpected", "{4=[nine], 5=[eight]}");
    }

    @Test
    void containsExactlyVarargFailureBoth() {
        ImmutableMultimap<Integer, String> expected =
                ImmutableMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");
        ListMultimap<Integer, String> actual = LinkedListMultimap.create(expected);
        actual.remove(3, "six");
        actual.remove(4, "five");
        actual.put(4, "nine");
        actual.put(5, "eight");

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactly(3, "one", 3, "six", 3, "two", 4, "five", 4, "four"));
        assertFailureKeys(
                failure,
                "missing", "unexpected", "---", "expected", "but was");
        assertFailureValue(
                failure,
                "missing", "{3=[six], 4=[five]}");
        assertFailureValue(
                failure,
                "unexpected", "{4=[nine], 5=[eight]}");
    }

    @Test
    void containsExactlyVarargRespectsDuplicates() {
        ImmutableListMultimap<Integer, String> actual =
                ImmutableListMultimap.of(3, "one", 3, "two", 3, "one", 4, "five", 4, "five");

        assertThat(actual).containsExactly(3, "two", 4, "five", 3, "one", 4, "five", 3, "one");
    }

    @Test
    void containsExactlyVarargRespectsDuplicatesFailure() {
        ImmutableListMultimap<Integer, String> actual =
                ImmutableListMultimap.of(3, "one", 3, "two", 3, "one", 4, "five", 4, "five");

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactly(3, "one", 3, "two", 4, "five"));
        assertFailureKeys(
                failure,
                "unexpected", "---", "expected", "but was");
        assertFailureValue(
                failure,
                "unexpected", "{3=[one], 4=[five]}");
    }

    @Test
    void containsExactlyVarargInOrder() {
        ImmutableMultimap<Integer, String> actual =
                ImmutableMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");

        assertThat(actual)
                .containsExactly(3, "one", 3, "six", 3, "two", 4, "five", 4, "four")
                .inOrder();
    }

    @Test
    void containsExactlyVarargInOrderFailure() {
        ImmutableMultimap<Integer, String> actual =
                ImmutableMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");

        assertThat(actual).containsExactly(4, "four", 3, "six", 4, "five", 3, "two", 3, "one");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactly(4, "four", 3, "six", 4, "five", 3, "two", 3, "one")
                        .inOrder());
        assertFailureKeys(
                failure,
                "contents match, but order was wrong",
                "keys are not in order",
                "keys with out-of-order values",
                "---",
                "expected",
                "but was");
        assertFailureValue(
                failure,
                "keys with out-of-order values", "[4, 3]");
    }

    @Test
    void containsExactlyVarargInOrderFailureValuesOnly() {
        ImmutableMultimap<Integer, String> actual =
                ImmutableMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");

        assertThat(actual).containsExactly(3, "six", 3, "two", 3, "one", 4, "five", 4, "four");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactly(3, "six", 3, "two", 3, "one", 4, "five", 4, "four")
                        .inOrder());
        assertFailureKeys(
                failure,
                "contents match, but order was wrong",
                "keys with out-of-order values",
                "---",
                "expected",
                "but was");
        assertFailureValue(
                failure,
                "keys with out-of-order values", "[3]");
    }

    @Test
    void containsExactlyEntriesIn_homogeneousMultimap_failsWithSameToString() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(ImmutableMultimap.of(1, "a", 1, "b", 2, "c"))
                        .containsExactlyEntriesIn(ImmutableMultimap.of(1L, "a", 1L, "b", 2L, "c")));
        assertFailureKeys(
                failure,
                "missing", "unexpected", "---", "expected", "but was");
        assertFailureValue(
                failure,
                "missing", "[1=a, 1=b, 2=c] (Map.Entry<java.lang.Long, java.lang.String>)");
        assertFailureValue(
                failure,
                "unexpected", "[1=a, 1=b, 2=c] (Map.Entry<java.lang.Integer, java.lang.String>)");
    }

    @Test
    void containsExactlyEntriesIn_heterogeneousMultimap_failsWithSameToString() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(ImmutableMultimap.of(1, "a", 1, "b", 2L, "c"))
                        .containsExactlyEntriesIn(ImmutableMultimap.of(1L, "a", 1L, "b", 2, "c")));
        assertFailureKeys(
                failure,
                "missing", "unexpected", "---", "expected", "but was");
        assertFailureValue(
                failure,
                "missing",
                "[1=a (Map.Entry<java.lang.Long, java.lang.String>), "
                        + "1=b (Map.Entry<java.lang.Long, java.lang.String>), "
                        + "2=c (Map.Entry<java.lang.Integer, java.lang.String>)]");
        assertFailureValue(
                failure,
                "unexpected",
                "[1=a (Map.Entry<java.lang.Integer, java.lang.String>), "
                        + "1=b (Map.Entry<java.lang.Integer, java.lang.String>), "
                        + "2=c (Map.Entry<java.lang.Long, java.lang.String>)]");
    }

    @Test
    void containsAtLeastEntriesIn() {
        ImmutableListMultimap<Integer, String> actual =
                ImmutableListMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");
        ImmutableSetMultimap<Integer, String> expected =
                ImmutableSetMultimap.of(3, "one", 3, "six", 3, "two", 4, "five");

        assertThat(actual).containsAtLeastEntriesIn(expected);
    }

    @Test
    void containsAtLeastEmpty() {
        ImmutableListMultimap<Integer, String> actual = ImmutableListMultimap.of(3, "one");
        ImmutableSetMultimap<Integer, String> expected = ImmutableSetMultimap.of();

        assertThat(actual).containsAtLeastEntriesIn(expected);
        assertThat(actual).containsAtLeastEntriesIn(expected).inOrder();
    }

    @Test
    void containsAtLeastRejectsNull() {
        ImmutableMultimap<Integer, String> multimap =
                ImmutableMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");

        try {
            assertThat(multimap).containsAtLeastEntriesIn(null);
            fail("Should have thrown.");
        } catch (NullPointerException expected) {
        }
    }

    @Test
    void containsAtLeastRespectsDuplicates() {
        ImmutableListMultimap<Integer, String> actual =
                ImmutableListMultimap.of(3, "one", 3, "two", 3, "one", 4, "five", 4, "five");
        ImmutableListMultimap<Integer, String> expected =
                ImmutableListMultimap.of(3, "two", 4, "five", 3, "one", 4, "five", 3, "one");

        assertThat(actual).containsAtLeastEntriesIn(expected);
    }

    @Test
    void containsAtLeastRespectsDuplicatesFailure() {
        ImmutableListMultimap<Integer, String> expected =
                ImmutableListMultimap.of(3, "one", 3, "two", 3, "one", 4, "five", 4, "five");
        ImmutableSetMultimap<Integer, String> actual = ImmutableSetMultimap.copyOf(expected);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsAtLeastEntriesIn(expected));
        assertFailureKeys(
                failure,
                "missing", "---", "expected to contain at least", "but was");
        assertFailureValue(
                failure,
                "missing", "{3=[one], 4=[five]}");
        assertFailureValue(
                failure,
                "expected to contain at least", "{3=[one, two, one], 4=[five, five]}");
        assertFailureValue(
                failure,
                "but was", "{3=[one, two], 4=[five]}");
    }

    @Test
    void containsAtLeastFailureMissing() {
        ImmutableMultimap<Integer, String> expected =
                ImmutableMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");
        ListMultimap<Integer, String> actual = LinkedListMultimap.create(expected);
        actual.remove(3, "six");
        actual.remove(4, "five");
        actual.put(50, "hawaii");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsAtLeastEntriesIn(expected));
        assertFailureKeys(
                failure,
                "missing", "---", "expected to contain at least", "but was");
        assertFailureValue(
                failure,
                "missing", "{3=[six], 4=[five]}");
    }

    @Test
    void containsAtLeastFailureWithEmptyStringMissing() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(ImmutableMultimap.of("key", "value"))
                        .containsAtLeast("", "a"));
        assertFailureKeys(
                failure,
                "missing", "---", "expected to contain at least", "but was");
        assertFailureValue(
                failure,
                "missing", "{\"\" (empty String)=[a]}");
    }

    @Test
    void containsAtLeastInOrder() {
        ImmutableMultimap<Integer, String> actual =
                ImmutableMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");
        ImmutableMultimap<Integer, String> expected =
                ImmutableMultimap.of(3, "one", 3, "six", 4, "five", 4, "four");

        assertThat(actual).containsAtLeastEntriesIn(expected).inOrder();
    }

    @Test
    void containsAtLeastInOrderDifferentTypes() {
        ImmutableListMultimap<Integer, String> actual =
                ImmutableListMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");
        ImmutableSetMultimap<Integer, String> expected =
                ImmutableSetMultimap.of(3, "one", 3, "six", 4, "five", 4, "four");

        assertThat(actual).containsAtLeastEntriesIn(expected).inOrder();
    }

    @Test
    void containsAtLeastInOrderFailure() {
        ImmutableMultimap<Integer, String> actual =
                ImmutableMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");
        ImmutableMultimap<Integer, String> expected =
                ImmutableMultimap.of(4, "four", 3, "six", 3, "two", 3, "one");

        assertThat(actual).containsAtLeastEntriesIn(expected);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsAtLeastEntriesIn(expected)
                        .inOrder());
        assertFailureKeys(
                failure,
                "contents match, but order was wrong",
                "keys are not in order",
                "keys with out-of-order values",
                "---",
                "expected to contain at least",
                "but was");
        assertFailureValue(
                failure,
                "keys with out-of-order values", "[3]");
        assertFailureValue(
                failure,
                "expected to contain at least", "{4=[four], 3=[six, two, one]}");
        assertFailureValue(
                failure,
                "but was", "{3=[one, six, two], 4=[five, four]}");
    }

    @Test
    void containsAtLeastInOrderFailureValuesOnly() {
        ImmutableMultimap<Integer, String> actual =
                ImmutableMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");
        ImmutableMultimap<Integer, String> expected =
                ImmutableMultimap.of(3, "six", 3, "one", 4, "five", 4, "four");
        assertThat(actual).containsAtLeastEntriesIn(expected);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsAtLeastEntriesIn(expected)
                        .inOrder());
        assertFailureKeys(
                failure,
                "contents match, but order was wrong",
                "keys with out-of-order values",
                "---",
                "expected to contain at least",
                "but was");
        assertFailureValue(
                failure,
                "keys with out-of-order values", "[3]");
    }

    @Test
    void containsAtLeastVararg() {
        ImmutableListMultimap<Integer, String> listMultimap =
                ImmutableListMultimap.of(1, "one", 3, "six", 3, "two", 3, "one");

        assertThat(listMultimap).containsAtLeast(1, "one", 3, "six", 3, "two");
    }

    @Test
    void containsAtLeastVarargWithNull() {
        Multimap<Integer, String> listMultimap =
                LinkedListMultimap.create(ImmutableListMultimap.of(1, "one", 3, "six", 3, "two"));
        listMultimap.put(4, null);

        assertThat(listMultimap).containsAtLeast(1, "one", 3, "two", 4, null);
    }

    @Test
    void containsAtLeastVarargFailureMissing() {
        ImmutableMultimap<Integer, String> expected =
                ImmutableMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");
        ListMultimap<Integer, String> actual = LinkedListMultimap.create(expected);
        actual.remove(3, "six");
        actual.remove(4, "five");
        actual.put(3, "nine");

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsAtLeast(3, "one", 3, "six", 3, "two", 4, "five", 4, "four"));
        assertFailureKeys(
                failure,
                "missing", "---", "expected to contain at least", "but was");
        assertFailureValue(
                failure,
                "missing", "{3=[six], 4=[five]}");
    }

    @Test
    void containsAtLeastVarargRespectsDuplicates() {
        ImmutableListMultimap<Integer, String> actual =
                ImmutableListMultimap.of(3, "one", 3, "two", 3, "one", 4, "five", 4, "five");

        assertThat(actual).containsAtLeast(3, "two", 4, "five", 3, "one", 3, "one");
    }

    @Test
    void containsAtLeastVarargRespectsDuplicatesFailure() {
        ImmutableListMultimap<Integer, String> actual =
                ImmutableListMultimap.of(3, "one", 3, "two", 4, "five", 4, "five");

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsAtLeast(3, "one", 3, "one", 3, "one", 4, "five"));
        assertFailureKeys(
                failure,
                "missing", "---", "expected to contain at least", "but was");
        assertFailureValue(
                failure,
                "missing", "{3=[one [2 copies]]}");
    }

    @Test
    void containsAtLeastVarargInOrder() {
        ImmutableMultimap<Integer, String> actual =
                ImmutableMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");

        assertThat(actual).containsAtLeast(3, "one", 3, "six", 4, "five", 4, "four").inOrder();
    }

    @Test
    void containsAtLeastVarargInOrderFailure() {
        ImmutableMultimap<Integer, String> actual =
                ImmutableMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");

        assertThat(actual).containsAtLeast(4, "four", 3, "six", 3, "two", 3, "one");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsAtLeast(4, "four", 3, "six", 3, "two", 3, "one")
                        .inOrder());
        assertFailureKeys(
                failure,
                "contents match, but order was wrong",
                "keys are not in order",
                "keys with out-of-order values",
                "---",
                "expected to contain at least",
                "but was");
        assertFailureValue(
                failure,
                "keys with out-of-order values", "[3]");
        assertFailureValue(
                failure,
                "expected to contain at least", "{4=[four], 3=[six, two, one]}");
        assertFailureValue(
                failure,
                "but was", "{3=[one, six, two], 4=[five, four]}");
    }

    @Test
    void containsAtLeastVarargInOrderFailureValuesOnly() {
        ImmutableMultimap<Integer, String> actual =
                ImmutableMultimap.of(3, "one", 3, "six", 3, "two", 4, "five", 4, "four");

        assertThat(actual).containsAtLeast(3, "two", 3, "one", 4, "five", 4, "four");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsAtLeast(3, "two", 3, "one", 4, "five", 4, "four")
                        .inOrder());
        assertFailureKeys(
                failure,
                "contents match, but order was wrong",
                "keys with out-of-order values",
                "---",
                "expected to contain at least",
                "but was");
        assertFailureValue(
                failure,
                "keys with out-of-order values", "[3]");
    }

    @Test
    void comparingValuesUsing_containsEntry_success() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("abc", "+123", "def", "+456", "def", "+789");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsEntry("def", 789);
    }

    @Test
    void comparingValuesUsing_containsEntry_failsExpectedKeyHasWrongValues() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("abc", "+123", "def", "+456", "def", "+789");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsEntry("def", 123));
        assertFailureKeys(
                failure,
                "expected to contain entry",
                "testing whether",
                "but did not",
                "though it did contain values for that key",
                "full contents");
        assertFailureValue(
                failure,
                "expected to contain entry", "def=123");
        assertFailureValue(
                failure,
                "testing whether", "actual value parses to expected value");
        assertFailureValue(
                failure,
                "though it did contain values for that key", "[+456, +789]");
        assertFailureValue(
                failure,
                "full contents", "{abc=[+123], def=[+456, +789]}");
    }

    @Test
    void comparingValuesUsing_containsEntry_failsWrongKeyHasExpectedValue() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("abc", "+123", "def", "+456", "def", "+789");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsEntry("xyz", 789));
        assertFailureKeys(
                failure,
                "expected to contain entry",
                "testing whether",
                "but did not",
                "though it did contain entries with matching values",
                "full contents");
        assertFailureValue(
                failure,
                "though it did contain entries with matching values", "[def=+789]");
    }

    @Test
    void comparingValuesUsing_containsEntry_failsMissingExpectedKeyAndValue() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("abc", "+123", "def", "+456", "def", "+789");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsEntry("xyz", 321));
        assertFailureKeys(
                failure,
                "expected to contain entry", "testing whether", "but did not", "full contents");
    }

    @Test
    void comparingValuesUsing_containsEntry_handlesException_expectedKeyHasWrongValues() {
        ListMultimap<Integer, String> actual = LinkedListMultimap.create();
        actual.put(1, "one");
        actual.put(2, "two");
        actual.put(2, "deux");
        actual.put(2, null);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(CASE_INSENSITIVE_EQUALITY)
                        .containsEntry(2, "ZWEI"));
        // The test fails because the expected key doesn't have a match for the expected value. We are
        // bound also to hit a NPE from compare(null, ZWEI) along the way, and should also report that.
        assertFailureKeys(
                failure,
                "expected to contain entry",
                "testing whether",
                "but did not",
                "though it did contain values for that key",
                "full contents",
                "additionally, one or more exceptions were thrown while comparing values",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(null, ZWEI) threw java.lang.NullPointerException");
    }

    @Test
    void comparingValuesUsing_containsEntry_handlesException_wrongKeyHasExpectedValue() {
        ListMultimap<Integer, String> actual = LinkedListMultimap.create();
        actual.put(1, "one");
        actual.put(3, "two");
        actual.put(3, null);
        actual.put(3, "zwei");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(CASE_INSENSITIVE_EQUALITY)
                        .containsEntry(2, "ZWEI"));
        // The test fails and does not contain the expected key, but does contain the expected value
        // we the wrong key. We are bound also to hit a NPE from compare(null, ZWEI) along the way, and
        // should also report that.
        assertFailureKeys(
                failure,
                "expected to contain entry",
                "testing whether",
                "but did not",
                "though it did contain entries with matching values",
                "full contents",
                "additionally, one or more exceptions were thrown while comparing values",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(null, ZWEI) threw java.lang.NullPointerException");
    }

    @Test
    void comparingValuesUsing_containsEntry_handlesException_alwaysFails() {
        ListMultimap<Integer, String> actual = LinkedListMultimap.create();
        actual.put(1, "one");
        actual.put(2, "two");
        actual.put(2, null);
        actual.put(2, "zwei");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(CASE_INSENSITIVE_EQUALITY)
                        .containsEntry(2, "ZWEI"));
        // The multimap does contain the expected entry, but no reasonable implementation could find
        // it without hitting the NPE from compare(null, ZWEI) first, so we are contractually required
        // to fail.
        assertFailureKeys(
                failure,
                "one or more exceptions were thrown while comparing values",
                "first exception",
                "expected to contain entry",
                "testing whether",
                "found match (but failing because of exception)",
                "full contents");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(null, ZWEI) threw java.lang.NullPointerException");
        assertFailureValue(
                failure,
                "found match (but failing because of exception)", "2=zwei");
    }

    @Test
    void comparingValuesUsing_containsEntry_wrongTypeInActual() {
        ImmutableListMultimap<String, Object> actual =
                ImmutableListMultimap.of("abc", "+123", "def", "+456", "def", 789);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsEntry("def", 789));
        assertFailureKeys(
                failure,
                "expected to contain entry",
                "testing whether",
                "but did not",
                "though it did contain values for that key",
                "full contents",
                "additionally, one or more exceptions were thrown while comparing values",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(789, 789) threw java.lang.ClassCastException");
    }

    @Test
    void comparingValuesUsing_doesNotContainEntry_successExcludeKeyHasWrongValues() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("abc", "+123", "def", "+456", "def", "+789");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .doesNotContainEntry("def", 123);
    }

    @Test
    void comparingValuesUsing_doesNotContainEntry_successWrongKeyHasExcludedValue() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("abc", "+123", "def", "+456", "def", "+789");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .doesNotContainEntry("xyz", 789);
    }

    @Test
    void comparingValuesUsing_doesNotContainEntry_successMissingExcludedKeyAndValue() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("abc", "+123", "def", "+456", "def", "+789");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .doesNotContainEntry("xyz", 321);
    }

    @Test
    void comparingValuesUsing_doesNotContainEntry_failure() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("abc", "+123", "def", "+456", "def", "+789");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .doesNotContainEntry("def", 789));
        assertFailureKeys(
                failure,
                "expected not to contain entry",
                "testing whether",
                "but contained that key with matching values",
                "full contents");
        assertFailureValue(
                failure,
                "expected not to contain entry", "def=789");
        assertFailureValue(
                failure,
                "testing whether", "actual value parses to expected value");
        assertFailureValue(
                failure,
                "but contained that key with matching values", "[+789]");
        assertFailureValue(
                failure,
                "full contents", "{abc=[+123], def=[+456, +789]}");
    }

    @Test
    void comparingValuesUsing_doesNotContainEntry_handlesException_didContainEntry() {
        ListMultimap<Integer, String> actual = LinkedListMultimap.create();
        actual.put(1, "one");
        actual.put(2, "two");
        actual.put(2, null);
        actual.put(2, "zwei");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(CASE_INSENSITIVE_EQUALITY)
                        .doesNotContainEntry(2, "ZWEI"));
        // The test fails because it does contain the expected entry. We are bound to also hit the NPE
        // from compare(null, ZWEI) along the way, and should also report that.
        assertFailureKeys(
                failure,
                "expected not to contain entry",
                "testing whether",
                "but contained that key with matching values",
                "full contents",
                "additionally, one or more exceptions were thrown while comparing values",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(null, ZWEI) threw java.lang.NullPointerException");
    }

    @Test
    void comparingValuesUsing_doesNotContainEntry_handlesException_didNotContainEntry() {
        ListMultimap<Integer, String> actual = LinkedListMultimap.create();
        actual.put(1, "one");
        actual.put(2, "two");
        actual.put(2, "deux");
        actual.put(2, null);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(CASE_INSENSITIVE_EQUALITY)
                        .doesNotContainEntry(2, "ZWEI"));
        // The test would pass if compare(null, ZWEI) returned false. But it actually throws NPE, and
        // we are bound to hit that, so we are contractually required to fail.
        assertFailureKeys(
                failure,
                "one or more exceptions were thrown while comparing values",
                "first exception",
                "expected not to contain entry",
                "testing whether",
                "found no match (but failing because of exception)",
                "full contents");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(null, ZWEI) threw java.lang.NullPointerException");
    }

    @Test
    void comparingValuesUsing_doesNotContainEntry_wrongTypeInActual() {
        ImmutableListMultimap<String, Object> actual =
                ImmutableListMultimap.of("abc", "+123", "def", "+456", "def", 789);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .doesNotContainEntry("def", 789));
        assertFailureKeys(
                failure,
                "one or more exceptions were thrown while comparing values",
                "first exception",
                "expected not to contain entry",
                "testing whether",
                "found no match (but failing because of exception)",
                "full contents");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(789, 789) threw java.lang.ClassCastException");
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_success() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("abc", "+123", "def", "+64", "def", "0x40", "def", "+128");
        ImmutableListMultimap<String, Integer> expected =
                ImmutableListMultimap.of("def", 64, "def", 128, "def", 64, "abc", 123);
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactlyEntriesIn(expected);
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_missingKey() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("def", "+64", "def", "0x40", "def", "+128");
        ImmutableListMultimap<String, Integer> expected =
                ImmutableListMultimap.of("def", 64, "def", 128, "def", 64, "abc", 123);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyEntriesIn(expected));
        assertFailureKeys(
                failure,
                "missing (1)", "---", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "missing (1)", "abc=123");
        // TODO(b/69154276): Address the fact that we show "expected" as a list of entries and "but was"
        // as a multimap, which looks a bit odd.
        assertFailureValue(
                failure,
                "expected", "[def=64, def=128, def=64, abc=123]");
        assertFailureValue(
                failure,
                "testing whether",
                "actual element has a key that is equal to and a value that parses to the key and value of"
                        + " expected element");
        assertFailureValue(
                failure,
                "but was", "{def=[+64, 0x40, +128]}");
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_extraKey() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("abc", "+123", "def", "+64", "def", "0x40", "def", "+128");
        ImmutableListMultimap<String, Integer> expected =
                ImmutableListMultimap.of("def", 64, "def", 128, "def", 64);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyEntriesIn(expected));
        assertFailureKeys(
                failure,
                "unexpected (1)", "---", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "unexpected (1)", "abc=+123");
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_wrongValueForKey() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("abc", "+123", "def", "+64", "def", "0x40", "def", "+128");
        ImmutableListMultimap<String, Integer> expected =
                ImmutableListMultimap.of("def", 64, "def", 128, "def", 128, "abc", 123);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyEntriesIn(expected));
        assertFailureKeys(
                failure,
                "in an assertion requiring a 1:1 mapping between the expected and the actual elements,"
                        + " each actual element matches as least one expected element, and vice versa, but"
                        + " there was no 1:1 mapping",
                "using the most complete 1:1 mapping (or one such mapping, if there is a tie)",
                "missing (1)",
                "unexpected (1)",
                "---",
                "expected",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "missing (1)", "def=128");
        assertThatFailure(failure).factValue("unexpected (1)").isAnyOf("[def=+64]", "[def=0x40]");
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_handlesException() {
        ListMultimap<Integer, String> actual = LinkedListMultimap.create();
        actual.put(1, "one");
        actual.put(2, null);
        actual.put(2, "deux");
        actual.put(2, "zwei");
        ImmutableListMultimap<Integer, String> expected =
                ImmutableListMultimap.of(1, "ONE", 2, "TWO", 2, "DEUX", 2, "ZWEI");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(CASE_INSENSITIVE_EQUALITY)
                        .containsExactlyEntriesIn(expected));
        assertFailureKeys(
                failure,
                "missing (1)",
                "unexpected (1)",
                "---",
                "expected",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing elements",
                "first exception");
        assertFailureValue(
                failure,
                "missing (1)", "2=TWO");
        assertFailureValue(
                failure,
                "unexpected (1)", "[2=null]");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(2=null, 2=TWO) threw java.lang.NullPointerException");
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_handlesException_alwaysFails() {
        ListMultimap<Integer, String> actual = LinkedListMultimap.create();
        actual.put(1, "one");
        actual.put(2, null);
        actual.put(2, "two");
        actual.put(2, "deux");
        ListMultimap<Integer, String> expected = LinkedListMultimap.create();
        expected.put(1, "ONE");
        expected.put(2, "TWO");
        expected.put(2, "DEUX");
        expected.put(2, null);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(CASE_INSENSITIVE_EQUALITY_HALF_NULL_SAFE)
                        .containsExactlyEntriesIn(expected));
        // CASE_INSENSITIVE_EQUALITY_HALF_NULL_SAFE.compare(null, null) returns true, so there is a
        // mapping between actual and expected entries where they all correspond. However, no
        // reasonable implementation would find that mapping without hitting the (null, "TWO") case
        // along the way, and that throws NPE, so we are contractually required to fail.
        assertFailureKeys(
                failure,
                "one or more exceptions were thrown while comparing elements",
                "first exception",
                "expected",
                "testing whether",
                "found all expected elements (but failing because of exception)",
                "full contents");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(2=null, 2=TWO) threw java.lang.NullPointerException");
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_wrongTypeInActual() {
        ImmutableListMultimap<String, Object> actual =
                ImmutableListMultimap.<String, Object>of(
                        "abc", "+123", "def", "+64", "def", "0x40", "def", 999);
        ImmutableListMultimap<String, Integer> expected =
                ImmutableListMultimap.of("def", 64, "def", 123, "def", 64, "abc", 123);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyEntriesIn(expected));
        assertFailureKeys(
                failure,
                "missing (1)",
                "unexpected (1)",
                "---",
                "expected",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing elements",
                "first exception");
        assertFailureValue(
                failure,
                "missing (1)", "def=123");
        assertFailureValue(
                failure,
                "unexpected (1)", "[def=999]");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(def=999, def=64) threw java.lang.ClassCastException");
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_inOrder_success() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("abc", "+123", "def", "+64", "def", "0x40", "def", "+128");
        ImmutableListMultimap<String, Integer> expected =
                ImmutableListMultimap.of("abc", 123, "def", 64, "def", 64, "def", 128);
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactlyEntriesIn(expected);
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_inOrder_wrongKeyOrder() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("abc", "+123", "def", "+64", "def", "0x40", "def", "+128");
        ImmutableListMultimap<String, Integer> expected =
                ImmutableListMultimap.of("def", 64, "def", 64, "def", 128, "abc", 123);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyEntriesIn(expected)
                        .inOrder());
        assertFailureKeys(
                failure,
                "contents match, but order was wrong", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "expected", "[def=64, def=64, def=128, abc=123]");
    }

    @Test
    void comparingValuesUsing_containsExactlyEntriesIn_inOrder_wrongValueOrder() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("abc", "+123", "def", "+64", "def", "0x40", "def", "+128");
        ImmutableListMultimap<String, Integer> expected =
                ImmutableListMultimap.of("abc", 123, "def", 64, "def", 128, "def", 64);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyEntriesIn(expected)
                        .inOrder());
        assertFailureKeys(
                failure,
                "contents match, but order was wrong", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "expected", "[abc=123, def=64, def=128, def=64]");
    }

    @Test
    void comparingValuesUsing_containsExactlyNoArgs() {
        ImmutableListMultimap<String, String> actual = ImmutableListMultimap.of();

        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactly();
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactly()
                .inOrder();
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(ImmutableListMultimap.of("abc", "+123"))
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactly());
        assertFailureKeys(
                failure,
                "expected to be empty", "but was");
    }

    @Test
    void comparingValuesUsing_containsExactly_success() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("abc", "+123", "def", "+64", "def", "0x40", "def", "+128");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactly("def", 64, "def", 128, "def", 64, "abc", 123);
    }

    @Test
    void comparingValuesUsing_containsExactly_missingKey() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("def", "+64", "def", "0x40", "def", "+128");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactly("def", 64, "def", 128, "def", 64, "abc", 123));
        assertFailureKeys(
                failure,
                "missing (1)", "---", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "missing (1)", "abc=123");
    }

    @Test
    void comparingValuesUsing_containsExactly_extraKey() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("abc", "+123", "def", "+64", "def", "0x40", "def", "+128");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactly("def", 64, "def", 128, "def", 64));
        assertFailureKeys(
                failure,
                "unexpected (1)", "---", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "unexpected (1)", "abc=+123");
    }

    @Test
    void comparingValuesUsing_containsExactly_wrongValueForKey() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("abc", "+123", "def", "+64", "def", "0x40", "def", "+128");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactly("def", 64, "def", 128, "def", 128, "abc", 123));
        assertFailureKeys(
                failure,
                "in an assertion requiring a 1:1 mapping between the expected and the actual elements,"
                        + " each actual element matches as least one expected element, and vice versa, but"
                        + " there was no 1:1 mapping",
                "using the most complete 1:1 mapping (or one such mapping, if there is a tie)",
                "missing (1)",
                "unexpected (1)",
                "---",
                "expected",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "missing (1)", "def=128");
        assertThatFailure(failure).factValue("unexpected (1)").isAnyOf("[def=+64]", "[def=0x40]");
    }

    @Test
    void comparingValuesUsing_containsExactly_wrongTypeInActual() {
        ImmutableListMultimap<String, Object> actual =
                ImmutableListMultimap.<String, Object>of(
                        "abc", "+123", "def", "+64", "def", "0x40", "def", 999);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactly("def", 64, "def", 123, "def", 64, "abc", 123));
        assertFailureKeys(
                failure,
                "missing (1)",
                "unexpected (1)",
                "---",
                "expected",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing elements",
                "first exception");
        assertFailureValue(
                failure,
                "missing (1)", "def=123");
        assertFailureValue(
                failure,
                "unexpected (1)", "[def=999]");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(def=999, def=64) threw java.lang.ClassCastException");
    }

    @Test
    void comparingValuesUsing_containsExactly_nullKey() {
        ListMultimap<String, String> actual = ArrayListMultimap.create();
        actual.put(null, "+123");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactly(null, 123);
    }

    @Test
    void comparingValuesUsing_containsExactly_inOrder_success() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("abc", "+123", "def", "+64", "def", "0x40", "def", "+128");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactly("abc", 123, "def", 64, "def", 64, "def", 128);
    }

    @Test
    void comparingValuesUsing_containsExactly_inOrder_wrongKeyOrder() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("abc", "+123", "def", "+64", "def", "0x40", "def", "+128");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactly("def", 64, "def", 64, "def", 128, "abc", 123)
                        .inOrder());
        assertFailureKeys(
                failure,
                "contents match, but order was wrong", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "expected", "[def=64, def=64, def=128, abc=123]");
    }

    @Test
    void comparingValuesUsing_containsExactly_inOrder_wrongValueOrder() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("abc", "+123", "def", "+64", "def", "0x40", "def", "+128");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactly("abc", 123, "def", 64, "def", 128, "def", 64)
                        .inOrder());
        assertFailureKeys(
                failure,
                "contents match, but order was wrong", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "expected", "[abc=123, def=64, def=128, def=64]");
    }

    @Test
    void comparingValuesUsing_containsAtLeastEntriesIn_success() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("abc", "+123", "def", "+64", "def", "0x40", "def", "+128");
        ImmutableListMultimap<String, Integer> expected =
                ImmutableListMultimap.of("def", 64, "def", 128, "abc", 123);
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeastEntriesIn(expected);
    }

    @Test
    void comparingValuesUsing_containsAtLeastEntriesIn_missingKey() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("def", "+64", "def", "0x40", "def", "+128", "abc", "+99");
        ImmutableListMultimap<String, Integer> expected =
                ImmutableListMultimap.of("def", 64, "def", 128, "def", 64, "abc", 123);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeastEntriesIn(expected));
        assertFailureKeys(
                failure,
                "missing (1)", "---", "expected to contain at least", "testing whether", "but was");
        assertFailureValue(
                failure,
                "missing (1)", "abc=123");
    }

    @Test
    void comparingValuesUsing_containsAtLeastEntriesIn_wrongValueForKey() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("abc", "+123", "def", "+64", "def", "0x40", "def", "+128");
        ImmutableListMultimap<String, Integer> expected =
                ImmutableListMultimap.of("def", 64, "def", 128, "def", 128, "abc", 123);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeastEntriesIn(expected));
        assertFailureKeys(
                failure,
                "in an assertion requiring a 1:1 mapping between the expected and a subset of the actual"
                        + " elements, each actual element matches as least one expected element, and vice"
                        + " versa, but there was no 1:1 mapping",
                "using the most complete 1:1 mapping (or one such mapping, if there is a tie)",
                "missing (1)",
                "---",
                "expected to contain at least",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "missing (1)", "def=128");
    }

    @Test
    void comparingValuesUsing_containsAtLeastEntriesIn_handlesException() {
        ListMultimap<Integer, String> actual = LinkedListMultimap.create();
        actual.put(1, "one");
        actual.put(2, null);
        actual.put(2, "deux");
        actual.put(2, "zwei");
        ImmutableListMultimap<Integer, String> expected =
                ImmutableListMultimap.of(1, "ONE", 2, "TWO", 2, "DEUX");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(CASE_INSENSITIVE_EQUALITY)
                        .containsAtLeastEntriesIn(expected));
        assertFailureKeys(
                failure,
                "missing (1)",
                "---",
                "expected to contain at least",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing elements",
                "first exception");
        assertFailureValue(
                failure,
                "missing (1)", "2=TWO");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(2=null, 2=TWO) threw java.lang.NullPointerException");
    }

    @Test
    void comparingValuesUsing_containsAtLeastEntriesIn_handlesException_alwaysFails() {
        ListMultimap<Integer, String> actual = LinkedListMultimap.create();
        actual.put(1, "one");
        actual.put(2, null);
        actual.put(2, "two");
        actual.put(2, "deux");
        ListMultimap<Integer, String> expected = LinkedListMultimap.create();
        expected.put(1, "ONE");
        expected.put(2, "TWO");
        expected.put(2, "DEUX");
        expected.put(2, null);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(CASE_INSENSITIVE_EQUALITY_HALF_NULL_SAFE)
                        .containsAtLeastEntriesIn(expected));
        // CASE_INSENSITIVE_EQUALITY_HALF_NULL_SAFE.compare(null, null) returns true, so there is a
        // mapping between actual and expected entries where they all correspond. However, no
        // reasonable implementation would find that mapping without hitting the (null, "TWO") case
        // along the way, and that throws NPE, so we are contractually required to fail.
        assertFailureKeys(
                failure,
                "one or more exceptions were thrown while comparing elements",
                "first exception",
                "expected to contain at least",
                "testing whether",
                "found all expected elements (but failing because of exception)",
                "full contents");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(2=null, 2=TWO) threw java.lang.NullPointerException");
        assertFailureValue(
                failure,
                "expected to contain at least", "[1=ONE, 2=TWO, 2=DEUX, 2=null]");
    }

    @Test
    void comparingValuesUsing_containsAtLeastEntriesIn_wrongTypeInActual() {
        ImmutableListMultimap<String, Object> actual =
                ImmutableListMultimap.<String, Object>of(
                        "abc", "+123", "def", "+64", "def", "0x40", "def", 999);
        ImmutableListMultimap<String, Integer> expected =
                ImmutableListMultimap.of("def", 64, "def", 123, "def", 64, "abc", 123);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeastEntriesIn(expected));
        assertFailureKeys(
                failure,
                "missing (1)",
                "---",
                "expected to contain at least",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing elements",
                "first exception");
        assertFailureValue(
                failure,
                "missing (1)", "def=123");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(def=999, def=64) threw java.lang.ClassCastException");
    }

    @Test
    void comparingValuesUsing_containsAtLeastEntriesIn_inOrder_success() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of(
                        "def", "+64", "abc", "+123", "def", "0x40", "m", "+1", "def", "+128");
        ImmutableListMultimap<String, Integer> expected =
                ImmutableListMultimap.of("def", 64, "def", 64, "def", 128, "abc", 123);
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeastEntriesIn(expected);
    }

    @Test
    void comparingValuesUsing_containsAtLeastEntriesIn_inOrder_wrongKeyOrder() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of(
                        "abc", "+123", "def", "+64", "m", "+1", "def", "0x40", "def", "+128");
        ImmutableListMultimap<String, Integer> expected =
                ImmutableListMultimap.of("def", 64, "def", 64, "def", 128, "abc", 123);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeastEntriesIn(expected)
                        .inOrder());
        assertFailureKeys(
                failure,
                "required elements were all found, but order was wrong",
                "expected order for required elements",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "expected order for required elements", "[def=64, def=64, def=128, abc=123]");
    }

    @Test
    void comparingValuesUsing_containsAtLeastEntriesIn_inOrder_wrongValueOrder() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of(
                        "abc", "+123", "def", "+64", "m", "+1", "def", "0x40", "def", "+128");
        ImmutableListMultimap<String, Integer> expected =
                ImmutableListMultimap.of("abc", 123, "def", 64, "def", 128, "def", 64);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeastEntriesIn(expected)
                        .inOrder());
        assertFailureKeys(
                failure,
                "required elements were all found, but order was wrong",
                "expected order for required elements",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "expected order for required elements", "[abc=123, def=64, def=128, def=64]");
    }

    @Test
    void comparingValuesUsing_containsAtLeast_success() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of(
                        "abc", "+123", "def", "+64", "m", "+1", "def", "0x40", "def", "+128");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeast("def", 64, "def", 128, "def", 64, "abc", 123);
    }

    @Test
    void comparingValuesUsing_containsAtLeast_missingKey() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of("def", "+64", "def", "0x40", "m", "+1", "def", "+128");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeast("def", 64, "def", 128, "def", 64, "abc", 123));
        assertFailureKeys(
                failure,
                "missing (1)", "---", "expected to contain at least", "testing whether", "but was");
        assertFailureValue(
                failure,
                "missing (1)", "abc=123");
    }

    @Test
    void comparingValuesUsing_containsAtLeast_wrongValueForKey() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of(
                        "abc", "+123", "def", "+64", "m", "+1", "def", "0x40", "def", "+128");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeast("def", 64, "def", 128, "def", 128, "abc", 123));
        assertFailureKeys(
                failure,
                "in an assertion requiring a 1:1 mapping between the expected and a subset of the actual"
                        + " elements, each actual element matches as least one expected element, and vice"
                        + " versa, but there was no 1:1 mapping",
                "using the most complete 1:1 mapping (or one such mapping, if there is a tie)",
                "missing (1)",
                "---",
                "expected to contain at least",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "missing (1)", "def=128");
    }

    @Test
    void comparingValuesUsing_containsAtLeast_wrongTypeInActual() {
        ImmutableListMultimap<String, Object> actual =
                ImmutableListMultimap.of(
                        "abc", "+123", "def", "+64", "def", "0x40", "def", 999, "m", "+1");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeast("def", 64, "def", 123, "def", 64, "abc", 123));
        assertFailureKeys(
                failure,
                "missing (1)",
                "---",
                "expected to contain at least",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing elements",
                "first exception");
        assertFailureValue(
                failure,
                "missing (1)", "def=123");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(def=999, def=64) threw java.lang.ClassCastException");
    }

    @Test
    void comparingValuesUsing_containsAtLeast_inOrder_success() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of(
                        "abc", "+123", "def", "+64", "m", "+1", "def", "0x40", "def", "+128");
        assertThat(actual)
                .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeast("abc", 123, "def", 64, "def", 64, "def", 128);
    }

    @Test
    void comparingValuesUsing_containsAtLeast_inOrder_wrongKeyOrder() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of(
                        "abc", "+123", "def", "+64", "def", "0x40", "m", "+1", "def", "+128");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeast("def", 64, "def", 64, "def", 128, "abc", 123)
                        .inOrder());
        assertFailureKeys(
                failure,
                "required elements were all found, but order was wrong",
                "expected order for required elements",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "expected order for required elements", "[def=64, def=64, def=128, abc=123]");
    }

    @Test
    void comparingValuesUsing_containsAtLeast_inOrder_wrongValueOrder() {
        ImmutableListMultimap<String, String> actual =
                ImmutableListMultimap.of(
                        "abc", "+123", "m", "+1", "def", "+64", "def", "0x40", "def", "+128");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeast("abc", 123, "def", 64, "def", 128, "def", 64)
                        .inOrder());
        assertFailureKeys(
                failure,
                "required elements were all found, but order was wrong",
                "expected order for required elements",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "expected order for required elements", "[abc=123, def=64, def=128, def=64]");
    }
}
