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

import com.google.common.truth.Subject.Factory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.google.common.truth.ExpectFailure.assertThat;
import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Truth.assertThat;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for generic Subject behavior.
 *
 * @author David Saff
 * @author Christian Gruber
 */
class SubjectTest extends BaseSubjectTestCase {

    private static final Object OBJECT_1 =
            new Object() {
                @Override
                public String toString() {
                    return "Object 1";
                }
            };
    private static final Object OBJECT_2 =
            new Object() {
                @Override
                public String toString() {
                    return "Object 2";
                }
            };

    @SuppressWarnings("TruthIncompatibleType") // Intentional for testing purposes.
    @Test
    void toStringsAreIdentical() {
        IntWrapper wrapper = new IntWrapper();
        wrapper.wrapped = 5;
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(5)
                        .isEqualTo(wrapper));
        assertFailureKeys(
                failure,
                "expected", "an instance of", "but was", "an instance of");
        assertFailureValue(
                failure,
                "expected", "5");
        assertFailureValueIndexed(
                failure,
                "an instance of", 0, "com.google.common.truth.SubjectTest$IntWrapper");
        assertFailureValue(
                failure,
                "but was", "(non-equal value with same string representation)");
        assertFailureValueIndexed(
                failure,
                "an instance of", 1, "java.lang.Integer");
    }

    private static class IntWrapper {
        int wrapped;

        @Override
        public String toString() {
            return Integer.toString(wrapped);
        }
    }

    @Test
    void isSameInstanceAsWithNulls() {
        Object o = null;
        assertThat(o).isSameInstanceAs(null);
    }

    @Test
    void isSameInstanceAsFailureWithNulls() {
        Object o = null;
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(o)
                        .isSameInstanceAs("a"));
        assertFailureKeys(
                failure,
                "expected specific instance", "but was");
        assertFailureValue(
                failure,
                "expected specific instance", "a");
    }

    @Test
    void isSameInstanceAsWithSameObject() {
        Object a = new Object();
        Object b = a;
        assertThat(a).isSameInstanceAs(b);
    }

    @Test
    void isSameInstanceAsFailureWithObjects() {
        Object a = OBJECT_1;
        Object b = OBJECT_2;
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(a)
                        .isSameInstanceAs(b));
        assertThat(failure).isNotInstanceOf(ComparisonFailureWithFacts.class);
    }

    @Test
    void isSameInstanceAsFailureWithComparableObjects_nonString() {
        Object a = LocalDate.parse("2014-04-01");
        Object b = LocalDate.parse("2014-04-01");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(a)
                        .isSameInstanceAs(b));
        assertFailureKeys(
                failure,
                "expected specific instance", "but was");
        assertFailureValue(
                failure,
                "expected specific instance", "2014-04-01");
        assertFailureValue(
                failure,
                "but was", "(different but equal instance of same class with same string representation)");
    }

    @Test
    void isSameInstanceAsFailureWithComparableObjects() {
        Object a = "ab";
        Object b = new StringBuilder("ab").toString();
        assertThrows(
                AssertionError.class,
                () -> assertThat(a)
                        .isSameInstanceAs(b));
    }

    @Test
    void isSameInstanceAsFailureWithDifferentTypesAndSameToString() {
        Object a = "true";
        Object b = true;
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(a)
                        .isSameInstanceAs(b));
        assertFailureKeys(
                failure,
                "expected specific instance", "an instance of", "but was", "an instance of");
        assertFailureValue(
                failure,
                "expected specific instance", "true");
        assertFailureValueIndexed(
                failure,
                "an instance of", 0, "java.lang.Boolean");
        assertFailureValue(
                failure,
                "but was", "(non-equal value with same string representation)");
        assertFailureValueIndexed(
                failure,
                "an instance of", 1, "java.lang.String");
    }

    @Test
    void isNotSameInstanceAsWithNulls() {
        Object o = null;
        assertThat(o).isNotSameInstanceAs("a");
    }

    @Test
    void isNotSameInstanceAsFailureWithNulls() {
        Object o = null;
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(o)
                        .isNotSameInstanceAs(null));
        assertFailureKeys(
                failure,
                "expected not to be specific instance");
        assertFailureValue(
                failure,
                "expected not to be specific instance", "null");
    }

    @Test
    void isNotSameInstanceAsWithObjects() {
        Object a = new Object();
        Object b = new Object();
        assertThat(a).isNotSameInstanceAs(b);
    }

    @Test
    void isNotSameInstanceAsFailureWithSameObject() {
        Object a = OBJECT_1;
        Object b = a;
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(a)
                        .isNotSameInstanceAs(b));
        assertFailureKeys(
                failure,
                "expected not to be specific instance");
        assertFailureValue(
                failure,
                "expected not to be specific instance", "Object 1");
    }

    @Test
    void isNotSameInstanceAsWithComparableObjects_nonString() {
        Object a = LocalDate.parse("2014-04-01");
        Object b = LocalDate.parse("2014-04-01");
        assertThat(a).isNotSameInstanceAs(b);
    }

    @Test
    void isNotSameInstanceAsWithComparableObjects() {
        Object a = "ab";
        Object b = new StringBuilder("ab").toString();
        assertThat(a).isNotSameInstanceAs(b);
    }

    @Test
    void isNotSameInstanceAsWithDifferentTypesAndSameToString() {
        Object a = "true";
        Object b = true;
        assertThat(a).isNotSameInstanceAs(b);
    }

    @Test
    void isNull() {
        Object o = null;
        assertThat(o).isNull();
    }

    @Test
    void isNullFail() {
        Object o = new Object();
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(o)
                        .isNull());
        assertFailureKeys(
                failure,
                "expected", "but was");
        assertFailureValue(
                failure,
                "expected", "null");
    }

    @Test
    void isNullWhenSubjectForbidsIsEqualTo() {
        assertAbout(objectsForbiddingEqualityCheck()).that(null).isNull();
    }

    @Test
    void isNullWhenSubjectForbidsIsEqualToFail() {
        assertThrows(
                AssertionError.class,
                () -> assertAbout(objectsForbiddingEqualityCheck())
                        .that(new Object())
                        .isNull());
    }

    @Test
    void stringIsNullFail() {
        assertThrows(
                AssertionError.class,
                () -> assertThat("foo")
                        .isNull());
    }

    @Test
    void isNullBadEqualsImplementation() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(new ThrowsOnEqualsNull())
                        .isNull());
    }

    @Test
    void isNotNull() {
        Object o = new Object();
        assertThat(o).isNotNull();
    }

    @Test
    void isNotNullFail() {
        Object o = null;
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(o)
                        .isNotNull());
        assertFailureKeys(
                failure,
                "expected not to be");
        assertFailureValue(
                failure,
                "expected not to be", "null");
    }

    @Test
    void isNotNullBadEqualsImplementation() {
        assertThat(new ThrowsOnEqualsNull()).isNotNull();
    }

    @Test
    void isNotNullWhenSubjectForbidsIsEqualTo() {
        assertAbout(objectsForbiddingEqualityCheck()).that(new Object()).isNotNull();
    }

    @Test
    void isNotNullWhenSubjectForbidsIsEqualToFail() {
        assertThrows(
                AssertionError.class,
                () -> assertAbout(objectsForbiddingEqualityCheck())
                        .that(null)
                        .isNotNull());
    }

    @Test
    void isEqualToWithNulls() {
        Object o = null;
        assertThat(o).isEqualTo(null);
    }

    @Test
    void isEqualToFailureWithNulls() {
        Object o = null;
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(o)
                        .isEqualTo("a"));
        assertFailureKeys(
                failure,
                "expected", "but was");
        assertFailureValue(
                failure,
                "expected", "a");
        assertFailureValue(
                failure,
                "but was", "null");
    }

    @Test
    void isEqualToStringWithNullVsNull() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("null")
                        .isEqualTo(null));
        assertFailureKeys(
                failure,
                "expected", "an instance of", "but was", "an instance of");
        assertFailureValue(
                failure,
                "expected", "null");
        assertFailureValueIndexed(
                failure,
                "an instance of", 0, "(null reference)");
        assertFailureValue(
                failure,
                "but was", "(non-equal value with same string representation)");
        assertFailureValueIndexed(
                failure,
                "an instance of", 1, "java.lang.String");
    }

    @Test
    void isEqualToWithSameObject() {
        Object a = new Object();
        Object b = a;
        assertThat(a).isEqualTo(b);
    }

    @Test
    void isEqualToFailureWithObjects() {
        Object a = OBJECT_1;
        Object b = OBJECT_2;
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(a)
                        .isEqualTo(b));
        assertFailureKeys(
                failure,
                "expected", "but was");
        assertFailureValue(
                failure,
                "expected", "Object 2");
        assertFailureValue(
                failure,
                "but was", "Object 1");
    }

    @Test
    void isEqualToFailureWithDifferentTypesAndSameToString() {
        Object a = "true";
        Object b = true;
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(a)
                        .isEqualTo(b));
        assertFailureKeys(
                failure,
                "expected", "an instance of", "but was", "an instance of");
        assertFailureValue(
                failure,
                "expected", "true");
        assertFailureValueIndexed(
                failure,
                "an instance of", 0, "java.lang.Boolean");
        assertFailureValue(
                failure,
                "but was", "(non-equal value with same string representation)");
        assertFailureValueIndexed(
                failure,
                "an instance of", 1, "java.lang.String");
    }

    @Test
    void isEqualToNullBadEqualsImplementation() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(new ThrowsOnEqualsNull())
                        .isEqualTo(null));
    }

    @SuppressWarnings("TruthSelfEquals")
    @Test
    void isEqualToSameInstanceBadEqualsImplementation() {
        Object o = new ThrowsOnEquals();
        assertThat(o).isEqualTo(o);
    }

    @Test
    void isNotEqualToWithNulls() {
        Object o = null;
        assertThat(o).isNotEqualTo("a");
    }

    @Test
    void isNotEqualToFailureWithNulls() {
        Object o = null;
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(o)
                        .isNotEqualTo(null));
        assertFailureKeys(
                failure,
                "expected not to be");
        assertFailureValue(
                failure,
                "expected not to be", "null");
    }

    @Test
    void isNotEqualToWithObjects() {
        Object a = new Object();
        Object b = new Object();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void isNotEqualToFailureWithObjects() {
        Object o = 1;
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(o)
                        .isNotEqualTo(1));
        assertFailureKeys(
                failure,
                "expected not to be");
        assertFailureValue(
                failure,
                "expected not to be", "1");
    }

    @Test
    void isNotEqualToFailureWithSameObject() {
        Object a = OBJECT_1;
        Object b = a;
        assertThrows(
                AssertionError.class,
                () -> assertThat(a)
                        .isNotEqualTo(b));
    }

    @Test
    void isNotEqualToWithDifferentTypesAndSameToString() {
        Object a = "true";
        Object b = true;
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void isNotEqualToNullBadEqualsImplementation() {
        assertThat(new ThrowsOnEqualsNull()).isNotEqualTo(null);
    }

    @SuppressWarnings("TruthSelfEquals")
    @Test
    void isNotEqualToSameInstanceBadEqualsImplementation() {
        Object o = new ThrowsOnEquals();
        assertThrows(
                AssertionError.class,
                () -> assertThat(o)
                        .isNotEqualTo(o));
    }

    @Test
    void isInstanceOfExactType() {
        assertThat("a").isInstanceOf(String.class);
    }

    @Test
    void isInstanceOfSuperclass() {
        assertThat(3).isInstanceOf(Number.class);
    }

    @Test
    void isInstanceOfImplementedInterface() {
        assertThat("a").isInstanceOf(CharSequence.class);
    }

    @Test
    void isInstanceOfUnrelatedClass() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(4.5)
                        .isInstanceOf(Long.class));
        assertFailureKeys(
                failure,
                "expected instance of", "but was instance of", "with value");
        assertFailureValue(
                failure,
                "expected instance of", "java.lang.Long");
        assertFailureValue(
                failure,
                "but was instance of", "java.lang.Double");
        assertFailureValue(
                failure,
                "with value", "4.5");
    }

    @Test
    void isInstanceOfUnrelatedInterface() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(4.5)
                        .isInstanceOf(CharSequence.class));
    }

    @Test
    void isInstanceOfClassForNull() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat((Object) null)
                        .isInstanceOf(Long.class));
        assertFailureKeys(
                failure,
                "expected instance of", "but was");
        assertFailureValue(
                failure,
                "expected instance of", "java.lang.Long");
    }

    @Test
    void isInstanceOfInterfaceForNull() {
        assertThrows(
                AssertionError.class,
                () -> assertThat((Object) null)
                        .isInstanceOf(CharSequence.class));
    }

    @Test
    void isNotInstanceOfUnrelatedClass() {
        assertThat("a").isNotInstanceOf(Long.class);
    }

    @Test
    void isNotInstanceOfUnrelatedInterface() {
        assertThat(5).isNotInstanceOf(CharSequence.class);
    }

    @Test
    void isNotInstanceOfExactType() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(5)
                        .isNotInstanceOf(Integer.class));
        assertFailureKeys(
                failure,
                "expected not to be an instance of", "but was");
        assertFailureValue(
                failure,
                "expected not to be an instance of", "java.lang.Integer");
    }

    @Test
    void isNotInstanceOfSuperclass() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(5)
                        .isNotInstanceOf(Number.class));
    }

    @Test
    void isNotInstanceOfImplementedInterface() {
        assertThrows(
                AssertionError.class,
                () -> assertThat("a")
                        .isNotInstanceOf(CharSequence.class));
    }

    @Test
    void isIn() {
        assertThat("b").isIn(oneShotIterable("a", "b", "c"));
    }

    @Test
    void isInJustTwo() {
        assertThat("b").isIn(oneShotIterable("a", "b"));
    }

    @Test
    void isInFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("x")
                        .isIn(oneShotIterable("a", "b", "c")));
        assertFailureKeys(
                failure,
                "expected any of", "but was");
        assertFailureValue(
                failure,
                "expected any of", "[a, b, c]");
    }

    @Test
    void isInNullInListWithNull() {
        assertThat((String) null).isIn(oneShotIterable("a", "b", (String) null));
    }

    @Test
    void isInNonnullInListWithNull() {
        assertThat("b").isIn(oneShotIterable("a", "b", (String) null));
    }

    @Test
    void isInNullFailure() {
        assertThrows(
                AssertionError.class,
                () -> assertThat((String) null)
                        .isIn(oneShotIterable("a", "b", "c")));
    }

    @Test
    void isInEmptyFailure() {
        assertThrows(
                AssertionError.class,
                () -> assertThat("b")
                        .isIn(List.<String>of()));
    }

    @Test
    void isAnyOf() {
        assertThat("b").isAnyOf("a", "b", "c");
    }

    @Test
    void isAnyOfJustTwo() {
        assertThat("b").isAnyOf("a", "b");
    }

    @Test
    void isAnyOfFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("x")
                        .isAnyOf("a", "b", "c"));
        assertFailureKeys(
                failure,
                "expected any of", "but was");
        assertFailureValue(
                failure,
                "expected any of", "[a, b, c]");
    }

    @Test
    void isAnyOfNullInListWithNull() {
        assertThat((String) null).isAnyOf("a", "b", (String) null);
    }

    @Test
    void isAnyOfNonnullInListWithNull() {
        assertThat("b").isAnyOf("a", "b", (String) null);
    }

    @Test
    void isAnyOfNullFailure() {
        assertThrows(
                AssertionError.class,
                () -> assertThat((String) null)
                        .isAnyOf("a", "b", "c"));
    }

    @Test
    void isNotIn() {
        assertThat("x").isNotIn(oneShotIterable("a", "b", "c"));
    }

    @Test
    void isNotInFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("b")
                        .isNotIn(oneShotIterable("a", "b", "c")));
        assertFailureKeys(
                failure,
                "expected not to be any of", "but was");
        assertFailureValue(
                failure,
                "expected not to be any of", "[a, b, c]");
    }

    @Test
    void isNotInNull() {
        assertThat((String) null).isNotIn(oneShotIterable("a", "b", "c"));
    }

    @Test
    void isNotInNullFailure() {
        assertThrows(
                AssertionError.class,
                () -> assertThat((String) null)
                        .isNotIn(oneShotIterable("a", "b", null)));
    }

    @Test
    void isNotInEmpty() {
        assertThat("b").isNotIn(List.<String>of());
    }

    @Test
    void isNoneOf() {
        assertThat("x").isNoneOf("a", "b", "c");
    }

    @Test
    void isNoneOfFailure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("b")
                        .isNoneOf("a", "b", "c"));
        assertFailureKeys(
                failure,
                "expected not to be any of", "but was");
        assertFailureValue(
                failure,
                "expected not to be any of", "[a, b, c]");
    }

    @Test
    void isNoneOfNull() {
        assertThat((String) null).isNoneOf("a", "b", "c");
    }

    @Test
    void isNoneOfNullFailure() {
        assertThrows(
                AssertionError.class,
                () -> assertThat((String) null)
                        .isNoneOf("a", "b", (String) null));
    }

    @Test
    @SuppressWarnings({"EqualsIncompatibleType", "DoNotCall"})
    void equalsThrowsUSOE() {
        try {
            boolean unused = assertThat(5).equals(5);
        } catch (UnsupportedOperationException expected) {
            assertThat(expected)
                    .hasMessageThat()
                    .isEqualTo(
                            "Subject.equals() is not supported. Did you mean to call"
                                    + " assertThat(actual).isEqualTo(expected) instead of"
                                    + " assertThat(actual).equals(expected)?");
            return;
        }
        fail("Should have thrown.");
    }

    @Test
    @SuppressWarnings("DoNotCall")
    void hashCodeThrowsUSOE() {
        try {
            int unused = assertThat(5).hashCode();
        } catch (UnsupportedOperationException expected) {
            assertThat(expected).hasMessageThat().isEqualTo("Subject.hashCode() is not supported.");
            return;
        }
        fail("Should have thrown.");
    }

    @Test
    void ignoreCheckDiscardsFailures() {
        assertThat((Object) null).ignoreCheck().that("foo").isNull();
    }

    private static <T> Iterable<T> oneShotIterable(final T... values) {
        final Iterator<T> iterator = Arrays.asList(values).iterator();
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return iterator;
            }

            @Override
            public String toString() {
                return Arrays.toString(values);
            }
        };
    }

    @Test
    void disambiguationWithSameToString() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(new StringBuilder("foo"))
                        .isEqualTo(new StringBuilder("foo")));
        assertFailureKeys(
                failure,
                "expected", "but was");
        assertFailureValue(
                failure,
                "expected", "foo");
        assertFailureValue(
                failure,
                "but was", "(non-equal instance of same class with same string representation)");
    }

    private static final class ThrowsOnEqualsNull {

        @SuppressWarnings("EqualsHashCode")
        @Override
        public boolean equals(Object obj) {
            requireNonNull(obj); // buggy implementation but one that we're working around, at least for now
            return super.equals(obj);
        }
    }

    private static final class ThrowsOnEquals {

        @SuppressWarnings("EqualsHashCode")
        @Override
        public boolean equals(Object obj) {
            throw new UnsupportedOperationException();
            // buggy implementation but one that we're working around, at least for now
        }
    }

    private static final class ForbidsEqualityChecksSubject extends Subject {
        ForbidsEqualityChecksSubject(FailureMetadata metadata, Object actual) {
            super(metadata, actual);
        }

        // Not sure how to feel about this, but people do it:

        @Override
        public void isEqualTo(Object expected) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void isNotEqualTo(Object unexpected) {
            throw new UnsupportedOperationException();
        }
    }

    private static Subject.Factory<ForbidsEqualityChecksSubject, Object>
    objectsForbiddingEqualityCheck() {
        return new Factory<ForbidsEqualityChecksSubject, Object>() {
            @Override
            public ForbidsEqualityChecksSubject createSubject(FailureMetadata metadata, Object actual) {
                return new ForbidsEqualityChecksSubject(metadata, actual);
            }
        };
    }
}
