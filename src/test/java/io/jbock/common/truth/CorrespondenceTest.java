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
import java.util.function.Function;

import static io.jbock.common.truth.Correspondence.equality;
import static io.jbock.common.truth.Correspondence.tolerance;
import static io.jbock.common.truth.TestCorrespondences.INT_DIFF_FORMATTER;
import static io.jbock.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for {@link Correspondence}.
 *
 * @author Pete Gillin
 */
final class CorrespondenceTest extends BaseSubjectTestCase {
    // Tests of the abstract base class (just assert that equals and hashCode throw).

    private static final Correspondence<Object, Object> INSTANCE =
            Correspondence.from(
                    // If we were allowed to use lambdas, this would be:
                    // (a, e) -> false,
                    new Correspondence.BinaryPredicate<Object, Object>() {
                        @Override
                        public boolean apply(Object actual, Object expected) {
                            return false;
                        }
                    },
                    "has example property");

    @Test
    @SuppressWarnings("deprecation")
        // testing deprecated method
    void testEquals_throws() {
        try {
            INSTANCE.equals(new Object());
            fail("Expected UnsupportedOperationException from Correspondence.equals");
        } catch (UnsupportedOperationException expected) {
        }
    }

    @Test
    @SuppressWarnings("deprecation")
        // testing deprecated method
    void testHashCode_throws() {
        try {
            INSTANCE.hashCode();
            fail("Expected UnsupportedOperationException from Correspondence.hashCode");
        } catch (UnsupportedOperationException expected) {
        }
    }

    // Tests of the 'from' factory method.

    private static final Correspondence<String, String> STRING_PREFIX_EQUALITY =
            // If we were allowed to use method references here, this would be:
            // Correspondence.from(String::startsWith, "starts with");
            Correspondence.from(
                    new Correspondence.BinaryPredicate<String, String>() {
                        @Override
                        public boolean apply(String actual, String expected) {
                            return actual.startsWith(expected);
                        }
                    },
                    "starts with");

    @Test
    void testFrom_compare() {
        assertThat(STRING_PREFIX_EQUALITY.compare("foot", "foo")).isTrue();
        assertThat(STRING_PREFIX_EQUALITY.compare("foot", "foot")).isTrue();
        assertThat(STRING_PREFIX_EQUALITY.compare("foo", "foot")).isFalse();
    }

    @Test
    void testFrom_formatDiff() {
        assertThat(STRING_PREFIX_EQUALITY.formatDiff("foo", "foot")).isNull();
    }

    @Test
    void testFrom_toString() {
        assertThat(STRING_PREFIX_EQUALITY.toString()).isEqualTo("starts with");
    }

    @Test
    void testFrom_isEquality() {
        assertThat(STRING_PREFIX_EQUALITY.isEquality()).isFalse();
    }

    @Test
    void testFrom_viaIterableSubjectContainsExactly_success() {
        assertThat(List.of("foot", "barn"))
                .comparingElementsUsing(STRING_PREFIX_EQUALITY)
                .containsExactly("foo", "bar");
    }

