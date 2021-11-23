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

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Math.nextAfter;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for {@link PrimitiveDoubleArraySubject}.
 *
 * @author Christian Gruber (cgruber@israfil.net)
 */
class PrimitiveDoubleArraySubjectTest extends BaseSubjectTestCase {
    private static final double DEFAULT_TOLERANCE = 0.000005d;

    private static final double OVER_2POINT2 = 2.2000000000000006d;
    private static final double TOLERABLE_2 = 2.0000049999999994d;
    private static final double TOLERABLE_2POINT2 = 2.2000049999999995d;
    private static final double INTOLERABLE_2POINT2 = 2.2000050000000004d;
    private static final double TOLERABLE_3POINT3 = 3.300004999999999d;
    private static final double INTOLERABLE_3POINT3 = 3.300005d;
    private static final double UNDER_MIN_OF_LONG = -9.223372036854778E18d;

    @Test
    void testDoubleConstants_matchNextAfter() {
        assertThat(nextAfter(2.0 + DEFAULT_TOLERANCE, NEGATIVE_INFINITY)).isEqualTo(TOLERABLE_2);
        assertThat(nextAfter(2.2 + DEFAULT_TOLERANCE, NEGATIVE_INFINITY)).isEqualTo(TOLERABLE_2POINT2);
        assertThat(nextAfter(2.2 + DEFAULT_TOLERANCE, POSITIVE_INFINITY))
                .isEqualTo(INTOLERABLE_2POINT2);
        assertThat(nextAfter(2.2, POSITIVE_INFINITY)).isEqualTo(OVER_2POINT2);
        assertThat(nextAfter(3.3 + DEFAULT_TOLERANCE, NEGATIVE_INFINITY)).isEqualTo(TOLERABLE_3POINT3);
        assertThat(nextAfter(3.3 + DEFAULT_TOLERANCE, POSITIVE_INFINITY))
                .isEqualTo(INTOLERABLE_3POINT3);
        assertThat(nextAfter((double) Long.MIN_VALUE, NEGATIVE_INFINITY)).isEqualTo(UNDER_MIN_OF_LONG);
    }

    @Test
    void isEqualTo_WithoutToleranceParameter_Success() {
        assertThat(array(2.2d, 5.4d, POSITIVE_INFINITY, NEGATIVE_INFINITY, 0.0, -0.0))
                .isEqualTo(array(2.2d, 5.4d, POSITIVE_INFINITY, NEGATIVE_INFINITY, 0.0, -0.0));
    }

    @Test
    void isEqualTo_WithoutToleranceParameter_NaN_Success() {
        assertThat(array(2.2d, 5.4d, POSITIVE_INFINITY, NEGATIVE_INFINITY, NaN, 0.0, -0.0))
                .isEqualTo(array(2.2d, 5.4d, POSITIVE_INFINITY, NEGATIVE_INFINITY, NaN, 0.0, -0.0));
    }

