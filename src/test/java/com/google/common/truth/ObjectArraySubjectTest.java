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

import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link com.google.common.truth.ObjectArraySubject}.
 *
 * @author Christian Gruber (cgruber@israfil.net)
 */
class ObjectArraySubjectTest extends BaseSubjectTestCase {
    private static final Object[] EMPTY = new Object[0];

    @Test
    void isEqualTo() {
        assertThat(objectArray("A", 5L)).isEqualTo(objectArray("A", 5L));
    }

    @SuppressWarnings("TruthSelfEquals")
    @Test
    void isEqualTo_Same() {
        Object[] same = objectArray("A", 5L);
        assertThat(same).isEqualTo(same);
    }

    @Test
    void asList() {
        assertThat(objectArray("A", 5L)).asList().contains("A");
    }

    @Test
    void hasLength() {
        assertThat(EMPTY).hasLength(0);
        assertThat(objectArray("A", 5L)).hasLength(2);
        assertThat(new Object[][]{}).hasLength(0);
        assertThat(new Object[][]{{}}).hasLength(1);
    }

    @Test
    void hasLengthFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(objectArray("A", 5L))
                        .hasLength(1));
        assertFailureValue(
                failure,
                "value of", "array.length");
    }

    @Test
    void hasLengthMultiFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(new Object[][]{{"A"}, {5L}})
                        .hasLength(1));
        assertFailureValue(
                failure,
                "value of", "array.length");
    }

    @Test
    void hasLengthNegative() {
        try {
            assertThat(objectArray(2, 5)).hasLength(-1);
            fail("Should have failed");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    void isEmpty() {
        assertThat(EMPTY).isEmpty();
        assertThat(new Object[][]{}).isEmpty();
    }

    @Test
    void isEmptyFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(objectArray("A", 5L))
                        .isEmpty());
        assertFailureKeys(
                failure,
                "expected to be empty", "but was");
    }

    @Test
    void isNotEmpty() {
        assertThat(objectArray("A", 5L)).isNotEmpty();
        assertThat(new Object[][]{{"A"}, {5L}}).isNotEmpty();
    }

    @Test
    void isNotEmptyFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(EMPTY)
                        .isNotEmpty());
        assertFailureKeys(
                failure,
                "expected not to be empty");
    }

    @Test
    void isEqualTo_Fail_UnequalOrdering() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(objectArray("A", 5L))
                        .isEqualTo(objectArray(5L, "A")));
        assertFailureValue(
                failure,
                "differs at index", "[0]");
    }

    @Test
    void isEqualTo_Fail_UnequalOrderingMultiDimensional_00() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(new Object[][]{{"A"}, {5L}})
                        .isEqualTo(new Object[][]{{5L}, {"A"}}));
        assertFailureValue(
                failure,
                "differs at index", "[0][0]");
    }

    @Test
    void isEqualTo_Fail_UnequalOrderingMultiDimensional_01() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(new Object[][]{{"A", "B"}, {5L}})
                        .isEqualTo(new Object[][]{{"A"}, {5L}}));
        assertFailureValue(
                failure,
                "wrong length for index", "[0]");
        assertFailureValueIndexed(
                failure,
                "expected", 1, "1");
        assertFailureValueIndexed(
                failure,
                "but was", 1, "2");
    }

    @Test
    void isEqualTo_Fail_UnequalOrderingMultiDimensional_11() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(new Object[][]{{"A"}, {5L}})
                        .isEqualTo(new Object[][]{{"A"}, {5L, 6L}}));
        assertFailureValue(
                failure,
                "wrong length for index", "[1]");
        assertFailureValueIndexed(
                failure,
                "expected", 1, "2");
        assertFailureValueIndexed(
                failure,
                "but was", 1, "1");
    }

    @Test
    void isEqualTo_Fail_NotAnArray() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(objectArray("A", 5L))
                        .isEqualTo(new Object()));
    }

    @Test
    void isNotEqualTo_SameLengths() {
        assertThat(objectArray("A", 5L)).isNotEqualTo(objectArray("C", 5L));
        assertThat(new Object[][]{{"A"}, {5L}}).isNotEqualTo(new Object[][]{{"C"}, {5L}});
    }

    @Test
    void isNotEqualTo_DifferentLengths() {
        assertThat(objectArray("A", 5L)).isNotEqualTo(objectArray("A", 5L, "c"));
        assertThat(new Object[][]{{"A"}, {5L}}).isNotEqualTo(new Object[][]{{"A", "c"}, {5L}});
        assertThat(new Object[][]{{"A"}, {5L}}).isNotEqualTo(new Object[][]{{"A"}, {5L}, {"C"}});
    }

    @Test
    void isNotEqualTo_DifferentTypes() {
        assertThat(objectArray("A", 5L)).isNotEqualTo(new Object());
    }

    @Test
    void isNotEqualTo_FailEquals() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(objectArray("A", 5L))
                        .isNotEqualTo(objectArray("A", 5L)));
    }

    @Test
    void isNotEqualTo_FailEqualsMultiDimensional() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(new Object[][]{{"A"}, {5L}})
                        .isNotEqualTo(new Object[][]{{"A"}, {5L}}));
        assertFailureValue(
                failure,
                "expected not to be", "[[A], [5]]");
    }

    @SuppressWarnings("TruthSelfEquals")
    @Test
    void isNotEqualTo_FailSame() {
        Object[] same = objectArray("A", 5L);
        assertThrows(
                AssertionError.class,
                () -> assertThat(same)
                        .isNotEqualTo(same));
    }

    @SuppressWarnings("TruthSelfEquals")
    @Test
    void isNotEqualTo_FailSameMultiDimensional() {
        Object[][] same = new Object[][]{{"A"}, {5L}};
        assertThrows(
                AssertionError.class,
                () -> assertThat(same)
                        .isNotEqualTo(same));
    }

    @Test
    void stringArrayIsEqualTo() {
        assertThat(objectArray("A", "B")).isEqualTo(objectArray("A", "B"));
        assertThat(new String[][]{{"A"}, {"B"}}).isEqualTo(new String[][]{{"A"}, {"B"}});
    }

    @Test
    void stringArrayAsList() {
        assertThat(objectArray("A", "B")).asList().contains("A");
    }

    @Test
    void multiDimensionalStringArrayAsList() {
        String[] ab = {"A", "B"};
        assertThat(new String[][]{ab, {"C"}}).asList().contains(ab);
    }

    @Test
    void stringArrayIsEqualTo_Fail_UnequalLength() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(objectArray("A", "B"))
                        .isEqualTo(objectArray("B")));
        assertFailureKeys(
                failure,
                "expected", "but was", "wrong length", "expected", "but was");
        assertFailureValueIndexed(
                failure,
                "expected", 1, "1");
        assertFailureValueIndexed(
                failure,
                "but was", 1, "2");
    }

    @Test
    void stringArrayIsEqualTo_Fail_UnequalLengthMultiDimensional() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(new String[][]{{"A"}, {"B"}})
                        .isEqualTo(new String[][]{{"A"}}));
        assertFailureKeys(
                failure,
                "expected", "but was", "wrong length", "expected", "but was");
        assertFailureValueIndexed(
                failure,
                "expected", 1, "1");
        assertFailureValueIndexed(
                failure,
                "but was", 1, "2");
    }

    @Test
    void stringArrayIsEqualTo_Fail_UnequalOrdering() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(objectArray("A", "B"))
                        .isEqualTo(objectArray("B", "A")));
        assertFailureValue(
                failure,
                "differs at index", "[0]");
    }

    @Test
    void stringArrayIsEqualTo_Fail_UnequalOrderingMultiDimensional() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(new String[][]{{"A"}, {"B"}})
                        .isEqualTo(new String[][]{{"B"}, {"A"}}));
        assertFailureValue(
                failure,
                "differs at index", "[0][0]");
    }

    @Test
    void setArrayIsEqualTo_Fail_UnequalOrdering() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(objectArray(ImmutableSet.of("A"), ImmutableSet.of("B")))
                        .isEqualTo(objectArray(ImmutableSet.of("B"), ImmutableSet.of("A"))));
        assertFailureValue(
                failure,
                "differs at index", "[0]");
        // Maybe one day:
        // .hasMessage("Not true that <(Set<String>[]) [[A], [B]]> is equal to <[[B], [A]]>");
    }

    @Test
    void primitiveMultiDimensionalArrayIsEqualTo() {
        assertThat(new int[][]{{1, 2}, {3}, {4, 5, 6}})
                .isEqualTo(new int[][]{{1, 2}, {3}, {4, 5, 6}});
    }

    @Test
    void primitiveMultiDimensionalArrayIsEqualTo_Fail_UnequalOrdering() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(new int[][]{{1, 2}, {3}, {4, 5, 6}})
                        .isEqualTo(new int[][]{{1, 2}, {3}, {4, 5, 6, 7}}));
        assertFailureValue(
                failure,
                "wrong length for index", "[2]");
        assertFailureValueIndexed(
                failure,
                "expected", 1, "4");
        assertFailureValueIndexed(
                failure,
                "but was", 1, "3");
    }

    @Test
    void primitiveMultiDimensionalArrayIsNotEqualTo() {
        assertThat(new int[][]{{1, 2}, {3}, {4, 5, 6}})
                .isNotEqualTo(new int[][]{{1, 2}, {3}, {4, 5, 6, 7}});
    }

    @Test
    void primitiveMultiDimensionalArrayIsNotEqualTo_Fail_Equal() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(new int[][]{{1, 2}, {3}, {4, 5, 6}})
                        .isNotEqualTo(new int[][]{{1, 2}, {3}, {4, 5, 6}}));
    }

    @Test
    void boxedAndUnboxed() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(new Object[]{new int[]{0}})
                        .isEqualTo(new Object[]{new Integer[]{0}}));
        assertFailureValue(
                failure,
                "wrong type for index", "[0]");
        assertFailureValueIndexed(
                failure,
                "expected", 1, "Object[]");
        assertFailureValueIndexed(
                failure,
                "but was", 1, "int[]");
    }

    private static Object[] objectArray(Object... ts) {
        return ts;
    }

    private static String[] objectArray(String... ts) {
        return ts;
    }

    private static Set[] objectArray(Set... ts) {
        return ts;
    }
}
