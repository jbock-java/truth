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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for {@link IterableSubject} APIs, excluding those that use {@link Correspondence} (which
 * are tested in {@link IterableSubjectCorrespondenceTest}.
 *
 * @author David Saff
 * @author Christian Gruber (cgruber@israfil.net)
 */
class IterableSubjectTest extends BaseSubjectTestCase {

    @Test
    void hasSize() {
        assertThat(ImmutableList.of(1, 2, 3)).hasSize(3);
    }

    @Test
    void hasSizeZero() {
        assertThat(ImmutableList.of()).hasSize(0);
    }

    @Test
    void hasSizeFails() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(ImmutableList.of(1, 2, 3))
                        .hasSize(4));
        assertFailureValue(
                failure,
                "value of", "iterable.size()");
    }

    @Test
    void hasSizeNegative() {
        try {
            assertThat(ImmutableList.of(1, 2, 3)).hasSize(-1);
            fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    void iterableContains() {
        assertThat(asList(1, 2, 3)).contains(1);
    }

    @Test
    void iterableContainsWithNull() {
        assertThat(asList(1, null, 3)).contains(null);
    }

    @Test
    void iterableContainsFailsWithSameToString() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1L, 2L, 3L, 2L))
                        .contains(2));
        assertFailureKeys(
                failure,
                "expected to contain",
                "an instance of",
                "but did not",
                "though it did contain",
                "full contents");
        assertFailureValue(
                failure,
                "expected to contain", "2");
        assertFailureValue(
                failure,
                "an instance of", "java.lang.Integer");
        assertFailureValue(
                failure,
                "though it did contain", "[2 [2 copies]] (java.lang.Long)");
        assertFailureValue(
                failure,
                "full contents", "[1, 2, 3, 2]");
    }

    @Test
    void iterableContainsFailsWithSameToStringAndNull() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, "null"))
                        .contains(null));
        assertFailureValue(
                failure,
                "an instance of", "null type");
    }

    @Test
    void iterableContainsFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 3))
                        .contains(5));
        assertFailureKeys(
                failure,
                "expected to contain", "but was");
        assertFailureValue(
                failure,
                "expected to contain", "5");
    }

    @Test
    void iterableDoesNotContain() {
        assertThat(asList(1, null, 3)).doesNotContain(5);
    }

    @Test
    void iterableDoesNotContainNull() {
        assertThat(asList(1, 2, 3)).doesNotContain(null);
    }

    @Test
    void iterableDoesNotContainFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 3))
                        .doesNotContain(2));
        assertFailureKeys(
                failure,
                "expected not to contain", "but was");
        assertFailureValue(
                failure,
                "expected not to contain", "2");
    }

    @Test
    void doesNotContainDuplicates() {
        assertThat(asList(1, 2, 3)).containsNoDuplicates();
    }

    @Test
    void doesNotContainDuplicatesMixedTypes() {
        assertThat(asList(1, 2, 2L, 3)).containsNoDuplicates();
    }

    @Test
    void doesNotContainDuplicatesFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 2, 3))
                        .containsNoDuplicates());
        assertFailureKeys(
                failure,
                "expected not to contain duplicates", "but contained", "full contents");
        assertFailureValue(
                failure,
                "but contained", "[2 x 2]");
        assertFailureValue(
                failure,
                "full contents", "[1, 2, 2, 3]");
    }

    @Test
    void iterableContainsAnyOf() {
        assertThat(asList(1, 2, 3)).containsAnyOf(1, 5);
    }

    @Test
    void iterableContainsAnyOfWithNull() {
        assertThat(asList(1, null, 3)).containsAnyOf(null, 5);
    }

    @Test
    void iterableContainsAnyOfWithNullInThirdAndFinalPosition() {
        assertThat(asList(1, null, 3)).containsAnyOf(4, 5, (Integer) null);
    }

    @Test
    void iterableContainsAnyOfFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 3))
                        .containsAnyOf(5, 6, 0));
        assertFailureKeys(
                failure,
                "expected to contain any of", "but was");
        assertFailureValue(
                failure,
                "expected to contain any of", "[5, 6, 0]");
    }

    @Test
    void iterableContainsAnyOfFailsWithSameToStringAndHomogeneousList() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1L, 2L, 3L))
                        .containsAnyOf(2, 3));
        assertFailureKeys(
                failure,
                "expected to contain any of", "but did not", "though it did contain", "full contents");
        assertFailureValue(
                failure,
                "expected to contain any of", "[2, 3] (java.lang.Integer)");
        assertFailureValue(
                failure,
                "though it did contain", "[2, 3] (java.lang.Long)");
        assertFailureValue(
                failure,
                "full contents", "[1, 2, 3]");
    }

    @Test
    void iterableContainsAnyOfFailsWithSameToStringAndHomogeneousListWithDuplicates() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(3L, 3L))
                        .containsAnyOf(2, 3, 3));
        assertFailureKeys(
                failure,
                "expected to contain any of", "but did not", "though it did contain", "full contents");
        assertFailureValue(
                failure,
                "expected to contain any of", "[2, 3 [2 copies]] (java.lang.Integer)");
        assertFailureValue(
                failure,
                "though it did contain", "[3 [2 copies]] (java.lang.Long)");
        assertFailureValue(
                failure,
                "full contents", "[3, 3]");
    }

    @Test
    void iterableContainsAnyOfFailsWithSameToStringAndNullInSubject() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(null, "abc"))
                        .containsAnyOf("def", "null"));
        assertFailureKeys(
                failure,
                "expected to contain any of", "but did not", "though it did contain", "full contents");
        assertFailureValue(
                failure,
                "expected to contain any of", "[def, null] (java.lang.String)");
        assertFailureValue(
                failure,
                "though it did contain", "[null (null type)]");
        assertFailureValue(
                failure,
                "full contents", "[null, abc]");
    }

    @Test
    void iterableContainsAnyOfFailsWithSameToStringAndNullInExpectation() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList("null", "abc"))
                        .containsAnyOf("def", null));
        assertFailureKeys(
                failure,
                "expected to contain any of", "but did not", "though it did contain", "full contents");
        assertFailureValue(
                failure,
                "expected to contain any of", "[def (java.lang.String), null (null type)]");
        assertFailureValue(
                failure,
                "though it did contain", "[null] (java.lang.String)");
        assertFailureValue(
                failure,
                "full contents", "[null, abc]");
    }

    @Test
    void iterableContainsAnyOfWithOneShotIterable() {
        Iterator<Object> iterator = asList((Object) 2, 1, "b").iterator();
        Iterable<Object> iterable = () -> iterator;
        assertThat(iterable).containsAnyOf(3, "a", 7, "b", 0);
    }

    @Test
    void iterableContainsAnyInIterable() {
        assertThat(asList(1, 2, 3)).containsAnyIn(asList(1, 10, 100));

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 3))
                        .containsAnyIn(asList(5, 6, 0)));
        assertFailureKeys(
                failure,
                "expected to contain any of", "but was");
        assertFailureValue(
                failure,
                "expected to contain any of", "[5, 6, 0]");
    }

    @Test
    void iterableContainsAnyInArray() {
        assertThat(asList(1, 2, 3)).containsAnyIn(new Integer[]{1, 10, 100});

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 3))
                        .containsAnyIn(new Integer[]{5, 6, 0}));
        assertFailureKeys(
                failure,
                "expected to contain any of", "but was");
        assertFailureValue(
                failure,
                "expected to contain any of", "[5, 6, 0]");
    }

    @Test
    void iterableContainsAtLeast() {
        assertThat(asList(1, 2, 3)).containsAtLeast(1, 2);
    }

    @Test
    void iterableContainsAtLeastWithMany() {
        assertThat(asList(1, 2, 3)).containsAtLeast(1, 2);
    }

    @Test
    void iterableContainsAtLeastWithDuplicates() {
        assertThat(asList(1, 2, 2, 2, 3)).containsAtLeast(2, 2);
    }

    @Test
    void iterableContainsAtLeastWithNull() {
        assertThat(asList(1, null, 3)).containsAtLeast(3, (Integer) null);
    }

    @Test
    void iterableContainsAtLeastWithNullAtThirdAndFinalPosition() {
        assertThat(asList(1, null, 3)).containsAtLeast(1, 3, (Object) null);
    }

    /*
     * Test that we only call toString() if the assertion fails -- that is, not just if the elements
     * are out of order, but only if someone actually calls inOrder(). There are 2 reasons for this:
     *
     * 1. Calling toString() uses extra time and space. (To be fair, Iterable assertions often use a
     * lot of those already.)
     *
     * 2. Some toString() methods are buggy. Arguably we shouldn't accommodate these, especially since
     * those users are in for a nasty surprise if their tests actually fail someday, but I don't want
     * to bite that off now. (Maybe Fact should catch exceptions from toString()?)
     */
    @Test
    void iterableContainsAtLeastElementsInOutOfOrderDoesNotStringify() {
        CountsToStringCalls o = new CountsToStringCalls();
        List<Object> actual = asList(o, 1);
        List<Object> expected = asList(1, o);
        assertThat(actual).containsAtLeastElementsIn(expected);
        assertThat(o.calls).isEqualTo(0);
        assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsAtLeastElementsIn(expected)
                        .inOrder());
        assertThat(o.calls).isGreaterThan(0);
    }

    @Test
    void iterableContainsAtLeastFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 3))
                        .containsAtLeast(1, 2, 4));
        assertFailureKeys(
                failure,
                "missing (1)", "---", "expected to contain at least", "but was");
        assertFailureValue(
                failure,
                "missing (1)", "4");
        assertFailureValue(
                failure,
                "expected to contain at least", "[1, 2, 4]");
    }

    @Test
    void iterableContainsAtLeastWithExtras() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList("y", "x"))
                        .containsAtLeast("x", "y", "z"));
        assertFailureValue(
                failure,
                "missing (1)", "z");
    }

    @Test
    void iterableContainsAtLeastWithExtraCopiesOfOutOfOrder() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList("y", "x"))
                        .containsAtLeast("x", "y", "y"));
        assertFailureValue(
                failure,
                "missing (1)", "y");
    }

    @Test
    void iterableContainsAtLeastWithDuplicatesFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 3))
                        .containsAtLeast(1, 2, 2, 2, 3, 4));
        assertFailureValue(
                failure,
                "missing (3)", "2 [2 copies], 4");
    }

    /*
     * Slightly subtle test to ensure that if multiple equal elements are found
     * to be missing we only reference it once in the output message.
     */
    @Test
    void iterableContainsAtLeastWithDuplicateMissingElements() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2))
                        .containsAtLeast(4, 4, 4));
        assertFailureValue(
                failure,
                "missing (3)", "4 [3 copies]");
    }

    @Test
    void iterableContainsAtLeastWithNullFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, null, 3))
                        .containsAtLeast(1, null, null, 3));
        assertFailureValue(
                failure,
                "missing (1)", "null");
    }

    @Test
    void iterableContainsAtLeastFailsWithSameToStringAndHomogeneousList() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1L, 2L))
                        .containsAtLeast(1, 2));
        assertFailureValue(
                failure,
                "missing (2)", "1, 2 (java.lang.Integer)");
        assertFailureValue(
                failure,
                "though it did contain (2)", "1, 2 (java.lang.Long)");
    }

    @Test
    void iterableContainsAtLeastFailsWithSameToStringAndHomogeneousListWithDuplicates() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1L, 2L, 2L))
                        .containsAtLeast(1, 1, 2));
        assertFailureValue(
                failure,
                "missing (3)", "1 [2 copies], 2 (java.lang.Integer)");
        assertFailureValue(
                failure,
                "though it did contain (3)", "1, 2 [2 copies] (java.lang.Long)");
    }

    @Test
    void iterableContainsAtLeastFailsWithSameToStringAndHomogeneousListWithNull() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList("null", "abc"))
                        .containsAtLeast("abc", null));
        assertFailureValue(
                failure,
                "missing (1)", "null (null type)");
        assertFailureValue(
                failure,
                "though it did contain (1)", "null (java.lang.String)");
    }

    @Test
    void iterableContainsAtLeastFailsWithSameToStringAndHeterogeneousListWithDuplicates() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 2L, 3L, 3L))
                        .containsAtLeast(2L, 2L, 3, 3));
        assertFailureValue(
                failure,
                "missing (3)", "2 (java.lang.Long), 3 (java.lang.Integer) [2 copies]");
        assertFailureValue(
                failure,
                "though it did contain (3)", "2 (java.lang.Integer), 3 (java.lang.Long) [2 copies]");
    }

    @Test
    void iterableContainsAtLeastFailsWithEmptyString() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList("a", null))
                        .containsAtLeast("", null));
        assertFailureKeys(
                failure,
                "missing (1)", "---", "expected to contain at least", "but was");
        assertFailureValue(
                failure,
                "missing (1)", "");
    }

    @Test
    void iterableContainsAtLeastInOrder() {
        assertThat(asList(3, 2, 5)).containsAtLeast(3, 2, 5).inOrder();
    }

    @Test
    void iterableContainsAtLeastInOrderWithGaps() {
        assertThat(asList(3, 2, 5)).containsAtLeast(3, 5).inOrder();
        assertThat(asList(3, 2, 2, 4, 5)).containsAtLeast(3, 2, 2, 5).inOrder();
        assertThat(asList(3, 1, 4, 1, 5)).containsAtLeast(3, 1, 5).inOrder();
        assertThat(asList("x", "y", "y", "z")).containsAtLeast("x", "y", "z").inOrder();
        assertThat(asList("x", "x", "y", "z")).containsAtLeast("x", "y", "z").inOrder();
        assertThat(asList("z", "x", "y", "z")).containsAtLeast("x", "y", "z").inOrder();
        assertThat(asList("x", "x", "y", "z", "x")).containsAtLeast("x", "y", "z", "x").inOrder();
    }

    @Test
    void iterableContainsAtLeastInOrderWithNull() {
        assertThat(asList(3, null, 5)).containsAtLeast(3, null, 5).inOrder();
        assertThat(asList(3, null, 7, 5)).containsAtLeast(3, null, 5).inOrder();
    }

    @Test
    void iterableContainsAtLeastInOrderWithFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, null, 3))
                        .containsAtLeast(null, 1, 3)
                        .inOrder());
        assertFailureKeys(
                failure,
                "required elements were all found, but order was wrong",
                "expected order for required elements",
                "but was");
        assertFailureValue(
                failure,
                "expected order for required elements", "[null, 1, 3]");
    }

    @Test
    void iterableContainsAtLeastInOrderWithOneShotIterable() {
        final Iterable<Object> iterable = Arrays.<Object>asList(2, 1, null, 4, "a", 3, "b");
        final Iterator<Object> iterator = iterable.iterator();
        Iterable<Object> oneShot =
                new Iterable<Object>() {
                    @Override
                    public Iterator<Object> iterator() {
                        return iterator;
                    }

                    @Override
                    public String toString() {
                        return Iterables.toString(iterable);
                    }
                };

        assertThat(oneShot).containsAtLeast(1, null, 3).inOrder();
    }

    @Test
    void iterableContainsAtLeastInOrderWithOneShotIterableWrongOrder() {
        Iterator<Object> iterator = asList((Object) 2, 1, null, 4, "a", 3, "b").iterator();
        Iterable<Object> iterable =
                new Iterable<>() {
                    @Override
                    public Iterator<Object> iterator() {
                        return iterator;
                    }

                    @Override
                    public String toString() {
                        return "BadIterable";
                    }
                };
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(iterable)
                        .containsAtLeast(1, 3, (Object) null).inOrder());
        assertFailureKeys(
                failure,
                "required elements were all found, but order was wrong",
                "expected order for required elements",
                "but was");
        assertFailureValue(
                failure,
                "expected order for required elements", "[1, 3, null]");
    }

    @Test
    void iterableContainsAtLeastInOrderWrongOrderAndMissing() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2))
                        .containsAtLeast(2, 1, 3).inOrder());
    }

    @Test
    void iterableContainsAtLeastElementsInIterable() {
        assertThat(asList(1, 2, 3)).containsAtLeastElementsIn(asList(1, 2));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 3))
                        .containsAtLeastElementsIn(asList(1, 2, 4)));
        assertFailureKeys(
                failure,
                "missing (1)", "---", "expected to contain at least", "but was");
        assertFailureValue(
                failure,
                "missing (1)", "4");
        assertFailureValue(
                failure,
                "expected to contain at least", "[1, 2, 4]");
    }

    @Test
    void iterableContainsAtLeastElementsInCanUseFactPerElement() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList("abc"))
                        .containsAtLeastElementsIn(asList("123\n456", "789")));
        assertFailureKeys(
                failure,
                "missing (2)", "#1", "#2", "---", "expected to contain at least", "but was");
        assertFailureValue(
                failure,
                "#1", "123\n456");
        assertFailureValue(
                failure,
                "#2", "789");
    }

    @Test
    void iterableContainsAtLeastElementsInArray() {
        assertThat(asList(1, 2, 3)).containsAtLeastElementsIn(new Integer[]{1, 2});

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 3))
                        .containsAtLeastElementsIn(new Integer[]{1, 2, 4}));
        assertFailureKeys(
                failure,
                "missing (1)", "---", "expected to contain at least", "but was");
        assertFailureValue(
                failure,
                "missing (1)", "4");
        assertFailureValue(
                failure,
                "expected to contain at least", "[1, 2, 4]");
    }

    @Test
    void iterableContainsNoneOf() {
        assertThat(asList(1, 2, 3)).containsNoneOf(4, 5, 6);
    }

    @Test
    void iterableContainsNoneOfFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 3))
                        .containsNoneOf(1, 2, 4));
        assertFailureKeys(
                failure,
                "expected not to contain any of", "but contained", "full contents");
        assertFailureValue(
                failure,
                "expected not to contain any of", "[1, 2, 4]");
        assertFailureValue(
                failure,
                "but contained", "[1, 2]");
        assertFailureValue(
                failure,
                "full contents", "[1, 2, 3]");
    }

    @Test
    void iterableContainsNoneOfFailureWithDuplicateInSubject() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 2, 3))
                        .containsNoneOf(1, 2, 4));
        assertFailureValue(
                failure,
                "but contained", "[1, 2]");
    }

    @Test
    void iterableContainsNoneOfFailureWithDuplicateInExpected() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 3))
                        .containsNoneOf(1, 2, 2, 4));
        assertFailureValue(
                failure,
                "but contained", "[1, 2]");
    }

    @Test
    void iterableContainsNoneOfFailureWithEmptyString() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(""))
                        .containsNoneOf("", null));
        assertFailureKeys(
                failure,
                "expected not to contain any of", "but contained", "full contents");
        assertFailureValue(
                failure,
                "expected not to contain any of", "[\"\" (empty String), null]");
        assertFailureValue(
                failure,
                "but contained", "[\"\" (empty String)]");
        assertFailureValue(
                failure,
                "full contents", "[]");
    }

    @Test
    void iterableContainsNoneInIterable() {
        assertThat(asList(1, 2, 3)).containsNoneIn(asList(4, 5, 6));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 3))
                        .containsNoneIn(asList(1, 2, 4)));
        assertFailureKeys(
                failure,
                "expected not to contain any of", "but contained", "full contents");
        assertFailureValue(
                failure,
                "expected not to contain any of", "[1, 2, 4]");
        assertFailureValue(
                failure,
                "but contained", "[1, 2]");
        assertFailureValue(
                failure,
                "full contents", "[1, 2, 3]");
    }

    @Test
    void iterableContainsNoneInArray() {
        assertThat(asList(1, 2, 3)).containsNoneIn(new Integer[]{4, 5, 6});
        assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 3))
                        .containsNoneIn(new Integer[]{1, 2, 4}));
    }

    @Test
    void iterableContainsExactlyArray() {
        String[] stringArray = {"a", "b"};
        ImmutableList<String[]> iterable = ImmutableList.of(stringArray);
        // This test fails w/o the explicit cast
        assertThat(iterable).containsExactly((Object) stringArray);
    }

    @Test
    void arrayContainsExactly() {
        ImmutableList<String> iterable = ImmutableList.of("a", "b");
        String[] array = {"a", "b"};
        assertThat(iterable).containsExactly((Object[]) array);
    }

    @Test
    void iterableContainsExactlyWithMany() {
        assertThat(asList(1, 2, 3)).containsExactly(1, 2, 3);
    }

    @Test
    void iterableContainsExactlyOutOfOrder() {
        assertThat(asList(1, 2, 3, 4)).containsExactly(3, 1, 4, 2);
    }

    @Test
    void iterableContainsExactlyWithDuplicates() {
        assertThat(asList(1, 2, 2, 2, 3)).containsExactly(1, 2, 2, 2, 3);
    }

    @Test
    void iterableContainsExactlyWithDuplicatesOutOfOrder() {
        assertThat(asList(1, 2, 2, 2, 3)).containsExactly(2, 1, 2, 3, 2);
    }

    @Test
    void iterableContainsExactlyWithOnlyNullPassedAsNullArray() {
        // Truth is tolerant of this erroneous varargs call.
        Iterable<Object> actual = asList((Object) null);
        assertThat(actual).containsExactly((Object[]) null);
    }

    @Test
    void iterableContainsExactlyWithOnlyNull() {
        Iterable<Object> actual = asList((Object) null);
        assertThat(actual).containsExactly((Object) null);
    }

    @Test
    void iterableContainsExactlyWithNullSecond() {
        assertThat(asList(1, null)).containsExactly(1, null);
    }

    @Test
    void iterableContainsExactlyWithNullThird() {
        assertThat(asList(1, 2, null)).containsExactly(1, 2, null);
    }

    @Test
    void iterableContainsExactlyWithNull() {
        assertThat(asList(1, null, 3)).containsExactly(1, null, 3);
    }

    @Test
    void iterableContainsExactlyWithNullOutOfOrder() {
        assertThat(asList(1, null, 3)).containsExactly(1, 3, (Integer) null);
    }

    @Test
    void iterableContainsExactlyOutOfOrderDoesNotStringify() {
        CountsToStringCalls o = new CountsToStringCalls();
        List<Object> actual = asList(o, 1);
        List<Object> expected = asList(1, o);
        assertThat(actual).containsExactlyElementsIn(expected);
        assertThat(o.calls).isEqualTo(0);
        assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .containsExactlyElementsIn(expected)
                        .inOrder());
        assertThat(o.calls).isGreaterThan(0);
    }

    @Test
    void iterableContainsExactlyWithEmptyString() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList())
                        .containsExactly(""));

        assertFailureValue(
                failure,
                "missing (1)", "");
    }

    @Test
    void iterableContainsExactlyWithEmptyStringAndUnexpectedItem() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList("a", null))
                        .containsExactly(""));

        assertFailureKeys(
                failure,
                "missing (1)", "unexpected (2)", "---", "expected", "but was");
        assertFailureValue(
                failure,
                "missing (1)", "");
        assertFailureValue(
                failure,
                "unexpected (2)", "a, null");
    }

    @Test
    void iterableContainsExactlyWithEmptyStringAndMissingItem() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(""))
                        .containsExactly("a", null));

        assertFailureValue(
                failure,
                "missing (2)", "a, null");
        assertFailureValue(
                failure,
                "unexpected (1)", "");
    }

    @Test
    void iterableContainsExactlyWithEmptyStringAmongMissingItems() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList("a"))
                        .containsExactly("", "b"));

        assertFailureKeys(
                failure,
                "missing (2)", "#1", "#2", "", "unexpected (1)", "#1", "---", "expected", "but was");
        assertFailureValueIndexed(
                failure,
                "#1", 0, "");
        assertFailureValueIndexed(
                failure,
                "#2", 0, "b");
        assertFailureValueIndexed(
                failure,
                "#1", 1, "a");
    }

    @Test
    void iterableContainsExactlySingleElement() {
        assertThat(asList(1)).containsExactly(1);

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1))
                        .containsExactly(2));
        assertFailureKeys(
                failure,
                "value of", "expected", "but was");
        assertFailureValue(
                failure,
                "value of", "iterable.onlyElement()");
    }

    @Test
    void iterableContainsExactlySingleElementNoEqualsMagic() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1))
                        .containsExactly(1L));

        assertFailureValueIndexed(
                failure,
                "an instance of", 0, "java.lang.Long");
    }

    @Test
    void iterableContainsExactlyWithElementsThatThrowWhenYouCallHashCode() {
        HashCodeThrower one = new HashCodeThrower();
        HashCodeThrower two = new HashCodeThrower();

        assertThat(asList(one, two)).containsExactly(two, one);
        assertThat(asList(one, two)).containsExactly(one, two).inOrder();
        assertThat(asList(one, two)).containsExactlyElementsIn(asList(two, one));
        assertThat(asList(one, two)).containsExactlyElementsIn(asList(one, two)).inOrder();

        assertThrows(
                AssertionError.class,
                () -> assertThat(asList(one, two))
                        .containsExactly(one));
    }

    private static class HashCodeThrower {
        @Override
        public boolean equals(Object other) {
            return this == other;
        }

        @Override
        public int hashCode() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return "HCT";
        }
    }

    @Test
    void iterableContainsExactlyElementsInInOrderPassesWithEmptyExpectedAndActual() {
        assertThat(ImmutableList.of()).containsExactlyElementsIn(ImmutableList.of()).inOrder();
    }

    @Test
    void iterableContainsExactlyElementsInWithEmptyExpected() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList("foo"))
                        .containsExactlyElementsIn(ImmutableList.of()));
        assertFailureKeys(
                failure,
                "expected to be empty", "but was");
    }

    @Test
    void iterableContainsExactlyElementsInErrorMessageIsInOrder() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList("foo OR bar"))
                        .containsExactlyElementsIn(asList("foo", "bar")));
        assertFailureValue(
                failure,
                "missing (2)", "foo, bar");
        assertFailureValue(
                failure,
                "unexpected (1)", "foo OR bar");
    }

    @Test
    void iterableContainsExactlyMissingItemFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2))
                        .containsExactly(1, 2, 4));
        assertFailureValue(
                failure,
                "missing (1)", "4");
    }

    @Test
    void iterableContainsExactlyUnexpectedItemFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 3))
                        .containsExactly(1, 2));
        assertFailureValue(
                failure,
                "unexpected (1)", "3");
    }

    @Test
    void iterableContainsExactlyWithDuplicatesNotEnoughItemsFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 3))
                        .containsExactly(1, 2, 2, 2, 3));
        assertFailureValue(
                failure,
                "missing (2)", "2 [2 copies]");
    }

    @Test
    void iterableContainsExactlyWithDuplicatesMissingItemFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 3))
                        .containsExactly(1, 2, 2, 2, 3, 4));
        assertFailureValue(
                failure,
                "missing (3)", "2 [2 copies], 4");
    }

    @Test
    void iterableContainsExactlyWithDuplicatesMissingItemsWithNewlineFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList("a", "b", "foo\nbar"))
                        .containsExactly("a", "b", "foo\nbar", "foo\nbar", "foo\nbar"));
        assertFailureKeys(
                failure,
                "missing (2)", "#1 [2 copies]", "---", "expected", "but was");
        assertFailureValue(
                failure,
                "#1 [2 copies]", "foo\nbar");
    }

    @Test
    void iterableContainsExactlyWithDuplicatesMissingAndExtraItemsWithNewlineFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList("a\nb", "a\nb"))
                        .containsExactly("foo\nbar", "foo\nbar"));
        assertFailureKeys(
                failure,
                "missing (2)",
                "#1 [2 copies]",
                "",
                "unexpected (2)",
                "#1 [2 copies]",
                "---",
                "expected",
                "but was");
        assertFailureValueIndexed(
                failure,
                "#1 [2 copies]", 0, "foo\nbar");
        assertFailureValueIndexed(
                failure,
                "#1 [2 copies]", 1, "a\nb");
    }

    @Test
    void iterableContainsExactlyWithDuplicatesUnexpectedItemFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 2, 2, 2, 3))
                        .containsExactly(1, 2, 2, 3));
        assertFailureValue(
                failure,
                "unexpected (2)", "2 [2 copies]");
    }

    /*
     * Slightly subtle test to ensure that if multiple equal elements are found
     * to be missing we only reference it once in the output message.
     */
    @Test
    void iterableContainsExactlyWithDuplicateMissingElements() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList())
                        .containsExactly(4, 4, 4));
        assertFailureValue(
                failure,
                "missing (3)", "4 [3 copies]");
    }

    @Test
    void iterableContainsExactlyWithNullFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, null, 3))
                        .containsExactly(1, null, null, 3));
        assertFailureValue(
                failure,
                "missing (1)", "null");
    }

    @Test
    void iterableContainsExactlyWithMissingAndExtraElements() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 3))
                        .containsExactly(1, 2, 4));
        assertFailureValue(
                failure,
                "missing (1)", "4");
        assertFailureValue(
                failure,
                "unexpected (1)", "3");
    }

    @Test
    void iterableContainsExactlyWithDuplicateMissingAndExtraElements() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 3, 3))
                        .containsExactly(1, 2, 4, 4));
        assertFailureValue(
                failure,
                "missing (2)", "4 [2 copies]");
        assertFailureValue(
                failure,
                "unexpected (2)", "3 [2 copies]");
    }

    @Test
    void iterableContainsExactlyWithCommaSeparatedVsIndividual() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList("a, b"))
                        .containsExactly("a", "b"));
        assertFailureKeys(
                failure,
                "missing (2)", "#1", "#2", "", "unexpected (1)", "#1", "---", "expected", "but was");
        assertFailureValueIndexed(
                failure,
                "#1", 0, "a");
        assertFailureValueIndexed(
                failure,
                "#2", 0, "b");
        assertFailureValueIndexed(
                failure,
                "#1", 1, "a, b");
    }

    @Test
    void iterableContainsExactlyFailsWithSameToStringAndHomogeneousList() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1L, 2L))
                        .containsExactly(1, 2));
        assertFailureValue(
                failure,
                "missing (2)", "1, 2 (java.lang.Integer)");
        assertFailureValue(
                failure,
                "unexpected (2)", "1, 2 (java.lang.Long)");
    }

    @Test
    void iterableContainsExactlyFailsWithSameToStringAndListWithNull() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1L, 2L))
                        .containsExactly(null, 1, 2));
        assertFailureValue(
                failure,
                "missing (3)", "null (null type), 1 (java.lang.Integer), 2 (java.lang.Integer)");
        assertFailureValue(
                failure,
                "unexpected (2)", "1, 2 (java.lang.Long)");
    }

    @Test
    void iterableContainsExactlyFailsWithSameToStringAndHeterogeneousList() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1L, 2))
                        .containsExactly(1, null, 2L));
        assertFailureValue(
                failure,
                "missing (3)", "1 (java.lang.Integer), null (null type), 2 (java.lang.Long)");
        assertFailureValue(
                failure,
                "unexpected (2)", "1 (java.lang.Long), 2 (java.lang.Integer)");
    }

    @Test
    void iterableContainsExactlyFailsWithSameToStringAndHomogeneousListWithDuplicates() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1L, 2L))
                        .containsExactly(1, 2, 2));
        assertFailureValue(
                failure,
                "missing (3)", "1, 2 [2 copies] (java.lang.Integer)");
        assertFailureValue(
                failure,
                "unexpected (2)", "1, 2 (java.lang.Long)");
    }

    @Test
    void iterableContainsExactlyFailsWithSameToStringAndHeterogeneousListWithDuplicates() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1L, 2))
                        .containsExactly(1, null, null, 2L, 2L));
        assertFailureValue(
                failure,
                "missing (5)",
                "1 (java.lang.Integer), null (null type) [2 copies], 2 (java.lang.Long) [2 copies]");
        assertFailureValue(
                failure,
                "unexpected (2)", "1 (java.lang.Long), 2 (java.lang.Integer)");
    }

    @Test
    void iterableContainsExactlyWithOneIterableGivesWarning() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 3, 4))
                        .containsExactly(asList(1, 2, 3, 4)));
        assertThat(failure)
                .hasMessageThat()
                .contains(CONTAINS_EXACTLY_ITERABLE_WARNING);
    }

    @Test
    void iterableContainsExactlyElementsInWithOneIterableDoesNotGiveWarning() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 3, 4))
                        .containsExactlyElementsIn(asList(1, 2, 3)));
        assertFailureValue(
                failure,
                "unexpected (1)", "4");
    }

    @Test
    void iterableContainsExactlyWithTwoIterableDoesNotGivesWarning() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 3, 4))
                        .containsExactly(asList(1, 2), asList(3, 4)));
        assertThat(failure)
                .hasMessageThat()
                .doesNotContain(CONTAINS_EXACTLY_ITERABLE_WARNING);
    }

    private static final String CONTAINS_EXACTLY_ITERABLE_WARNING =
            "Passing an iterable to the varargs method containsExactly(Object...) is "
                    + "often not the correct thing to do. Did you mean to call "
                    + "containsExactlyElementsIn(Iterable) instead?";

    @Test
    void iterableContainsExactlyWithOneNonIterableDoesNotGiveWarning() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 3, 4))
                        .containsExactly(1));
        assertFailureValue(
                failure,
                "unexpected (3)", "2, 3, 4");
    }

    @Test
    void iterableContainsExactlyInOrder() {
        assertThat(asList(3, 2, 5)).containsExactly(3, 2, 5).inOrder();
    }

    @Test
    void iterableContainsExactlyInOrderWithNull() {
        assertThat(asList(3, null, 5)).containsExactly(3, null, 5).inOrder();
    }

    @Test
    void iterableContainsExactlyInOrderWithFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, null, 3))
                        .containsExactly(null, 1, 3)
                        .inOrder());
        assertFailureKeys(
                failure,
                "contents match, but order was wrong", "expected", "but was");
        assertFailureValue(
                failure,
                "expected", "[null, 1, 3]");
    }

    @Test
    void iterableContainsExactlyInOrderWithOneShotIterable() {
        Iterator<Object> iterator = asList((Object) 1, null, 3).iterator();
        Iterable<Object> iterable = () -> iterator;
        assertThat(iterable).containsExactly(1, null, 3).inOrder();
    }

    @Test
    void iterableContainsExactlyInOrderWithOneShotIterableWrongOrder() {
        Iterator<Object> iterator = asList((Object) 1, null, 3).iterator();
        Iterable<Object> iterable =
                new Iterable<>() {
                    @Override
                    public Iterator<Object> iterator() {
                        return iterator;
                    }

                    @Override
                    public String toString() {
                        return "BadIterable";
                    }
                };
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(iterable)
                        .containsExactly(1, 3, null)
                        .inOrder());
        assertFailureKeys(
                failure,
                "contents match, but order was wrong", "expected", "but was");
        assertFailureValue(
                failure,
                "expected", "[1, 3, null]");
    }

    @Test
    void iterableWithNoToStringOverride() {
        Iterable<Integer> iterable =
                () -> Iterators.forArray(1, 2, 3);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(iterable)
                        .containsExactly(1, 2)
                        .inOrder());
        assertFailureValue(
                failure,
                "but was", "[1, 2, 3]");
    }

    @Test
    void iterableContainsExactlyElementsInIterable() {
        assertThat(asList(1, 2)).containsExactlyElementsIn(asList(1, 2));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2))
                        .containsExactlyElementsIn(asList(1, 2, 4)));
        assertFailureValue(
                failure,
                "missing (1)", "4");
    }

    @Test
    void iterableContainsExactlyElementsInArray() {
        assertThat(asList(1, 2)).containsExactlyElementsIn(new Integer[]{1, 2});
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2))
                        .containsExactlyElementsIn(new Integer[]{1, 2, 4}));
        assertFailureValue(
                failure,
                "missing (1)", "4");
    }

    @Test
    void nullEqualToNull() {
        assertThat((Iterable<?>) null).isEqualTo(null);
    }

    @Test
    void nullEqualToSomething() {
        assertThrows(
                AssertionError.class,
                () -> assertThat((Iterable<?>) null)
                        .isEqualTo(ImmutableList.of()));
    }

    @Test
    void somethingEqualToNull() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(ImmutableList.of())
                        .isEqualTo(null));
    }

    @Test
    void somethingEqualToSomething() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(ImmutableList.of())
                        .isEqualTo(ImmutableList.of("a")));
        // isEqualTo uses the containsExactly style of message:
        assertFailureValue(
                failure,
                "missing (1)", "a");
    }

    @Test
    void isEqualToNotConsistentWithEquals() {
        TreeSet<String> actual = new TreeSet<>(CASE_INSENSITIVE_ORDER);
        TreeSet<String> expected = new TreeSet<>(CASE_INSENSITIVE_ORDER);
        actual.add("one");
        expected.add("ONE");
        /*
         * Our contract doesn't guarantee that the following test will pass. It *currently* does,
         * though, and if we change that behavior, we want this test to let us know.
         */
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void isEqualToNotConsistentWithEquals_failure() {
        TreeSet<String> actual = new TreeSet<>(CASE_INSENSITIVE_ORDER);
        TreeSet<String> expected = new TreeSet<>(CASE_INSENSITIVE_ORDER);
        actual.add("one");
        expected.add("ONE");
        actual.add("two");
        assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .isEqualTo(expected));
        // The exact message generated is unspecified.
    }

    @Test
    void iterableIsEmpty() {
        assertThat(asList()).isEmpty();
    }

    @Test
    void iterableIsEmptyWithFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, null, 3))
                        .isEmpty());
        assertFailureKeys(
                failure,
                "expected to be empty", "but was");
    }

    @Test
    void iterableIsNotEmpty() {
        assertThat(asList("foo")).isNotEmpty();
    }

    @Test
    void iterableIsNotEmptyWithFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList())
                        .isNotEmpty());
        assertFailureKeys(
                failure,
                "expected not to be empty");
    }

    @Test
    void iterableIsInStrictOrder() {
        assertThat(asList()).isInStrictOrder();
        assertThat(asList(1)).isInStrictOrder();
        assertThat(asList(1, 2, 3, 4)).isInStrictOrder();
    }

    @Test
    void isInStrictOrderFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 2, 2, 4))
                        .isInStrictOrder());
        assertFailureKeys(
                failure,
                "expected to be in strict order", "but contained", "followed by", "full contents");
        assertFailureValue(
                failure,
                "but contained", "2");
        assertFailureValue(
                failure,
                "followed by", "2");
        assertFailureValue(
                failure,
                "full contents", "[1, 2, 2, 4]");
    }

    @Test
    void isInStrictOrderWithNonComparableElementsFailure() {
        try {
            assertThat(asList((Object) 1, "2", 3, "4")).isInStrictOrder();
            fail("Should have thrown.");
        } catch (ClassCastException expected) {
        }
    }

    @Test
    void iterableIsInOrder() {
        assertThat(asList()).isInOrder();
        assertThat(asList(1)).isInOrder();
        assertThat(asList(1, 1, 2, 3, 3, 3, 4)).isInOrder();
    }

    @Test
    void isInOrderFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 3, 2, 4))
                        .isInOrder());
        assertFailureKeys(
                failure,
                "expected to be in order", "but contained", "followed by", "full contents");
        assertFailureValue(
                failure,
                "but contained", "3");
        assertFailureValue(
                failure,
                "followed by", "2");
        assertFailureValue(
                failure,
                "full contents", "[1, 3, 2, 4]");
    }

    @Test
    void isInOrderMultipleFailures() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(asList(1, 3, 2, 4, 0))
                        .isInOrder());
    }

    @Test
    void isInOrderWithNonComparableElementsFailure() {
        try {
            assertThat(asList((Object) 1, "2", 2, "3")).isInOrder();
            fail("Should have thrown.");
        } catch (ClassCastException expected) {
        }
    }

    @Test
    void iterableIsInStrictOrderWithComparator() {
        Iterable<String> emptyStrings = asList();
        assertThat(emptyStrings).isInStrictOrder(COMPARE_AS_DECIMAL);
        assertThat(asList("1")).isInStrictOrder(COMPARE_AS_DECIMAL);
        // Note: Use "10" and "20" to distinguish numerical and lexicographical ordering.
        assertThat(asList("1", "2", "10", "20")).isInStrictOrder(COMPARE_AS_DECIMAL);
    }

    @Test
    void iterableIsInStrictOrderWithComparatorFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList("1", "2", "2", "10"))
                        .isInStrictOrder(COMPARE_AS_DECIMAL));
        assertFailureKeys(
                failure,
                "expected to be in strict order", "but contained", "followed by", "full contents");
        assertFailureValue(
                failure,
                "but contained", "2");
        assertFailureValue(
                failure,
                "followed by", "2");
        assertFailureValue(
                failure,
                "full contents", "[1, 2, 2, 10]");
    }

    @Test
    void iterableIsInOrderWithComparator() {
        Iterable<String> emptyStrings = asList();
        assertThat(emptyStrings).isInOrder(COMPARE_AS_DECIMAL);
        assertThat(asList("1")).isInOrder(COMPARE_AS_DECIMAL);
        assertThat(asList("1", "1", "2", "10", "10", "10", "20")).isInOrder(COMPARE_AS_DECIMAL);
    }

    @Test
    void iterableIsInOrderWithComparatorFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList("1", "10", "2", "20"))
                        .isInOrder(COMPARE_AS_DECIMAL));
        assertFailureKeys(
                failure,
                "expected to be in order", "but contained", "followed by", "full contents");
        assertFailureValue(
                failure,
                "but contained", "10");
        assertFailureValue(
                failure,
                "followed by", "2");
        assertFailureValue(
                failure,
                "full contents", "[1, 10, 2, 20]");
    }

    private static final Comparator<String> COMPARE_AS_DECIMAL =
            Comparator.comparing(Integer::valueOf);

    private static class Foo {
        private final int x;

        private Foo(int x) {
            this.x = x;
        }
    }

    private static class Bar extends Foo {
        private Bar(int x) {
            super(x);
        }
    }

    private static final Comparator<Foo> FOO_COMPARATOR =
            Comparator.comparingInt(a -> a.x);

    @Test
    void iterableOrderedByBaseClassComparator() {
        Iterable<Bar> targetList = asList(new Bar(1), new Bar(2), new Bar(3));
        assertThat(targetList).isInOrder(FOO_COMPARATOR);
        assertThat(targetList).isInStrictOrder(FOO_COMPARATOR);
    }

    @Test
    void isIn() {
        ImmutableList<String> actual = ImmutableList.of("a");
        ImmutableList<String> expectedA = ImmutableList.of("a");
        ImmutableList<String> expectedB = ImmutableList.of("b");
        ImmutableList<ImmutableList<String>> expected = ImmutableList.of(expectedA, expectedB);

        assertThat(actual).isIn(expected);
    }

    @Test
    void isNotIn() {
        ImmutableList<String> actual = ImmutableList.of("a");

        assertThat(actual).isNotIn(ImmutableList.of(ImmutableList.of("b"), ImmutableList.of("c")));

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .isNotIn(ImmutableList.of("a", "b")));
        assertThat(failure)
                .hasMessageThat()
                .isEqualTo(
                        "The actual value is an Iterable, and you've written a test that compares it to some "
                                + "objects that are not Iterables. Did you instead mean to check whether its "
                                + "*contents* match any of the *contents* of the given values? If so, call "
                                + "containsNoneOf(...)/containsNoneIn(...) instead. Non-iterables: [a, b]");
    }

    @Test
    void isAnyOf() {
        ImmutableList<String> actual = ImmutableList.of("a");
        ImmutableList<String> expectedA = ImmutableList.of("a");
        ImmutableList<String> expectedB = ImmutableList.of("b");

        assertThat(actual).isAnyOf(expectedA, expectedB);
    }

    @Test
    @SuppressWarnings("IncompatibleArgumentType")
    void isNoneOf() {
        ImmutableList<String> actual = ImmutableList.of("a");

        assertThat(actual).isNoneOf(ImmutableList.of("b"), ImmutableList.of("c"));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .isNoneOf("a", "b"));
        assertThat(failure)
                .hasMessageThat()
                .isEqualTo(
                        "The actual value is an Iterable, and you've written a test that compares it to some "
                                + "objects that are not Iterables. Did you instead mean to check whether its "
                                + "*contents* match any of the *contents* of the given values? If so, call "
                                + "containsNoneOf(...)/containsNoneIn(...) instead. Non-iterables: [a, b]");
    }

    private static final class CountsToStringCalls {
        int calls;

        @Override
        public String toString() {
            calls++;
            return super.toString();
        }
    }
}