    @Test
    void testFrom_viaIterableSubjectContainsExactly_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(List.of("foot", "barn", "gallon"))
                        .comparingElementsUsing(STRING_PREFIX_EQUALITY)
                        .containsExactly("foo", "bar"));
        assertFailureKeys(
                failure,
                "unexpected (1)", "---", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "unexpected (1)", "gallon");
        assertFailureValue(
                failure,
                "testing whether", "actual element starts with expected element");
    }

    @Test
    void testFrom_viaIterableSubjectContainsExactly_null() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList("foot", "barn", null))
                        .comparingElementsUsing(STRING_PREFIX_EQUALITY)
                        .containsExactly("foo", "bar"));
        assertFailureKeys(
                failure,
                "unexpected (1)",
                "---",
                "expected",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing elements",
                "first exception");
        assertFailureValue(
                failure,
                "unexpected (1)", "null");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(null, foo) threw java.lang.NullPointerException");
    }

    // Tests of the 'transform' factory methods.

    private static final Correspondence<String, Integer> LENGTHS =
            // If we were allowed to use method references here, this would be:
            // Correspondence.transforming(String::length, "has a length of");
            Correspondence.transforming(
                    new Function<String, Integer>() {
                        @Override
                        public Integer apply(String str) {
                            return str.length();
                        }
                    },
                    "has a length of");

    private static final Correspondence<String, Integer> HYPHEN_INDEXES =
            // If we were allowed to use lambdas here, this would be:
            // Correspondence.transforming(
            //     str -> {
            //       int index = str.indexOf('-');
            //       return (index >= 0) ? index : null;
            //     },
            //     "has a hyphen at an index of");
            // (Or else perhaps we'd pull out a method for the lambda body and use a method reference?)
            Correspondence.transforming(
                    new Function<String, Integer>() {
                        @Override
                        public Integer apply(String str) {
                            int index = str.indexOf('-');
                            return (index >= 0) ? index : null;
                        }
                    },
                    "has a hyphen at an index of");

    @Test
    void testTransforming_actual_compare() {
        assertThat(LENGTHS.compare("foo", 3)).isTrue();
        assertThat(LENGTHS.compare("foot", 4)).isTrue();
        assertThat(LENGTHS.compare("foo", 4)).isFalse();
    }

    @Test
    void testTransforming_actual_compare_nullTransformedValues() {
        assertThat(HYPHEN_INDEXES.compare("mailing-list", null)).isFalse();
        assertThat(HYPHEN_INDEXES.compare("forum", 7)).isFalse();
        assertThat(HYPHEN_INDEXES.compare("forum", null)).isTrue();
    }

    @Test
    void testTransforming_actual_compare_nullActualValue() {
        try {
            HYPHEN_INDEXES.compare(null, 7);
            fail("Expected NullPointerException to be thrown but wasn't");
        } catch (NullPointerException expected) {
        }
    }

    @Test
    void testTransforming_actual_formatDiff() {
        assertThat(LENGTHS.formatDiff("foo", 4)).isNull();
    }

    @Test
    void testTransforming_actual_toString() {
        assertThat(LENGTHS.toString()).isEqualTo("has a length of");
    }

    @Test
    void testTransforming_actual_isEquality() {
        assertThat(LENGTHS.isEquality()).isFalse();
    }

    @Test
    void testTransforming_actual_viaIterableSubjectContainsExactly_success() {
        assertThat(List.of("feet", "barns", "gallons"))
                .comparingElementsUsing(LENGTHS)
                .containsExactly(4, 5, 7)
                .inOrder();
    }

    @Test
    void testTransforming_actual_viaIterableSubjectContainsExactly_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(List.of("feet", "barns", "gallons"))
                        .comparingElementsUsing(LENGTHS)
                        .containsExactly(4, 5));
        assertFailureKeys(
                failure,
                "unexpected (1)", "---", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "unexpected (1)", "gallons");
        assertFailureValue(
                failure,
                "testing whether", "actual element has a length of expected element");
    }

    @Test
    void testTransforming_actual_viaIterableSubjectContainsExactly_nullActual() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList("feet", "barns", null))
                        .comparingElementsUsing(LENGTHS)
                        .containsExactly(4, 5));
        assertFailureKeys(
                failure,
                "unexpected (1)",
                "---",
                "expected",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing elements",
                "first exception");
        assertFailureValue(
                failure,
                "unexpected (1)", "null");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(null, 4) threw java.lang.NullPointerException");
    }

    @Test
    void testTransforming_actual_viaIterableSubjectContainsExactly_nullTransformed() {
        // "mailing-list" and "chat-room" have hyphens at index 7 and 4 respectively.
        // "forum" contains no hyphen so the Function in HYPHEN_INDEXES transforms it to null.
        assertThat(List.of("mailing-list", "chat-room", "forum"))
                .comparingElementsUsing(HYPHEN_INDEXES)
                .containsExactly(7, 4, null)
                .inOrder();
    }

    private static final Correspondence<String, String> HYPHENS_MATCH_COLONS =
            // If we were allowed to use lambdas here, this would be:
            // Correspondence.transforming(
            //     str -> {
            //       int index = str.indexOf('-');
            //       return (index >= 0) ? index : null;
            //     },
            //     str -> {
            //       int index = str.indexOf(':');
            //       return (index >= 0) ? index : null;
            //     },
            //     "has a hyphen at the same index as the colon in");
            // (Or else perhaps we'd pull out a method for the lambda bodies?)
            Correspondence.transforming(
                    new Function<String, Integer>() {
                        @Override
                        public Integer apply(String str) {
                            int index = str.indexOf('-');
                            return (index >= 0) ? index : null;
                        }
                    },
                    new Function<String, Integer>() {
                        @Override
                        public Integer apply(String str) {
                            int index = str.indexOf(':');
                            return (index >= 0) ? index : null;
                        }
                    },
                    "has a hyphen at the same index as the colon in");

    @Test
    void testTransforming_both_compare() {
        assertThat(HYPHENS_MATCH_COLONS.compare("mailing-list", "abcdefg:hij")).isTrue();
        assertThat(HYPHENS_MATCH_COLONS.compare("chat-room", "abcd:efghij")).isTrue();
        assertThat(HYPHENS_MATCH_COLONS.compare("chat-room", "abcdefg:hij")).isFalse();
    }

    @Test
    void testTransforming_both_compare_nullTransformedValue() {
        assertThat(HYPHENS_MATCH_COLONS.compare("mailing-list", "abcdefg-hij")).isFalse();
        assertThat(HYPHENS_MATCH_COLONS.compare("forum", "abcde:fghij")).isFalse();
        assertThat(HYPHENS_MATCH_COLONS.compare("forum", "abcde-fghij")).isTrue();
    }

    @Test
    void testTransforming_both_compare_nullInputValues() {
        try {
            HYPHENS_MATCH_COLONS.compare(null, "abcde:fghij");
            fail("Expected NullPointerException to be thrown but wasn't");
        } catch (NullPointerException expected) {
        }
        try {
            HYPHENS_MATCH_COLONS.compare("mailing-list", null);
            fail("Expected NullPointerException to be thrown but wasn't");
        } catch (NullPointerException expected) {
        }
    }

    @Test
    void testTransforming_both_formatDiff() {
        assertThat(HYPHENS_MATCH_COLONS.formatDiff("chat-room", "abcdefg:hij")).isNull();
    }

    @Test
    void testTransforming_both_toString() {
        assertThat(HYPHENS_MATCH_COLONS.toString())
                .isEqualTo("has a hyphen at the same index as the colon in");
    }

    @Test
    void testTransforming_both_isEquality() {
        assertThat(HYPHENS_MATCH_COLONS.isEquality()).isFalse();
    }

    @Test
    void testTransforming_both_viaIterableSubjectContainsExactly_success() {
        assertThat(List.of("mailing-list", "chat-room", "web-app"))
                .comparingElementsUsing(HYPHENS_MATCH_COLONS)
                .containsExactly("abcdefg:hij", "abcd:efghij", "abc:defghij")
                .inOrder();
    }

    @Test
    void testTransforming_both_viaIterableSubjectContainsExactly_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(List.of("mailing-list", "chat-room", "web-app"))
                        .comparingElementsUsing(HYPHENS_MATCH_COLONS)
                        .containsExactly("abcdefg:hij", "abcd:efghij"));
        assertFailureKeys(
                failure,
                "unexpected (1)", "---", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "unexpected (1)", "web-app");
        assertFailureValue(
                failure,
                "testing whether",
                "actual element has a hyphen at the same index as the colon in expected element");
    }

    @Test
    void testTransforming_both_viaIterableSubjectContainsExactly_nullActual() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList("mailing-list", "chat-room", null))
                        .comparingElementsUsing(HYPHENS_MATCH_COLONS)
                        .containsExactly("abcdefg:hij", "abcd:efghij"));
        assertFailureKeys(
                failure,
                "unexpected (1)",
                "---",
                "expected",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing elements",
                "first exception");
        assertFailureValue(
                failure,
                "unexpected (1)", "null");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(null, abcdefg:hij) threw java.lang.NullPointerException");
    }

    @Test
    void testTransforming_both_viaIterableSubjectContainsExactly_nullExpected() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(List.of("mailing-list", "chat-room"))
                        .comparingElementsUsing(HYPHENS_MATCH_COLONS)
                        .containsExactly("abcdefg:hij", "abcd:efghij", null));
        assertFailureKeys(
                failure,
                "missing (1)",
                "---",
                "expected",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing elements",
                "first exception");
        assertFailureValue(
                failure,
                "missing (1)", "null");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(mailing-list, null) threw java.lang.NullPointerException");
    }

    @Test
    void testTransforming_both_viaIterableSubjectContainsExactly_nullTransformed() {
        // The actual element "forum" contains no hyphen, and the expected element "abcde-fghij"
        // contains no colon, so they both transform to null, and so they correspond.
        assertThat(List.of("mailing-list", "chat-room", "forum"))
                .comparingElementsUsing(HYPHENS_MATCH_COLONS)
                .containsExactly("abcdefg:hij", "abcd:efghij", "abcde-fghij")
                .inOrder();
    }

    // Tests of the 'tolerance' factory method. Includes both direct tests of the compare method and
    // indirect tests using it in a basic call chain.

    @Test
    void testTolerance_compare_doubles() {
        assertThat(tolerance(0.0).compare(2.0, 2.0)).isTrue();
        assertThat(tolerance(0.00001).compare(2.0, 2.0)).isTrue();
        assertThat(tolerance(1000.0).compare(2.0, 2.0)).isTrue();
        assertThat(tolerance(1.00001).compare(2.0, 3.0)).isTrue();
        assertThat(tolerance(1000.0).compare(2.0, 1003.0)).isFalse();
        assertThat(tolerance(1000.0).compare(2.0, Double.POSITIVE_INFINITY)).isFalse();
        assertThat(tolerance(1000.0).compare(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY))
                .isFalse();
        assertThat(tolerance(1000.0).compare(2.0, Double.NaN)).isFalse();
        assertThat(tolerance(1000.0).compare(Double.NaN, Double.NaN)).isFalse();
        assertThat(tolerance(0.0).compare(-0.0, 0.0)).isTrue();
    }

    @Test
    void testTolerance_compare_floats() {
        assertThat(tolerance(0.0).compare(2.0f, 2.0f)).isTrue();
        assertThat(tolerance(0.00001).compare(2.0f, 2.0f)).isTrue();
        assertThat(tolerance(1000.0).compare(2.0f, 2.0f)).isTrue();
        assertThat(tolerance(1.00001).compare(2.0f, 3.0f)).isTrue();
        assertThat(tolerance(1000.0).compare(2.0f, 1003.0f)).isFalse();
        assertThat(tolerance(1000.0).compare(2.0f, Float.POSITIVE_INFINITY)).isFalse();
        assertThat(tolerance(1000.0).compare(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY))
                .isFalse();
        assertThat(tolerance(1000.0).compare(2.0f, Float.NaN)).isFalse();
        assertThat(tolerance(1000.0).compare(Float.NaN, Float.NaN)).isFalse();
        assertThat(tolerance(0.0).compare(-0.0f, 0.0f)).isTrue();
    }

    @Test
    void testTolerance_compare_doublesVsInts() {
        assertThat(tolerance(0.0).compare(2.0, 2)).isTrue();
        assertThat(tolerance(0.00001).compare(2.0, 2)).isTrue();
        assertThat(tolerance(1000.0).compare(2.0, 2)).isTrue();
        assertThat(tolerance(1.00001).compare(2.0, 3)).isTrue();
        assertThat(tolerance(1000.0).compare(2.0, 1003)).isFalse();
    }

    @Test
    void testTolerance_compare_negativeTolerance() {
        try {
            tolerance(-0.05).compare(1.0, 2.0);
            fail("Expected IllegalArgumentException to be thrown but wasn't");
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessageThat().isEqualTo("tolerance (-0.05) cannot be negative");
        }
    }

    @Test
    void testTolerance_compare_null() {
        try {
            tolerance(0.05).compare(1.0, null);
            fail("Expected NullPointerException to be thrown but wasn't");
        } catch (NullPointerException expected) {
        }
        try {
            tolerance(0.05).compare(null, 2.0);
            fail("Expected NullPointerException to be thrown but wasn't");
        } catch (NullPointerException expected) {
        }
    }

    @Test
    void testTolerance_formatDiff() {
        assertThat(tolerance(0.01).formatDiff(1.0, 2.0)).isNull();
    }

    @Test
    void testTolerance_toString() {
        assertThat(tolerance(0.01).toString()).isEqualTo("is a finite number within 0.01 of");
    }

    @Test
    void testTolerance_isEquality() {
        assertThat(tolerance(0.01).isEquality()).isFalse();
        // This is close to equality, but not close enough (it calls numbers of different types equal):
        assertThat(tolerance(0.0).isEquality()).isFalse();
    }

    @Test
    void testTolerance_viaIterableSubjectContains_success() {
        assertThat(List.of(1.02, 2.04, 3.08))
                .comparingElementsUsing(tolerance(0.05))
                .contains(2.0);
    }

    @Test
    void testTolerance_viaIterableSubjectContains_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(List.of(1.02, 2.04, 3.08))
                        .comparingElementsUsing(tolerance(0.05))
                        .contains(3.01));
        assertFailureKeys(
                failure,
                "expected to contain", "testing whether", "but was");
        assertFailureValue(
                failure,
                "expected to contain", "3.01");
        assertFailureValue(
                failure,
                "testing whether", "actual element is a finite number within 0.05 of expected element");
        assertFailureValue(
                failure,
                "but was", "[1.02, 2.04, 3.08]");
    }

    // Tests of the 'equality' factory method. Includes both direct tests of the compare method and
    // indirect tests using it in a basic call chain.

    @Test
    void testEquality_compare() {
        assertThat(equality().compare("foo", "foo")).isTrue();
        assertThat(equality().compare("foo", "bar")).isFalse();
        assertThat(equality().compare(123, 123)).isTrue();
        assertThat(equality().compare(123, 123L)).isFalse();
        assertThat(equality().compare(null, null)).isTrue();
        assertThat(equality().compare(null, "bar")).isFalse();
    }

    @Test
    void testEquality_formatDiff() {
        assertThat(equality().formatDiff("foo", "bar")).isNull();
    }

    @Test
    void testEquality_toString() {
        assertThat(equality().toString()).isEqualTo("is equal to"); // meta!
    }

    @Test
    void testEquality_isEquality() {
        assertThat(equality().isEquality()).isTrue();
    }

    @Test
    void testEquality_viaIterableSubjectContains_success() {
        assertThat(List.of(1.0, 2.0, 3.0)).comparingElementsUsing(equality()).contains(2.0);
    }

    @Test
    void testEquality_viaIterableSubjectContains_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(List.of(1.01, 2.02, 3.03))
                        .comparingElementsUsing(equality())
                        .contains(2.0));
        // N.B. No "testing whether" fact:
        assertFailureKeys(
                failure,
                "expected to contain", "but was");
    }

    // Tests of formattingDiffsUsing.

    private static final Correspondence<String, Integer> LENGTHS_WITH_DIFF =
            // If we were allowed to use method references and lambdas here, this would be:
            // Correspondence.transforming(String::length, "has a length of")
            //     .formattingDiffsUsing((a, e) -> Integer.toString(a.length() - e));
            Correspondence.transforming(
                            new Function<String, Integer>() {
                                @Override
                                public Integer apply(String str) {
                                    return str.length();
                                }
                            },
                            "has a length of")
                    .formattingDiffsUsing(
                            new Correspondence.DiffFormatter<String, Integer>() {
                                @Override
                                public String formatDiff(String actualString, Integer expectedLength) {
                                    return Integer.toString(actualString.length() - expectedLength);
                                }
                            });

    @Test
    void testFormattingDiffsUsing_compare() {
        // The compare behaviour should be the same as the wrapped correspondence.
        assertThat(LENGTHS_WITH_DIFF.compare("foo", 3)).isTrue();
        assertThat(LENGTHS_WITH_DIFF.compare("foot", 4)).isTrue();
        assertThat(LENGTHS_WITH_DIFF.compare("foo", 4)).isFalse();
    }

    @Test
    void testFormattingDiffsUsing_formatDiff() {
        assertThat(LENGTHS_WITH_DIFF.formatDiff("foo", 4)).isEqualTo("-1");
        assertThat(LENGTHS_WITH_DIFF.formatDiff("foot", 3)).isEqualTo("1");
    }

    @Test
    void testFormattingDiffsUsing_toString() {
        // The toString behaviour should be the same as the wrapped correspondence.
        assertThat(LENGTHS_WITH_DIFF.toString()).isEqualTo("has a length of");
    }

    @Test
    void testFormattingDiffsUsing_isEquality() {
        // The isEquality behaviour should be the same as the wrapped correspondence.
        assertThat(LENGTHS_WITH_DIFF.isEquality()).isFalse();
        Correspondence<Integer, Integer> equalityWithDiffFormatter =
                Correspondence.<Integer>equality().formattingDiffsUsing(INT_DIFF_FORMATTER);
        assertThat(equalityWithDiffFormatter.isEquality()).isTrue();
    }

    @Test
    void testFormattingDiffsUsing_viaIterableSubjectContainsExactly_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(List.of("feet", "gallons"))
                        .comparingElementsUsing(LENGTHS_WITH_DIFF)
                        .containsExactly(4, 5));
        assertFailureKeys(
                failure,
                "missing (1)",
                "unexpected (1)",
                "#1",
                "diff",
                "---",
                "expected",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "missing (1)", "5");
        assertFailureValue(
                failure,
                "#1", "gallons");
        assertFailureValue(
                failure,
                "diff", "2");
    }

    @Test
    void testFormattingDiffsUsing_viaIterableSubjectContainsExactly_nullActual() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(asList("feet", null))
                        .comparingElementsUsing(LENGTHS_WITH_DIFF)
                        .containsExactly(4, 5));
        assertFailureKeys(
                failure,
                "missing (1)",
                "unexpected (1)",
                "---",
                "expected",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing elements",
                "first exception",
                "additionally, one or more exceptions were thrown while formatting diffs",
                "first exception");
        assertFailureValue(
                failure,
                "missing (1)", "5");
        assertFailureValue(
                failure,
                "unexpected (1)", "[null]");
        assertThatFailure(failure)
                .factValue("first exception", 0)
                .startsWith("compare(null, 4) threw java.lang.NullPointerException");
        assertThatFailure(failure)
                .factValue("first exception", 1)
                .startsWith("formatDiff(null, 5) threw java.lang.NullPointerException");
    }
}
