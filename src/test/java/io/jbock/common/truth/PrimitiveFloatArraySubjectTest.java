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
package io.jbock.common.truth;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

import static io.jbock.common.truth.Platform.floatToString;
import static io.jbock.common.truth.Truth.assertThat;
import static java.lang.Float.NEGATIVE_INFINITY;
import static java.lang.Float.NaN;
import static java.lang.Float.POSITIVE_INFINITY;
import static java.lang.Math.nextAfter;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for {@link PrimitiveFloatArraySubject}.
 *
 * @author Christian Gruber (cgruber@israfil.net)
 */
class PrimitiveFloatArraySubjectTest extends BaseSubjectTestCase {
    private static final float DEFAULT_TOLERANCE = 0.000005f;

    private static final float JUST_OVER_2POINT2 = 2.2000003f;
    private static final float JUST_OVER_3POINT3 = 3.3000002f;
    private static final float TOLERABLE_3POINT3 = 3.3000047f;
    private static final float INTOLERABLE_3POINT3 = 3.3000052f;
    private static final float UNDER_LONG_MIN = -9.223373E18f;
    private static final float TOLERABLE_TWO = 2.0000048f;
    private static final float TOLERABLE_2POINT2 = 2.2000048f;
    private static final float INTOLERABLE_2POINT2 = 2.2000053f;

    @Test
    void testFloatConstants_matchNextAfter() {
        assertThat(nextAfter(2.2f, POSITIVE_INFINITY)).isEqualTo(JUST_OVER_2POINT2);
        assertThat(nextAfter(3.3f, POSITIVE_INFINITY)).isEqualTo(JUST_OVER_3POINT3);
        assertThat(nextAfter(3.3f + DEFAULT_TOLERANCE, NEGATIVE_INFINITY)).isEqualTo(TOLERABLE_3POINT3);
        assertThat(nextAfter(3.3f + DEFAULT_TOLERANCE, POSITIVE_INFINITY))
                .isEqualTo(INTOLERABLE_3POINT3);
        assertThat(nextAfter(Long.MIN_VALUE, NEGATIVE_INFINITY)).isEqualTo(UNDER_LONG_MIN);
        assertThat(nextAfter(2.2f + DEFAULT_TOLERANCE, NEGATIVE_INFINITY)).isEqualTo(TOLERABLE_2POINT2);
        assertThat(nextAfter(2.2f + DEFAULT_TOLERANCE, POSITIVE_INFINITY))
                .isEqualTo(INTOLERABLE_2POINT2);
    }

    @Test
    void isEqualTo_WithoutToleranceParameter_Success() {
        assertThat(array(2.2f, 5.4f, POSITIVE_INFINITY, NEGATIVE_INFINITY, NaN, 0.0f, -0.0f))
                .isEqualTo(array(2.2f, 5.4f, POSITIVE_INFINITY, NEGATIVE_INFINITY, NaN, 0.0f, -0.0f));
    }