    @Test
    void isEqualTo_WithoutToleranceParameter_Fail_NotEqual() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(2.2d))
                        .isEqualTo(array(OVER_2POINT2)));
        assertFailureValue(
                failure,
                "expected", "[2.2000000000000006]");
        assertFailureValue(
                failure,
                "but was", "[2.2]");
        assertFailureValue(
                failure,
                "differs at index", "[0]");
    }

    @Test
    void isEqualTo_WithoutToleranceParameter_Fail_DifferentOrder() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(array(2.2d, 3.3d))
                        .isEqualTo(array(3.3d, 2.2d)));
    }

    @Test
    void isEqualTo_WithoutToleranceParameter_Fail_Longer() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(2.2d, 3.3d))
                        .isEqualTo(array(2.2d, 3.3d, 4.4d)));
        assertFailureKeys(
                failure,
                "expected", "but was", "wrong length", "expected", "but was");
        assertFailureValueIndexed(
                failure,
                "expected", 1, "3");
        assertFailureValueIndexed(
                failure,
                "but was", 1, "2");
    }

    @Test
    void isEqualTo_WithoutToleranceParameter_Fail_Shorter() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(array(2.2d, 3.3d))
                        .isEqualTo(array(2.2d)));
    }

    @Test
    void isEqualTo_WithoutToleranceParameter_Fail_PlusMinusZero() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(0.0d))
                        .isEqualTo(array(-0.0d)));
        assertFailureValue(
                failure,
                "expected", "[-0.0]");
        assertFailureValue(
                failure,
                "but was", "[0.0]");
    }

    @Test
    void isEqualTo_WithoutToleranceParameter_Fail_NotAnArray() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(array(2.2d, 3.3d, 4.4d))
                        .isEqualTo(new Object()));
    }

    @Test
    void isNotEqualTo_WithoutToleranceParameter_FailEquals() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(array(2.2d, 5.4d, POSITIVE_INFINITY, NEGATIVE_INFINITY))
                        .isNotEqualTo(array(2.2d, 5.4d, POSITIVE_INFINITY, NEGATIVE_INFINITY)));
    }

    @Test
    void isNotEqualTo_WithoutToleranceParameter_NaN_plusZero_FailEquals() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(array(2.2d, 5.4d, POSITIVE_INFINITY, NEGATIVE_INFINITY, NaN, 0.0, -0.0))
                        .isNotEqualTo(array(2.2d, 5.4d, POSITIVE_INFINITY, NEGATIVE_INFINITY, NaN, 0.0, -0.0)));
    }

    @Test
    void isNotEqualTo_WithoutToleranceParameter_Success_NotEqual() {
        assertThat(array(2.2d)).isNotEqualTo(array(OVER_2POINT2));
    }

    @Test
    void isNotEqualTo_WithoutToleranceParameter_Success_DifferentOrder() {
        assertThat(array(2.2d, 3.3d)).isNotEqualTo(array(3.3d, 2.2d));
    }

    @Test
    void isNotEqualTo_WithoutToleranceParameter_Success_Longer() {
        assertThat(array(2.2d, 3.3d)).isNotEqualTo(array(2.2d, 3.3d, 4.4d));
    }

    @Test
    void isNotEqualTo_WithoutToleranceParameter_Success_Shorter() {
        assertThat(array(2.2d, 3.3d)).isNotEqualTo(array(2.2d));
    }

    @Test
    void isNotEqualTo_WithoutToleranceParameter_Success_PlusMinusZero() {
        assertThat(array(0.0d)).isNotEqualTo(array(-0.0d));
    }

    @Test
    void isNotEqualTo_WithoutToleranceParameter_Success_NotAnArray() {
        assertThat(array(2.2d, 3.3d, 4.4d)).isNotEqualTo(new Object());
    }

    @Test
    void usingTolerance_contains_success() {
        assertThat(array(1.1, TOLERABLE_2POINT2, 3.3)).usingTolerance(DEFAULT_TOLERANCE).contains(2.2);
    }

    @Test
    void usingTolerance_contains_successWithExpectedLong() {
        assertThat(array(1.0, TOLERABLE_2, 3.0)).usingTolerance(DEFAULT_TOLERANCE).contains(2L);
    }

    @Test
    void usingTolerance_contains_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1, INTOLERABLE_2POINT2, 3.3))
                        .usingTolerance(DEFAULT_TOLERANCE)
                        .contains(2.2));
        assertFailureKeys(
                failure,
                "value of", "expected to contain", "testing whether", "but was");
        assertFailureValue(
                failure,
                "value of", "array.asList()");
        assertFailureValue(
                failure,
                "expected to contain", "2.2");
        assertFailureValue(
                failure,
                "testing whether",
                "actual element is a finite number within " + DEFAULT_TOLERANCE + " of expected element");
        assertFailureValue(
                failure,
                "but was", "[1.1, " + INTOLERABLE_2POINT2 + ", 3.3]");
    }

    @Test
    void usingTolerance_contains_failureWithInfinity() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1, POSITIVE_INFINITY, 3.3))
                        .usingTolerance(DEFAULT_TOLERANCE)
                        .contains(POSITIVE_INFINITY));
        assertFailureKeys(
                failure,
                "value of", "expected to contain", "testing whether", "but was");
        assertFailureValue(
                failure,
                "expected to contain", "Infinity");
        assertFailureValue(
                failure,
                "but was", "[1.1, Infinity, 3.3]");
    }

    @Test
    void usingTolerance_contains_failureWithNaN() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1, NaN, 3.3))
                        .usingTolerance(DEFAULT_TOLERANCE)
                        .contains(NaN));
        assertFailureKeys(
                failure,
                "value of", "expected to contain", "testing whether", "but was");
        assertFailureValue(
                failure,
                "expected to contain", "NaN");
        assertFailureValue(
                failure,
                "but was", "[1.1, NaN, 3.3]");
    }

    @Test
    void usingTolerance_contains_successWithNegativeZero() {
        assertThat(array(1.1, -0.0, 3.3)).usingTolerance(0.0).contains(0.0);
    }

    @Test
    void usingTolerance_contains_otherTypes() {
        // Expected value is Float
        assertThat(array(1.0, 2.0 + 0.5 * DEFAULT_TOLERANCE, 3.0))
                .usingTolerance(DEFAULT_TOLERANCE)
                .contains(2.0f);
        // Expected value is Integer
        assertThat(array(1.0, 2.0 + 0.5 * DEFAULT_TOLERANCE, 3.0))
                .usingTolerance(DEFAULT_TOLERANCE)
                .contains(2);
        // Expected value is Integer.MAX_VALUE
        assertThat(array(1.0, Integer.MAX_VALUE + 0.5 * DEFAULT_TOLERANCE, 3.0))
                .usingTolerance(DEFAULT_TOLERANCE)
                .contains(Integer.MAX_VALUE);
        // Expected value is Long
        assertThat(array(1.0, 2.0 + 0.5 * DEFAULT_TOLERANCE, 3.0))
                .usingTolerance(DEFAULT_TOLERANCE)
                .contains(2L);
        // Expected value is Long.MIN_VALUE. This is -1*2^63, which has an exact double representation.
        // For the actual value we use the next value down, which is is 2^11 smaller (because the
        // resolution of doubles with absolute values between 2^63 and 2^64 is 2^11). So we'll make the
        // assertion with a tolerance of 2^12.
        assertThat(array(1.0, UNDER_MIN_OF_LONG, 3.0)).usingTolerance(1 << 12).contains(Long.MIN_VALUE);
        // Expected value is BigInteger
        assertThat(array(1.0, 2.0 + 0.5 * DEFAULT_TOLERANCE, 3.0))
                .usingTolerance(DEFAULT_TOLERANCE)
                .contains(BigInteger.valueOf(2));
        // Expected value is BigDecimal
        assertThat(array(1.0, 2.0 + 0.5 * DEFAULT_TOLERANCE, 3.0))
                .usingTolerance(DEFAULT_TOLERANCE)
                .contains(BigDecimal.valueOf(2.0));
    }

    @Test
    void usingTolerance_contains_nullExpected() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1, 2.2, 3.3))
                        .usingTolerance(DEFAULT_TOLERANCE)
                        .contains(null));
        assertFailureKeys(
                failure,
                "value of",
                "expected to contain",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing elements",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(1.1, null) threw java.lang.NullPointerException");
    }

    @Test
    void usingTolerance_contains_negativeTolerance() {
        try {
            assertThat(array(1.1, 2.2, 3.3)).usingTolerance(-1.1 * DEFAULT_TOLERANCE).contains(2.0f);
            fail("Expected IllegalArgumentException to be thrown but wasn't");
        } catch (IllegalArgumentException expected) {
            assertThat(expected)
                    .hasMessageThat()
                    .isEqualTo("tolerance (" + -1.1 * DEFAULT_TOLERANCE + ") cannot be negative");
        }
    }

    @Test
    void usingTolerance_containsAtLeast_primitiveDoubleArray_success() {
        assertThat(array(1.1, TOLERABLE_2POINT2, 3.3))
                .usingTolerance(DEFAULT_TOLERANCE)
                .containsAtLeast(array(2.2, 1.1));
    }

    @Test
    void usingTolerance_containsAtLeast_primitiveDoubleArray_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1, TOLERABLE_2POINT2, 3.3))
                        .usingTolerance(DEFAULT_TOLERANCE)
                        .containsAtLeast(array(2.2, 99.99)));
        assertFailureKeys(
                failure,
                "value of",
                "missing (1)",
                "---",
                "expected to contain at least",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "missing (1)", "99.99");
    }

    @Test
    void usingTolerance_containsAtLeast_primitiveDoubleArray_inOrder_success() {
        assertThat(array(1.1, TOLERABLE_2POINT2, 3.3))
                .usingTolerance(DEFAULT_TOLERANCE)
                .containsAtLeast(array(1.1, 2.2))
                .inOrder();
    }

    @Test
    void usingTolerance_containsAtLeast_primitiveDoubleArray_inOrder_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1, TOLERABLE_2POINT2, 3.3))
                        .usingTolerance(DEFAULT_TOLERANCE)
                        .containsAtLeast(array(2.2, 1.1))
                        .inOrder());
        assertFailureKeys(
                failure,
                "value of",
                "required elements were all found, but order was wrong",
                "expected order for required elements",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "expected order for required elements", "[2.2, 1.1]");
    }

    @Test
    void usingTolerance_containsAnyOf_primitiveDoubleArray_success() {
        assertThat(array(1.1, TOLERABLE_2POINT2, 3.3))
                .usingTolerance(DEFAULT_TOLERANCE)
                .containsAnyOf(array(99.99, 2.2));
    }

    @Test
    void usingTolerance_containsAnyOf_primitiveDoubleArray_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1, TOLERABLE_2POINT2, 3.3))
                        .usingTolerance(DEFAULT_TOLERANCE)
                        .containsAnyOf(array(99.99, 999.999)));
        assertFailureKeys(
                failure,
                "value of", "expected to contain any of", "testing whether", "but was");
        assertFailureValue(
                failure,
                "expected to contain any of", "[99.99, 999.999]");
    }

    @Test
    void usingTolerance_containsExactly_primitiveDoubleArray_success() {
        assertThat(array(1.1, TOLERABLE_2POINT2, 3.3))
                .usingTolerance(DEFAULT_TOLERANCE)
                .containsExactly(array(2.2, 1.1, 3.3));
    }

    @Test
    void usingTolerance_containsExactly_primitiveDoubleArray_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1, TOLERABLE_2POINT2, 3.3))
                        .usingTolerance(DEFAULT_TOLERANCE)
                        .containsExactly(array(2.2, 1.1)));
        assertFailureKeys(
                failure,
                "value of", "unexpected (1)", "---", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "unexpected (1)", "3.3");
    }

    @Test
    void usingTolerance_containsExactly_primitiveDoubleArray_inOrder_success() {
        assertThat(array(1.1, TOLERABLE_2POINT2, 3.3))
                .usingTolerance(DEFAULT_TOLERANCE)
                .containsExactly(array(1.1, 2.2, 3.3))
                .inOrder();
    }

    @Test
    void usingTolerance_containsExactly_primitiveDoubleArray_inOrder_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1, TOLERABLE_2POINT2, 3.3))
                        .usingTolerance(DEFAULT_TOLERANCE)
                        .containsExactly(array(2.2, 1.1, 3.3))
                        .inOrder());
        assertFailureKeys(
                failure,
                "value of",
                "contents match, but order was wrong",
                "expected",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "expected", "[2.2, 1.1, 3.3]");
    }

    @Test
    void usingTolerance_containsNoneOf_primitiveDoubleArray_success() {
        assertThat(array(1.1, TOLERABLE_2POINT2, 3.3))
                .usingTolerance(DEFAULT_TOLERANCE)
                .containsNoneOf(array(99.99, 999.999));
    }

    @Test
    void usingTolerance_containsNoneOf_primitiveDoubleArray_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1, TOLERABLE_2POINT2, 3.3))
                        .usingTolerance(DEFAULT_TOLERANCE)
                        .containsNoneOf(array(99.99, 2.2)));
        assertFailureKeys(
                failure,
                "value of",
                "expected not to contain any of",
                "testing whether",
                "but contained",
                "corresponding to",
                "---",
                "full contents");
        assertFailureValue(
                failure,
                "expected not to contain any of", "[99.99, 2.2]");
        assertFailureValue(
                failure,
                "but contained", "[" + TOLERABLE_2POINT2 + "]");
        assertFailureValue(
                failure,
                "corresponding to", "2.2");
    }

    @Test
    void usingExactEquality_contains_success() {
        assertThat(array(1.1, 2.2, 3.3)).usingExactEquality().contains(2.2);
    }

    @Test
    void usingExactEquality_contains_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1, OVER_2POINT2, 3.3))
                        .usingExactEquality()
                        .contains(2.2));
        assertFailureKeys(
                failure,
                "value of", "expected to contain", "testing whether", "but was");
        assertFailureValue(
                failure,
                "expected to contain", "2.2");
        assertFailureValue(
                failure,
                "testing whether", "actual element is exactly equal to expected element");
        assertFailureValue(
                failure,
                "but was", "[1.1, " + OVER_2POINT2 + ", 3.3]");
    }

    @Test
    void usingExactEquality_contains_otherTypes() {
        // Expected value is Float
        assertThat(array(1.0, 2.0, 3.0)).usingExactEquality().contains(2.0f);
        // Expected value is Integer
        assertThat(array(1.0, 2.0, 3.0)).usingExactEquality().contains(2);
        assertThat(array(1.0, Integer.MAX_VALUE, 3.0)).usingExactEquality().contains(Integer.MAX_VALUE);
        // Expected value is Long - supported up to +/- 2^53
        assertThat(array(1.0, 2.0, 3.0)).usingExactEquality().contains(2L);
        assertThat(array(1.0, 1L << 53, 3.0)).usingExactEquality().contains(1L << 53);
    }

    @Test
    void usingExactEquality_contains_otherTypes_longOutOfRange() {
        long expected = (1L << 53) + 1L;
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1, 2.2, 3.3))
                        .usingExactEquality()
                        .contains(expected));
        assertFailureKeys(
                failure,
                "value of",
                "expected to contain",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing elements",
                "first exception");
        assertFailureValue(
                failure,
                "expected to contain", Long.toString(expected));
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(1.1, " + expected + ") threw java.lang.IllegalArgumentException");
        assertThatFailure(failure)
                .factValue("first exception")
                .contains(
                        "Expected value "
                                + expected
                                + " in assertion using exact double equality was a long with an absolute value "
                                + "greater than 2^52 which has no exact double representation");
    }

    @Test
    void usingExactEquality_contains_otherTypes_bigIntegerNotSupported() {
        BigInteger expected = BigInteger.valueOf(2);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1, 2.2, 3.3))
                        .usingExactEquality()
                        .contains(expected));
        assertFailureKeys(
                failure,
                "value of",
                "expected to contain",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing elements",
                "first exception");
        assertFailureValue(
                failure,
                "expected to contain", "2");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(1.1, " + expected + ") threw java.lang.IllegalArgumentException");
        assertThatFailure(failure)
                .factValue("first exception")
                .contains(
                        "Expected value in assertion using exact double equality was of unsupported type "
                                + BigInteger.class
                                + " (it may not have an exact double representation)");
    }

    @Test
    void usingExactEquality_contains_otherTypes_bigDecimalNotSupported() {
        BigDecimal expected = BigDecimal.valueOf(2.0);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1, 2.2, 3.3))
                        .usingExactEquality()
                        .contains(expected));
        assertFailureKeys(
                failure,
                "value of",
                "expected to contain",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing elements",
                "first exception");
        assertFailureValue(
                failure,
                "expected to contain", expected.toString());
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(1.1, " + expected + ") threw java.lang.IllegalArgumentException");
        assertThatFailure(failure)
                .factValue("first exception")
                .contains(
                        "Expected value in assertion using exact double equality was of unsupported type "
                                + BigDecimal.class
                                + " (it may not have an exact double representation)");
    }

    @Test
    void usingExactEquality_contains_successWithInfinity() {
        assertThat(array(1.1, POSITIVE_INFINITY, 3.3)).usingExactEquality().contains(POSITIVE_INFINITY);
    }

    @Test
    void usingExactEquality_contains_successWithNaN() {
        assertThat(array(1.1, NaN, 3.3)).usingExactEquality().contains(NaN);
    }

    @Test
    void usingExactEquality_contains_failureWithNegativeZero() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1, -0.0, 3.3))
                        .usingExactEquality().contains(0.0));
        assertFailureKeys(
                failure,
                "value of", "expected to contain", "testing whether", "but was");
        /*
         * TODO(cpovirk): Find a way to print "0.0" rather than 0 in the error, even under GWT. One
         * easy(?) hack would be to make UsingCorrespondence use Platform.doubleToString() when
         * applicable. Or maybe Correspondence implementations should be able to provide custom string
         * conversions, similar to how we plan to let them render their own diffs.
         */
        assertFailureValue(
                failure,
                "expected to contain", Double.toString(0.0));
    }

    @Test
    void usingExactEquality_contains_nullExpected() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1, 2.2, 3.3))
                        .usingExactEquality()
                        .contains(null));
        assertFailureKeys(
                failure,
                "value of",
                "expected to contain",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing elements",
                "first exception");
        assertFailureValue(
                failure,
                "expected to contain", "null");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(1.1, null) threw java.lang.NullPointerException");
    }

    @Test
    void usingExactEquality_containsAtLeast_primitiveDoubleArray_success() {
        assertThat(array(1.1, 2.2, 3.3)).usingExactEquality().containsAtLeast(array(2.2, 1.1));
    }

    @Test
    void usingExactEquality_containsAtLeast_primitiveDoubleArray_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1, 2.2, 3.3))
                        .usingExactEquality()
                        .containsAtLeast(array(2.2, 99.99)));
        assertFailureKeys(
                failure,
                "value of",
                "missing (1)",
                "---",
                "expected to contain at least",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "missing (1)", "99.99");
    }

    @Test
    void usingExactEquality_containsAtLeast_primitiveDoubleArray_inOrder_success() {
        assertThat(array(1.1, 2.2, 3.3))
                .usingExactEquality()
                .containsAtLeast(array(1.1, 2.2))
                .inOrder();
    }

    @Test
    void usingExactEquality_containsAtLeast_primitiveDoubleArray_inOrder_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1, 2.2, 3.3))
                        .usingExactEquality()
                        .containsAtLeast(array(2.2, 1.1))
                        .inOrder());
        assertFailureKeys(
                failure,
                "value of",
                "required elements were all found, but order was wrong",
                "expected order for required elements",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "expected order for required elements", "[2.2, 1.1]");
    }

    @Test
    void usingExactEquality_containsAnyOf_primitiveDoubleArray_success() {
        assertThat(array(1.1, 2.2, 3.3)).usingExactEquality().containsAnyOf(array(99.99, 2.2));
    }

    @Test
    void usingExactEquality_containsAnyOf_primitiveDoubleArray_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1, 2.2, 3.3))
                        .usingExactEquality()
                        .containsAnyOf(array(99.99, 999.999)));
        assertFailureKeys(
                failure,
                "value of", "expected to contain any of", "testing whether", "but was");
    }

    @Test
    void usingExactEquality_containsExactly_primitiveDoubleArray_success() {
        assertThat(array(1.1, 2.2, 3.3)).usingExactEquality().containsExactly(array(2.2, 1.1, 3.3));
    }

    @Test
    void usingExactEquality_containsExactly_primitiveDoubleArray_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1, 2.2, 3.3))
                        .usingExactEquality()
                        .containsExactly(array(2.2, 1.1)));
        assertFailureKeys(
                failure,
                "value of", "unexpected (1)", "---", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "unexpected (1)", "3.3");
    }

    @Test
    void usingExactEquality_containsExactly_primitiveDoubleArray_inOrder_success() {
        assertThat(array(1.1, 2.2, 3.3))
                .usingExactEquality()
                .containsExactly(array(1.1, 2.2, 3.3))
                .inOrder();
    }

    @Test
    void usingExactEquality_containsExactly_primitiveDoubleArray_inOrder_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1, 2.2, 3.3))
                        .usingExactEquality()
                        .containsExactly(array(2.2, 1.1, 3.3))
                        .inOrder());
        assertFailureKeys(
                failure,
                "value of",
                "contents match, but order was wrong",
                "expected",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "expected", "[2.2, 1.1, 3.3]");
    }

    @Test
    void usingExactEquality_containsNoneOf_primitiveDoubleArray_success() {
        assertThat(array(1.1, 2.2, 3.3)).usingExactEquality().containsNoneOf(array(99.99, 999.999));
    }

    @Test
    void usingExactEquality_containsNoneOf_primitiveDoubleArray_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1, 2.2, 3.3))
                        .usingExactEquality()
                        .containsNoneOf(array(99.99, 2.2)));
        assertFailureKeys(
                failure,
                "value of",
                "expected not to contain any of",
                "testing whether",
                "but contained",
                "corresponding to",
                "---",
                "full contents");
        assertFailureValue(
                failure,
                "expected not to contain any of", "[99.99, 2.2]");
        assertFailureValue(
                failure,
                "but contained", "[2.2]");
        assertFailureValue(
                failure,
                "corresponding to", "2.2");
    }

    @Test
    void smallDifferenceInLongRepresentation() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(array(-4.4501477170144023E-308))
                        .isEqualTo(array(-4.450147717014402E-308)));
    }

    @Test
    void noCommas() {
        // Maybe we should include commas, but we don't yet, so make sure we don't under GWT, either.
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(10000.0))
                        .isEqualTo(array(20000.0)));
        assertFailureValue(
                failure,
                "expected", "[20000.0]");
        assertFailureValue(
                failure,
                "but was", "[10000.0]");
    }

    private static double[] array(double... primitives) {
        return primitives;
    }
}