    @Test
    void isEqualTo_WithoutToleranceParameter_Fail_NotEqual() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(2.2f))
                        .isEqualTo(array(JUST_OVER_2POINT2)));
        assertFailureKeys(
                failure,
                "expected", "but was", "differs at index");
        assertFailureValue(
                failure,
                "expected", "[" + floatToString(JUST_OVER_2POINT2) + "]");
        assertFailureValue(
                failure,
                "but was", "[" + floatToString(2.2f) + "]");
        assertFailureValue(
                failure,
                "differs at index", "[0]");
    }

    @Test
    void isEqualTo_WithoutToleranceParameter_Fail_DifferentOrder() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(array(2.2f, 3.3f))
                        .isEqualTo(array(3.3f, 2.2f)));
    }

    @Test
    void isEqualTo_WithoutToleranceParameter_Fail_Longer() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(2.2f, 3.3f))
                        .isEqualTo(array(2.2f, 3.3f, 4.4f)));
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
                () -> assertThat(array(2.2f, 3.3f))
                        .isEqualTo(array(2.2f)));
    }

    @Test
    void isEqualTo_WithoutToleranceParameter_Fail_PlusMinusZero() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(0.0f))
                        .isEqualTo(array(-0.0f)));
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
                () -> assertThat(array(2.2f, 3.3f, 4.4f))
                        .isEqualTo(new Object()));
    }

    @Test
    void isNotEqualTo_WithoutToleranceParameter_FailEquals() {
        assertThrows(
                AssertionError.class,
                () -> assertThat(array(2.2f, 5.4f, POSITIVE_INFINITY, NEGATIVE_INFINITY, NaN, 0.0f, -0.0f))
                        .isNotEqualTo(array(2.2f, 5.4f, POSITIVE_INFINITY, NEGATIVE_INFINITY, NaN, 0.0f, -0.0f)));
    }

    @Test
    void isNotEqualTo_WithoutToleranceParameter_Success_NotEqual() {
        assertThat(array(2.2f)).isNotEqualTo(array(JUST_OVER_2POINT2));
    }

    @Test
    void isNotEqualTo_WithoutToleranceParameter_Success_DifferentOrder() {
        assertThat(array(2.2f, 3.3f)).isNotEqualTo(array(3.3f, 2.2f));
    }

    @Test
    void isNotEqualTo_WithoutToleranceParameter_Success_Longer() {
        assertThat(array(2.2f, 3.3f)).isNotEqualTo(array(2.2f, 3.3f, 4.4f));
    }

    @Test
    void isNotEqualTo_WithoutToleranceParameter_Success_Shorter() {
        assertThat(array(2.2f, 3.3f)).isNotEqualTo(array(2.2f));
    }

    @Test
    void isNotEqualTo_WithoutToleranceParameter_Success_PlusMinusZero() {
        assertThat(array(0.0f)).isNotEqualTo(array(-0.0f));
    }

    @Test
    void isNotEqualTo_WithoutToleranceParameter_Success_NotAnArray() {
        assertThat(array(2.2f, 3.3f, 4.4f)).isNotEqualTo(new Object());
    }

    @Test
    void usingTolerance_contains_success() {
        assertThat(array(1.1f, TOLERABLE_2POINT2, 3.2f))
                .usingTolerance(DEFAULT_TOLERANCE)
                .contains(2.2f);
    }

    @Test
    void usingTolerance_contains_successWithExpectedLong() {
        assertThat(array(1.0f, TOLERABLE_TWO, 3.0f)).usingTolerance(DEFAULT_TOLERANCE).contains(2L);
    }

    @Test
    void usingTolerance_contains_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1f, INTOLERABLE_2POINT2, 3.3f))
                        .usingTolerance(DEFAULT_TOLERANCE)
                        .contains(2.0f));
        assertFailureKeys(
                failure,
                "value of", "expected to contain", "testing whether", "but was");
        assertFailureValue(
                failure,
                "expected to contain", Float.toString(2.0f));
        assertFailureValue(
                failure,
                "testing whether",
                "actual element is a finite number within "
                        + (double) DEFAULT_TOLERANCE
                        + " of expected element");
        assertFailureValue(
                failure,
                "but was", "[" + 1.1f + ", " + INTOLERABLE_2POINT2 + ", " + 3.3f + "]");
    }

    @Test
    void usingTolerance_contains_failureWithInfinity() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1f, POSITIVE_INFINITY, 3.3f))
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
                "but was", "[" + 1.1f + ", Infinity, " + 3.3f + "]");
    }

    @Test
    void usingTolerance_contains_failureWithNaN() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1f, NaN, 3.3f))
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
                "but was", "[" + 1.1f + ", NaN, " + 3.3f + "]");
    }

    @Test
    void usingTolerance_contains_successWithNegativeZero() {
        assertThat(array(1.0f, -0.0f, 3.0f)).usingTolerance(0.0f).contains(0.0f);
    }

    @Test
    void usingTolerance_contains_otherTypes() {
        // Expected value is Double
        assertThat(array(1.0f, 2.0f + 0.5f * DEFAULT_TOLERANCE, 3.0f))
                .usingTolerance(DEFAULT_TOLERANCE)
                .contains(2.0);
        // Expected value is Integer
        assertThat(array(1.0f, 2.0f + 0.5f * DEFAULT_TOLERANCE, 3.0f))
                .usingTolerance(DEFAULT_TOLERANCE)
                .contains(2);
        // Expected value is Integer.MIN_VALUE. This is -1*2^31, which has an exact float
        // representation. For the actual value we use the next value down, which is 2^8 smaller
        // (because the resolution of floats with absolute values between 2^31 and 2^32 is 2^8). So
        // we'll make the assertion with a tolerance of 2^9.
        assertThat(array(1.0f, Integer.MIN_VALUE + 0.5f * DEFAULT_TOLERANCE, 3.0f))
                .usingTolerance(1 << 9)
                .contains(Integer.MIN_VALUE);
        // Expected value is Long
        assertThat(array(1.0f, 2.0f + 0.5f * DEFAULT_TOLERANCE, 3.0f))
                .usingTolerance(DEFAULT_TOLERANCE)
                .contains(2L);
        // Expected value is Long.MIN_VALUE. This is -1*2^63, which has an exact float representation.
        // For the actual value we use the next value down, which is is 2^40 smaller (because the
        // resolution of floats with absolute values between 2^63 and 2^64 is 2^40). So we'll make the
        // assertion with a tolerance of 2^41.
        assertThat(array(1.0f, UNDER_LONG_MIN, 3.0f)).usingTolerance(1L << 41).contains(Long.MIN_VALUE);
        // Expected value is BigInteger
        assertThat(array(1.0f, 2.0f + 0.5f * DEFAULT_TOLERANCE, 3.0f))
                .usingTolerance(DEFAULT_TOLERANCE)
                .contains(BigInteger.valueOf(2));
        // Expected value is BigDecimal
        assertThat(array(1.0f, 2.0f + 0.5f * DEFAULT_TOLERANCE, 3.0f))
                .usingTolerance(DEFAULT_TOLERANCE)
                .contains(BigDecimal.valueOf(2.0));
    }

    @Test
    void usingTolerance_contains_nullExpected() {
        float[] actual = array(1.0f, 2.0f, 3.0f);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
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
                .startsWith("compare(" + actual[0] + ", null) threw java.lang.NullPointerException");
    }

    @Test
    void usingTolerance_contains_negativeTolerance() {
        try {
            assertThat(array(1.0f, 2.0f, 3.0f)).usingTolerance(-1.0f * DEFAULT_TOLERANCE).contains(2.0f);
            fail("Expected IllegalArgumentException to be thrown but wasn't");
        } catch (IllegalArgumentException expected) {
            assertThat(expected)
                    .hasMessageThat()
                    .isEqualTo("tolerance (" + -1.0 * DEFAULT_TOLERANCE + ") cannot be negative");
        }
    }

    @Test
    void usingTolerance_containsAtLeast_primitiveFloatArray_success() {
        assertThat(array(1.1f, TOLERABLE_2POINT2, 3.3f))
                .usingTolerance(DEFAULT_TOLERANCE)
                .containsAtLeast(array(2.2f, 1.1f));
    }

    @Test
    void usingTolerance_containsAtLeast_primitiveFloatArray_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1f, TOLERABLE_2POINT2, 3.3f))
                        .usingTolerance(DEFAULT_TOLERANCE)
                        .containsAtLeast(array(2.2f, 99.99f)));
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
                "missing (1)", Float.toString(99.99f));
    }

    @Test
    void usingTolerance_containsAtLeast_primitiveFloatArray_inOrder_success() {
        assertThat(array(1.1f, TOLERABLE_2POINT2, 3.3f))
                .usingTolerance(DEFAULT_TOLERANCE)
                .containsAtLeast(array(1.1f, 2.2f))
                .inOrder();
    }

    @Test
    void usingTolerance_containsAtLeast_primitiveFloatArray_inOrder_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1f, TOLERABLE_2POINT2, 3.3f))
                        .usingTolerance(DEFAULT_TOLERANCE)
                        .containsAtLeast(array(2.2f, 1.1f))
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
                "expected order for required elements", String.format(Locale.ROOT, "[%.1f, %.1f]", 2.2f, 1.1f));
    }

    @Test
    void usingTolerance_containsAnyOf_primitiveFloatArray_success() {
        assertThat(array(1.0f, TOLERABLE_2POINT2, 3.0f))
                .usingTolerance(DEFAULT_TOLERANCE)
                .containsAnyOf(array(99.99f, 2.2f));
    }

    @Test
    void usingTolerance_containsAnyOf_primitiveFloatArray_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1f, TOLERABLE_2POINT2, 3.3f))
                        .usingTolerance(DEFAULT_TOLERANCE)
                        .containsAnyOf(array(99.99f, 999.999f)));
        assertFailureKeys(
                failure,
                "value of", "expected to contain any of", "testing whether", "but was");
        assertFailureValue(
                failure,
                "expected to contain any of", "[" + 99.99f + ", " + 999.999f + "]");
    }

    @Test
    void usingTolerance_containsExactly_primitiveFloatArray_success() {
        assertThat(array(1.1f, TOLERABLE_2POINT2, 3.3f))
                .usingTolerance(DEFAULT_TOLERANCE)
                .containsExactly(array(2.2f, 1.1f, 3.3f));
    }

    @Test
    void usingTolerance_containsExactly_primitiveFloatArray_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1f, TOLERABLE_2POINT2, 3.3f))
                        .usingTolerance(DEFAULT_TOLERANCE)
                        .containsExactly(array(2.2f, 1.1f)));
        assertFailureKeys(
                failure,
                "value of", "unexpected (1)", "---", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "unexpected (1)", Float.toString(3.3f));
    }

    @Test
    void usingTolerance_containsExactly_primitiveFloatArray_inOrder_success() {
        assertThat(array(1.1f, TOLERABLE_2POINT2, 3.3f))
                .usingTolerance(DEFAULT_TOLERANCE)
                .containsExactly(array(1.1f, 2.2f, 3.3f))
                .inOrder();
    }

    @Test
    void usingTolerance_containsExactly_primitiveFloatArray_inOrder_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1f, TOLERABLE_2POINT2, 3.3f))
                        .usingTolerance(DEFAULT_TOLERANCE)
                        .containsExactly(array(2.2f, 1.1f, 3.3f))
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
                "expected", String.format(Locale.ROOT, "[%.1f, %.1f, %.1f]", 2.2f, 1.1f, 3.3f));
    }

    @Test
    void usingTolerance_containsNoneOf_primitiveFloatArray_success() {
        assertThat(array(1.1f, TOLERABLE_2POINT2, 3.3f))
                .usingTolerance(DEFAULT_TOLERANCE)
                .containsNoneOf(array(99.99f, 999.999f));
    }

    @Test
    void usingTolerance_containsNoneOf_primitiveFloatArray_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1f, TOLERABLE_2POINT2, 3.3f))
                        .usingTolerance(DEFAULT_TOLERANCE)
                        .containsNoneOf(array(99.99f, 2.2f)));
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
                "expected not to contain any of", "[" + 99.99f + ", " + 2.2f + "]");
        assertFailureValue(
                failure,
                "but contained", "[" + TOLERABLE_2POINT2 + "]");
        assertFailureValue(
                failure,
                "corresponding to", Float.toString(2.2f));
    }

    @Test
    void usingExactEquality_contains_success() {
        assertThat(array(1.0f, 2.0f, 3.0f)).usingExactEquality().contains(2.0f);
    }

    @Test
    void usingExactEquality_contains_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1f, JUST_OVER_2POINT2, 3.3f))
                        .usingExactEquality()
                        .contains(2.2f));
        assertFailureKeys(
                failure,
                "value of", "expected to contain", "testing whether", "but was");
        assertFailureValue(
                failure,
                "expected to contain", Float.toString(2.2f));
        assertFailureValue(
                failure,
                "testing whether", "actual element is exactly equal to expected element");
        assertFailureValue(
                failure,
                "but was", "[" + 1.1f + ", " + JUST_OVER_2POINT2 + ", " + 3.3f + "]");
    }

    @Test
    void usingExactEquality_contains_otherTypes() {
        // Expected value is Integer - supported up to +/- 2^24
        assertThat(array(1.0f, 2.0f, 3.0f)).usingExactEquality().contains(2);
        assertThat(array(1.0f, 1 << 24, 3.0f)).usingExactEquality().contains(1 << 24);
        // Expected value is Long - supported up to +/- 2^24
        assertThat(array(1.0f, 2.0f, 3.0f)).usingExactEquality().contains(2L);
        assertThat(array(1.0f, 1 << 24, 3.0f)).usingExactEquality().contains(1L << 24);
    }

    @Test
    void usingExactEquality_contains_otherTypes_intOutOfRange() {
        int expected = (1 << 24) + 1;
        float[] actual = array(1.0f, 2.0f, 3.0f);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
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
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith(
                        "compare("
                                + actual[0]
                                + ", "
                                + expected
                                + ") threw java.lang.IllegalArgumentException");
        assertThatFailure(failure)
                .factValue("first exception")
                .contains(
                        "Expected value "
                                + expected
                                + " in assertion using exact float equality was an int with an absolute value "
                                + "greater than 2^24 which has no exact float representation");
    }

    @Test
    void usingExactEquality_contains_otherTypes_longOutOfRange() {
        long expected = (1L << 24) + 1L;
        float[] actual = array(1.0f, 2.0f, 3.0f);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
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
                .startsWith(
                        "compare("
                                + actual[0]
                                + ", "
                                + expected
                                + ") threw java.lang.IllegalArgumentException");
        assertThatFailure(failure)
                .factValue("first exception")
                .contains(
                        "Expected value "
                                + expected
                                + " in assertion using exact float equality was a long with an absolute value "
                                + "greater than 2^24 which has no exact float representation");
    }

    @Test
    void usingExactEquality_contains_otherTypes_doubleNotSupported() {
        double expected = 2.0;
        float[] actual = array(1.0f, 2.0f, 3.0f);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
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
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith(
                        "compare("
                                + actual[0]
                                + ", "
                                + expected
                                + ") threw java.lang.IllegalArgumentException");
        assertThatFailure(failure)
                .factValue("first exception")
                .contains(
                        "Expected value in assertion using exact float equality was a double, which is not "
                                + "supported as a double may not have an exact float representation");
    }

    @Test
    void usingExactEquality_contains_otherTypes_bigIntegerNotSupported() {
        BigInteger expected = BigInteger.valueOf(2);
        float[] actual = array(1.0f, 2.0f, 3.0f);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
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
                .startsWith(
                        "compare("
                                + actual[0]
                                + ", "
                                + expected
                                + ") threw java.lang.IllegalArgumentException");
        assertThatFailure(failure)
                .factValue("first exception")
                .contains(
                        "Expected value in assertion using exact float equality was of unsupported type "
                                + BigInteger.class
                                + " (it may not have an exact float representation)");
    }

    @Test
    void usingExactEquality_contains_otherTypes_bigDecimalNotSupported() {
        BigDecimal expected = BigDecimal.valueOf(2.0);
        float[] actual = array(1.0f, 2.0f, 3.0f);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
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
                .startsWith(
                        "compare("
                                + actual[0]
                                + ", "
                                + expected
                                + ") threw java.lang.IllegalArgumentException");
        assertThatFailure(failure)
                .factValue("first exception")
                .contains(
                        "Expected value in assertion using exact float equality was of unsupported type "
                                + BigDecimal.class
                                + " (it may not have an exact float representation)");
    }

    @Test
    void usingExactEquality_contains_successWithInfinity() {
        assertThat(array(1.0f, POSITIVE_INFINITY, 3.0f))
                .usingExactEquality()
                .contains(POSITIVE_INFINITY);
    }

    @Test
    void usingExactEquality_contains_successWithNaN() {
        assertThat(array(1.0f, NaN, 3.0f)).usingExactEquality().contains(NaN);
    }

    @Test
    void usingExactEquality_contains_failureWithNegativeZero() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.0f, -0.0f, 3.0f))
                        .usingExactEquality()
                        .contains(0.0f));
        assertFailureKeys(
                failure,
                "value of", "expected to contain", "testing whether", "but was");
        assertFailureValue(
                failure,
                "expected to contain", Float.toString(0.0f));
    }

    @Test
    void usingExactEquality_contains_nullExpected() {
        float[] actual = array(1.0f, 2.0f, 3.0f);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
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
                .startsWith("compare(" + actual[0] + ", null) threw java.lang.NullPointerException");
    }

    @Test
    void usingExactEquality_containsAtLeast_primitiveFloatArray_success() {
        assertThat(array(1.0f, 2.0f, 3.0f)).usingExactEquality().containsAtLeast(array(2.0f, 1.0f));
    }

    @Test
    void usingExactEquality_containsAtLeast_primitiveFloatArray_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1f, 2.2f, 3.3f))
                        .usingExactEquality()
                        .containsAtLeast(array(2.2f, 99.99f)));
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
                "missing (1)", Float.toString(99.99f));
    }

    @Test
    void usingExactEquality_containsAtLeast_primitiveFloatArray_inOrder_success() {
        assertThat(array(1.0f, 2.0f, 3.0f))
                .usingExactEquality()
                .containsAtLeast(array(1.0f, 2.0f))
                .inOrder();
    }

    @Test
    void usingExactEquality_containsAtLeast_primitiveFloatArray_inOrder_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1f, 2.2f, 3.3f))
                        .usingExactEquality()
                        .containsAtLeast(array(2.2f, 1.1f))
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
                "expected order for required elements", String.format(Locale.ROOT, "[%.1f, %.1f]", 2.2f, 1.1f));
    }

    @Test
    void usingExactEquality_containsAnyOf_primitiveFloatArray_success() {
        assertThat(array(1.0f, 2.0f, 3.0f)).usingExactEquality().containsAnyOf(array(99.99f, 2.0f));
    }

    @Test
    void usingExactEquality_containsAnyOf_primitiveFloatArray_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1f, 2.2f, 3.3f))
                        .usingExactEquality()
                        .containsAnyOf(array(99.99f, 999.999f)));
        assertFailureKeys(
                failure,
                "value of", "expected to contain any of", "testing whether", "but was");
    }

    @Test
    void usingExactEquality_containsExactly_primitiveFloatArray_success() {
        assertThat(array(1.0f, 2.0f, 3.0f))
                .usingExactEquality()
                .containsExactly(array(2.0f, 1.0f, 3.0f));
    }

    @Test
    void usingExactEquality_containsExactly_primitiveFloatArray_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1f, 2.2f, 3.3f))
                        .usingExactEquality()
                        .containsExactly(array(2.2f, 1.1f)));
        assertFailureKeys(
                failure,
                "value of", "unexpected (1)", "---", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "unexpected (1)", Float.toString(3.3f));
    }

    @Test
    void usingExactEquality_containsExactly_primitiveFloatArray_inOrder_success() {
        assertThat(array(1.0f, 2.0f, 3.0f))
                .usingExactEquality()
                .containsExactly(array(1.0f, 2.0f, 3.0f))
                .inOrder();
    }

    @Test
    void usingExactEquality_containsExactly_primitiveFloatArray_inOrder_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1f, 2.2f, 3.3f))
                        .usingExactEquality()
                        .containsExactly(array(2.2f, 1.1f, 3.3f))
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
                "expected", String.format(Locale.ROOT, "[%.1f, %.1f, %.1f]", 2.2f, 1.1f, 3.3f));
    }

    @Test
    void usingExactEquality_containsNoneOf_primitiveFloatArray_success() {
        assertThat(array(1.0f, 2.0f, 3.0f))
                .usingExactEquality()
                .containsNoneOf(array(99.99f, 999.999f));
    }

    @Test
    void usingExactEquality_containsNoneOf_primitiveFloatArray_failure() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(array(1.1f, 2.2f, 3.3f))
                        .usingExactEquality()
                        .containsNoneOf(array(99.99f, 2.2f)));
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
                "expected not to contain any of", "[" + 99.99f + ", " + 2.2f + "]");
        assertFailureValue(
                failure,
                "but contained", "[" + 2.2f + "]");
        assertFailureValue(
                failure,
                "corresponding to", Float.toString(2.2f));
    }

    private static float[] array(float... primitives) {
        return primitives;
    }
}
