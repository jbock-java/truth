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

import com.google.common.truth.TestCorrespondences.Record;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Functions.identity;
import static com.google.common.collect.Collections2.permutations;
import static com.google.common.truth.Correspondence.equality;
import static com.google.common.truth.Correspondence.tolerance;
import static com.google.common.truth.TestCorrespondences.CASE_INSENSITIVE_EQUALITY;
import static com.google.common.truth.TestCorrespondences.CASE_INSENSITIVE_EQUALITY_HALF_NULL_SAFE;
import static com.google.common.truth.TestCorrespondences.NULL_SAFE_RECORD_ID;
import static com.google.common.truth.TestCorrespondences.PARSED_RECORDS_EQUAL_WITH_SCORE_TOLERANCE_10;
import static com.google.common.truth.TestCorrespondences.PARSED_RECORD_ID;
import static com.google.common.truth.TestCorrespondences.RECORDS_EQUAL_WITH_SCORE_TOLERANCE_10;
import static com.google.common.truth.TestCorrespondences.RECORDS_EQUAL_WITH_SCORE_TOLERANCE_10_NO_DIFF;
import static com.google.common.truth.TestCorrespondences.RECORD_DIFF_FORMATTER;
import static com.google.common.truth.TestCorrespondences.RECORD_ID;
import static com.google.common.truth.TestCorrespondences.STRING_PARSES_TO_INTEGER_CORRESPONDENCE;
import static com.google.common.truth.TestCorrespondences.WITHIN_10_OF;
import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link IterableSubject} APIs that use {@link Correspondence}.
 *
 * <p>Note: Most of the assertions here call {@code assertThat(someIterable)} to get an {@link
 * IterableSubject}, and then call {@code comparingElementsUsing(someCorrespondence)} on that to get
 * an {@link IterableSubject.UsingCorrespondence}. The test method names omit the {@code
 * comparingElementsUsing_} prefix for brevity.
 *
 * @author Pete Gillin
 */
class IterableSubjectCorrespondenceTest extends BaseSubjectTestCase {

    @Test
    void contains_success() {
        List<String> actual = List.of("not a number", "+123", "+456", "+789");
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .contains(456);
    }

    @Test
    void contains_failure() {
        List<String> actual = List.of("not a number", "+123", "+456", "+789");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .contains(2345));
        assertFailureKeys(
                failure,
                "expected to contain", "testing whether", "but was");
        assertFailureValue(
                failure,
                "expected to contain", "2345");
        assertFailureValue(
                failure,
                "testing whether", "actual element parses to expected element");
        assertFailureValue(
                failure,
                "but was", "[not a number, +123, +456, +789]");
    }

    @Test
    void contains_handlesExceptions() {
        // CASE_INSENSITIVE_EQUALITY.compare throws on the null actual element.
        List<String> actual = asList("abc", null, "ghi");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(CASE_INSENSITIVE_EQUALITY)
                        .contains("DEF"));
        // We fail with the more helpful failure message about the missing value, not the NPE.
        assertFailureKeys(
                failure,
                "expected to contain",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing elements",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(null, DEF) threw java.lang.NullPointerException");
    }

    @Test
    void contains_handlesExceptions_alwaysFails() {
        List<String> actual = asList("abc", null, "ghi");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(CASE_INSENSITIVE_EQUALITY)
                        .contains("GHI"));
        // The actual list does contain the required match. However, no reasonable implementation would
        // find that mapping without hitting the null along the way, and that throws NPE, so we are
        // contractually required to fail.
        assertFailureKeys(
                failure,
                "one or more exceptions were thrown while comparing elements",
                "first exception",
                "expected to contain",
                "testing whether",
                "found match (but failing because of exception)",
                "full contents");
        assertFailureValue(
                failure,
                "found match (but failing because of exception)", "ghi");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(null, GHI) threw java.lang.NullPointerException");
    }

    @Test
    void displayingDiffsPairedBy_1arg_contains() {
        Record expected = Record.create(2, 200);
        List<Record> actual =
                List.of(
                        Record.create(1, 100),
                        Record.create(2, 211),
                        Record.create(4, 400),
                        Record.create(2, 189),
                        Record.createWithoutId(999));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(RECORDS_EQUAL_WITH_SCORE_TOLERANCE_10)
                        .displayingDiffsPairedBy(RECORD_ID)
                        .contains(expected));
        assertFailureKeys(
                failure,
                "expected to contain",
                "testing whether",
                "but did not",
                "though it did contain elements with correct key (2)",
                "#1",
                "diff",
                "#2",
                "diff",
                "---",
                "full contents");
        assertFailureValue(
                failure,
                "#1", "2/211");
        assertFailureValueIndexed(
                failure,
                "diff", 0, "score:11");
        assertFailureValue(
                failure,
                "#2", "2/189");
        assertFailureValueIndexed(
                failure,
                "diff", 1, "score:-11");
    }

    @Test
    void displayingDiffsPairedBy_1arg_contains_noDiff() {
        Record expected = Record.create(2, 200);
        List<Record> actual =
                List.of(
                        Record.create(1, 100),
                        Record.create(2, 211),
                        Record.create(4, 400),
                        Record.create(2, 189),
                        Record.createWithoutId(999));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(RECORDS_EQUAL_WITH_SCORE_TOLERANCE_10_NO_DIFF)
                        .displayingDiffsPairedBy(RECORD_ID)
                        .contains(expected));
        assertFailureKeys(
                failure,
                "expected to contain",
                "testing whether",
                "but did not",
                "though it did contain elements with correct key (2)",
                "---",
                "full contents");
        assertFailureValue(
                failure,
                "though it did contain elements with correct key (2)", "[2/211, 2/189]");
    }

    @Test
    void displayingDiffsPairedBy_1arg_contains_handlesActualKeyerExceptions() {
        Record expected = Record.create(0, 999);
        List<Record> actual = asList(Record.create(1, 100), null, Record.create(4, 400));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(RECORDS_EQUAL_WITH_SCORE_TOLERANCE_10)
                        .displayingDiffsPairedBy(RECORD_ID)
                        .contains(expected));
        assertFailureKeys(
                failure,
                "expected to contain",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while keying elements for pairing",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("actualKeyFunction.apply(null) threw java.lang.NullPointerException");
    }

    @Test
    void displayingDiffsPairedBy_1arg_contains_handlesExpectedKeyerExceptions() {
        List<Record> actual =
                asList(Record.create(1, 100), Record.create(2, 200), Record.create(4, 400));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(RECORDS_EQUAL_WITH_SCORE_TOLERANCE_10)
                        .displayingDiffsPairedBy(RECORD_ID)
                        .contains(null));
        assertFailureKeys(
                failure,
                "expected to contain",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while keying elements for pairing",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("expectedKeyFunction.apply(null) threw java.lang.NullPointerException");
    }

    @Test
    void displayingDiffsPairedBy_1arg_contains_handlesFormatDiffExceptions() {
        Record expected = Record.create(0, 999);
        List<Record> actual = asList(Record.create(1, 100), null, Record.create(4, 400));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(RECORDS_EQUAL_WITH_SCORE_TOLERANCE_10)
                        .displayingDiffsPairedBy(NULL_SAFE_RECORD_ID)
                        .contains(expected));
        assertFailureKeys(
                failure,
                "expected to contain",
                "testing whether",
                "but did not",
                "though it did contain elements with correct key (1)",
                "---",
                "full contents",
                "additionally, one or more exceptions were thrown while formatting diffs",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("formatDiff(null, 0/999) threw java.lang.NullPointerException");
    }

    @Test
    void contains_null() {
        List<String> actual = Arrays.asList("+123", null, "+789");
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .contains(null);
    }

    @Test
    void wrongTypeInActual() {
        List<?> actual = List.of("valid", 123);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .contains(456));
        assertFailureKeys(
                failure,
                "expected to contain",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing elements",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(123, 456) threw java.lang.ClassCastException");
    }

    @Test
    void doesNotContain_success() {
        List<String> actual = List.of("not a number", "+123", "+456", "+789");
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .doesNotContain(2345);
    }

    @Test
    void doesNotContains_failure() {
        List<String> actual = List.of("not a number", "+123", "+456", "+789");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .doesNotContain(456));
        assertFailureKeys(
                failure,
                "expected not to contain", "testing whether", "but contained", "full contents");
        assertFailureValue(
                failure,
                "expected not to contain", "456");
        assertFailureValue(
                failure,
                "but contained", "[+456]");
    }

    @Test
    void doesNotContain_handlesExceptions() {
        // CASE_INSENSITIVE_EQUALITY.compare throws on the null actual element.
        List<String> actual = asList("abc", null, "ghi");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(CASE_INSENSITIVE_EQUALITY)
                        .doesNotContain("GHI"));
        // We fail with the more helpful failure message about the unexpected value, not the NPE.
        assertFailureKeys(
                failure,
                "expected not to contain",
                "testing whether",
                "but contained",
                "full contents",
                "additionally, one or more exceptions were thrown while comparing elements",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(null, GHI) threw java.lang.NullPointerException");
    }

    @Test
    void doesNotContain_handlesExceptions_alwaysFails() {
        List<String> actual = asList("abc", null, "ghi");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(CASE_INSENSITIVE_EQUALITY)
                        .doesNotContain("DEF"));
        // The actual list does not contain the forbidden match. However, we cannot establish that
        // without hitting the null along the way, and that throws NPE, so we are contractually required
        // to fail.
        assertFailureKeys(
                failure,
                "one or more exceptions were thrown while comparing elements",
                "first exception",
                "expected not to contain",
                "testing whether",
                "found no match (but failing because of exception)",
                "full contents");
        assertFailureValue(
                failure,
                "expected not to contain", "DEF");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(null, DEF) threw java.lang.NullPointerException");
        assertFailureValue(
                failure,
                "expected not to contain", "DEF");
    }

    @Test
    void containsExactlyElementsIn_inOrder_success() {
        List<Integer> expected = List.of(64, 128, 256, 128);
        List<String> actual = List.of("+64", "+128", "+256", "0x80");
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactlyElementsIn(expected)
                .inOrder();
    }

    @Test
    void containsExactlyElementsIn_successOutOfOrder() {
        List<Integer> expected = List.of(64, 128, 256, 128);
        List<String> actual = List.of("+128", "+64", "0x80", "+256");
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactlyElementsIn(expected);
    }

    @Test
    void containsExactlyElementsIn_outOfOrderDoesNotStringify() {
        CountsToStringCalls o = new CountsToStringCalls();
        List<Object> actual = asList(o, 1);
        List<Object> expected = asList(1, o);
        assertThat(actual).comparingElementsUsing(equality()).containsExactlyElementsIn(expected);
        assertThat(o.calls).isEqualTo(0);
        assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(equality())
                        .containsExactlyElementsIn(expected)
                        .inOrder());
        assertThat(o.calls).isGreaterThan(0);
    }

    @Test
    void containsExactlyElementsIn_successNonGreedy() {
        // (We use doubles with approximate equality for this test, because we can't illustrate this
        // case with the string parsing correspondence used in the other tests, because one string
        // won't parse to more than one integer.)
        List<Double> expected = List.of(1.0, 1.1, 1.2);
        List<Double> actual = List.of(1.05, 1.15, 0.95);
        // The comparingElementsUsing test with a tolerance of 0.1 should succeed by pairing 1.0 with
        // 0.95, 1.1 with 1.05, and 1.2 with 1.15. A left-to-right greedy implementation would fail as
        // it would pair 1.0 with 1.05 and 1.1 with 1.15, and fail to pair 1.2 with 0.95. Check that the
        // implementation is truly non-greedy by testing all permutations.
        for (List<Double> permutedActual : permutations(actual)) {
            assertThat(permutedActual)
                    .comparingElementsUsing(tolerance(0.1))
                    .containsExactlyElementsIn(expected);
        }
    }

    @Test
    void containsExactlyElementsIn_failsMissingOneCandidate() {
        List<Integer> expected = List.of(64, 128, 256, 128);
        List<String> actual = List.of("+64", "+128", "0x40", "0x80");
        // Actual list has candidate matches for 64, 128, and the other 128, but is missing 256.
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyElementsIn(expected));
        assertFailureKeys(
                failure,
                "missing (1)", "---", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "missing (1)", "256");
        assertFailureValue(
                failure,
                "expected", "[64, 128, 256, 128]");
        assertFailureValue(
                failure,
                "testing whether", "actual element parses to expected element");
        assertFailureValue(
                failure,
                "but was", "[+64, +128, 0x40, 0x80]");
    }

    @Test
    void containsExactlyElementsIn_inOrder_passesWhenBothEmpty() {
        List<Integer> expected = List.of();
        List<String> actual = List.of();
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactlyElementsIn(expected)
                .inOrder();
    }

    @Test
    void containsExactlyElementsIn_failsExpectedIsEmpty() {
        List<Integer> expected = List.of();
        List<String> actual = List.of("+64", "+128", "0x40", "0x80");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyElementsIn(expected));
        assertFailureKeys(
                failure,
                "expected to be empty", "but was");
    }

    @Test
    void containsExactlyElementsIn_failsMultipleMissingCandidates() {
        List<Integer> expected = List.of(64, 128, 256, 128);
        List<String> actual = List.of("+64", "+64", "0x40", "0x40");
        // Actual list has candidate matches for 64 only, and is missing 128, 256, and the other 128.
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyElementsIn(expected));
        assertFailureKeys(
                failure,
                "missing (3)", "---", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "missing (3)", "128 [2 copies], 256");
    }

    @Test
    void containsExactlyElementsIn_failsOrderedMissingOneCandidate() {
        List<Integer> expected = List.of(64, 128, 256, 512);
        List<String> actual = List.of("+64", "+128", "+256");
        // Actual list has in-order candidate matches for 64, 128, and 256, but is missing 512.
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyElementsIn(expected));
        assertFailureKeys(
                failure,
                "missing (1)", "---", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "missing (1)", "512");
    }

    @Test
    void containsExactlyElementsIn_failsExtraCandidates() {
        List<Integer> expected = List.of(64, 128, 256, 128);
        List<String> actual = List.of("+64", "+128", "+256", "cheese");
        // Actual list has candidate matches for all the expected, but has extra cheese.
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyElementsIn(expected));
        assertFailureKeys(
                failure,
                "unexpected (1)", "---", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "unexpected (1)", "cheese");
    }

    @Test
    void containsExactlyElementsIn_failsOrderedExtraCandidates() {
        List<Integer> expected = List.of(64, 128, 256, 128);
        List<String> actual = List.of("+64", "+128", "+256", "0x80", "cheese");
        // Actual list has in-order candidate matches for all the expected, but has extra cheese.
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyElementsIn(expected));
        assertFailureKeys(
                failure,
                "unexpected (1)", "---", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "unexpected (1)", "cheese");
    }

    @Test
    void containsExactlyElementsIn_failsMissingAndExtraCandidates() {
        List<Integer> expected = List.of(64, 128, 256, 128);
        List<String> actual = List.of("+64", "+128", "jalapenos", "cheese");
        // Actual list has candidate matches for 64, 128, and the other 128, but is missing 256 and has
        // extra jalapenos and cheese.
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyElementsIn(expected));
        assertFailureKeys(
                failure,
                "missing (1)", "unexpected (2)", "---", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "missing (1)", "256");
        assertFailureValue(
                failure,
                "unexpected (2)", "[jalapenos, cheese]");
    }

    @Test
    void containsExactlyElementsIn_failsMissingAndExtraNull() {
        List<Integer> expected = List.of(64, 128, 256, 128);
        List<String> actual = asList("+64", "+128", "0x80", null);
        // Actual list has candidate matches for 64, 128, and the other 128, but is missing 256 and has
        // extra null. (N.B. This tests a previous regression from calling extra.toString().)
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyElementsIn(expected));
        assertFailureKeys(
                failure,
                "missing (1)", "unexpected (1)", "---", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "missing (1)", "256");
        assertFailureValue(
                failure,
                "unexpected (1)", "[null]");
    }

    @Test
    void containsExactlyElementsIn_failsNullMissingAndExtra() {
        List<Integer> expected = asList(64, 128, null, 128);
        List<String> actual = List.of("+64", "+128", "0x80", "cheese");
        // Actual list has candidate matches for 64, 128, and the other 128, but is missing null and has
        // extra cheese.
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyElementsIn(expected));
        assertFailureKeys(
                failure,
                "missing (1)", "unexpected (1)", "---", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "missing (1)", "null");
        assertFailureValue(
                failure,
                "unexpected (1)", "[cheese]");
    }

    @Test
    void containsExactlyElementsIn_handlesExceptions() {
        List<String> expected = List.of("ABC", "DEF", "GHI", "JKL");
        // CASE_INSENSITIVE_EQUALITY.compare throws on the null actual element.
        List<String> actual = asList(null, "xyz", "abc", "def");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(CASE_INSENSITIVE_EQUALITY)
                        .containsExactlyElementsIn(expected));
        // We fail with the more helpful failure message about the mis-matched values, not the NPE.
        assertFailureKeys(
                failure,
                "missing (2)",
                "unexpected (2)",
                "---",
                "expected",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing elements",
                "first exception");
        assertFailureValue(
                failure,
                "missing (2)", "GHI, JKL");
        assertFailureValue(
                failure,
                "unexpected (2)", "null, xyz");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(null, ABC) threw java.lang.NullPointerException");
    }

    @Test
    void containsExactlyElementsIn_handlesExceptions_alwaysFails() {
        List<String> expected = asList("ABC", "DEF", "GHI", null);
        List<String> actual = asList(null, "def", "ghi", "abc");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(CASE_INSENSITIVE_EQUALITY_HALF_NULL_SAFE)
                        .containsExactlyElementsIn(expected));
        // CASE_INSENSITIVE_EQUALITY_HALF_NULL_SAFE.compare(null, null) returns true, so there is a
        // mapping between actual and expected elements where they all correspond. However, no
        // reasonable implementation would find that mapping without hitting the (null, "ABC") case
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
                .startsWith("compare(null, ABC) threw java.lang.NullPointerException");
    }

    @Test
    void containsExactlyElementsIn_diffOneMissingSomeExtraCandidate() {
        List<Integer> expected = List.of(30, 60, 90);
        List<Integer> actual = List.of(101, 65, 35, 190);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(WITHIN_10_OF)
                        .containsExactlyElementsIn(expected));
        assertFailureKeys(
                failure,
                "missing (1)",
                "unexpected (2)",
                "#1",
                "diff",
                "#2",
                "diff",
                "---",
                "expected",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "missing (1)", "90");
        assertFailureValue(
                failure,
                "#1", "101");
        assertFailureValueIndexed(
                failure,
                "diff", 0, "11");
        assertFailureValue(
                failure,
                "#2", "190");
        assertFailureValueIndexed(
                failure,
                "diff", 1, "100");
    }

    @Test
    void displayingDiffsPairedBy_1arg_containsExactlyElementsIn() {
        List<Record> expected =
                List.of(
                        Record.create(1, 100),
                        Record.create(2, 200),
                        Record.create(3, 300),
                        Record.createWithoutId(900));
        List<Record> actual =
                List.of(
                        Record.create(1, 100),
                        Record.create(2, 211),
                        Record.create(4, 400),
                        Record.createWithoutId(999));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(RECORDS_EQUAL_WITH_SCORE_TOLERANCE_10)
                        .displayingDiffsPairedBy(RECORD_ID)
                        .containsExactlyElementsIn(expected));
        assertFailureKeys(
                failure,
                "for key",
                "missing",
                "unexpected (1)",
                "#1",
                "diff",
                "---",
                "elements without matching keys:",
                "missing (2)",
                "unexpected (2)",
                "---",
                "expected",
                "testing whether",
                "but was");
        // the key=2 values:
        assertFailureValue(
                failure,
                "for key", "2");
        assertFailureValue(
                failure,
                "missing", "2/200");
        assertFailureValue(
                failure,
                "#1", "2/211");
        assertFailureValue(
                failure,
                "diff", "score:11");
        // the values without matching keys:
        assertFailureValue(
                failure,
                "missing (2)", "3/300, none/900");
        assertFailureValue(
                failure,
                "unexpected (2)", "4/400, none/999");
    }

    @Test
    void displayingDiffsPairedBy_2arg_containsExactlyElementsIn() {
        List<Record> expected =
                List.of(
                        Record.create(1, 100),
                        Record.create(2, 200),
                        Record.create(3, 300),
                        Record.createWithoutId(900));
        List<String> actual = List.of("1/100", "2/211", "4/400", "none/999");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(PARSED_RECORDS_EQUAL_WITH_SCORE_TOLERANCE_10)
                        .displayingDiffsPairedBy(PARSED_RECORD_ID, RECORD_ID)
                        .containsExactlyElementsIn(expected));
        assertFailureKeys(
                failure,
                "for key",
                "missing",
                "unexpected (1)",
                "#1",
                "diff",
                "---",
                "elements without matching keys:",
                "missing (2)",
                "unexpected (2)",
                "---",
                "expected",
                "testing whether",
                "but was");
        // the key=2 values:
        assertFailureValue(
                failure,
                "for key", "2");
        assertFailureValue(
                failure,
                "missing", "2/200");
        assertFailureValue(
                failure,
                "#1", "2/211");
        assertFailureValue(
                failure,
                "diff", "score:11");
        // the values without matching keys:
        assertFailureValue(
                failure,
                "missing (2)", "3/300, none/900");
        assertFailureValue(
                failure,
                "unexpected (2)", "4/400, none/999");
    }

    @Test
    void displayingDiffsPairedBy_containsExactlyElementsIn_onlyKeyed() {
        List<Record> expected =
                List.of(
                        Record.create(1, 100),
                        Record.create(2, 200),
                        Record.create(3, 300),
                        Record.createWithoutId(999));
        List<Record> actual =
                List.of(
                        Record.create(1, 100),
                        Record.create(2, 211),
                        Record.create(3, 303),
                        Record.createWithoutId(999));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(RECORDS_EQUAL_WITH_SCORE_TOLERANCE_10)
                        .displayingDiffsPairedBy(RECORD_ID)
                        .containsExactlyElementsIn(expected));
        assertFailureKeys(
                failure,
                "for key",
                "missing",
                "unexpected (1)",
                "#1",
                "diff",
                "---",
                "expected",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "for key", "2");
        assertFailureValue(
                failure,
                "missing", "2/200");
        assertFailureValue(
                failure,
                "#1", "2/211");
        assertFailureValue(
                failure,
                "diff", "score:11");
    }

    @Test
    void displayingDiffsPairedBy_containsExactlyElementsIn_noKeyed() {
        List<Record> expected =
                List.of(
                        Record.create(1, 100),
                        Record.create(2, 200),
                        Record.create(3, 300),
                        Record.createWithoutId(900));
        List<Record> actual =
                List.of(
                        Record.create(1, 100),
                        Record.create(2, 201),
                        Record.create(4, 400),
                        Record.createWithoutId(999));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(RECORDS_EQUAL_WITH_SCORE_TOLERANCE_10)
                        .displayingDiffsPairedBy(RECORD_ID)
                        .containsExactlyElementsIn(expected));
        assertFailureKeys(
                failure,
                "elements without matching keys:",
                "missing (2)",
                "unexpected (2)",
                "---",
                "expected",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "missing (2)", "3/300, none/900");
        assertFailureValue(
                failure,
                "unexpected (2)", "4/400, none/999");
    }

    @Test
    void displayingDiffsPairedBy_containsExactlyElementsIn_noDiffs() {
        List<Record> expected =
                List.of(
                        Record.create(1, 100),
                        Record.create(2, 200),
                        Record.create(3, 300),
                        Record.createWithoutId(999));
        List<Record> actual =
                List.of(
                        Record.create(1, 100),
                        Record.create(2, 211),
                        Record.create(3, 303),
                        Record.createWithoutId(999));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(RECORDS_EQUAL_WITH_SCORE_TOLERANCE_10_NO_DIFF)
                        .displayingDiffsPairedBy(RECORD_ID)
                        .containsExactlyElementsIn(expected));
        assertFailureKeys(
                failure,
                "for key", "missing", "unexpected (1)", "---", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "missing", "2/200");
        assertFailureValue(
                failure,
                "unexpected (1)", "[2/211]");
    }

    @Test
    void displayingDiffsPairedBy_containsExactlyElementsIn_passing() {
        // The contract on displayingDiffsPairedBy requires that it should not affect whether the test
        // passes or fails. This test asserts that a test which would pass on the basis of its
        // correspondence still passes even if the user specifies a key function such that none of the
        // elements match by key. (We advise against assertions where key function equality is stricter
        // than correspondence, but we should still do the thing we promised we'd do in that case.)
        List<Double> expected = List.of(1.0, 1.1, 1.2);
        List<Double> actual = List.of(1.05, 1.15, 0.95);
        assertThat(actual)
                .comparingElementsUsing(tolerance(0.1))
                .displayingDiffsPairedBy(identity())
                .containsExactlyElementsIn(expected);
    }

    @Test
    void displayingDiffsPairedBy_containsExactlyElementsIn_notUnique() {
        // The missing elements here are not uniquely keyed by the key function, so the key function
        // should be ignored, but a warning about this should be appended to the failure message.
        List<Record> expected =
                List.of(
                        Record.create(1, 100),
                        Record.create(2, 200),
                        Record.create(3, 300),
                        Record.create(3, 301),
                        Record.createWithoutId(900));
        List<Record> actual =
                List.of(
                        Record.create(1, 100),
                        Record.create(2, 211),
                        Record.create(4, 400),
                        Record.createWithoutId(999));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(RECORDS_EQUAL_WITH_SCORE_TOLERANCE_10)
                        .displayingDiffsPairedBy(RECORD_ID)
                        .containsExactlyElementsIn(expected));
        assertFailureKeys(
                failure,
                "missing (4)",
                "unexpected (3)",
                "---",
                "a key function which does not uniquely key the expected elements was provided and has"
                        + " consequently been ignored",
                "expected",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "missing (4)", "2/200, 3/300, 3/301, none/900");
        assertFailureValue(
                failure,
                "unexpected (3)", "2/211, 4/400, none/999");
    }

    @Test
    void displayingDiffsPairedBy_containsExactlyElementsIn_handlesActualKeyerExceptions() {
        List<Record> expected =
                List.of(Record.create(1, 100), Record.create(2, 200), Record.create(4, 400));
        List<Record> actual = asList(Record.create(1, 101), Record.create(2, 211), null);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(RECORDS_EQUAL_WITH_SCORE_TOLERANCE_10)
                        .displayingDiffsPairedBy(RECORD_ID)
                        .containsExactlyElementsIn(expected));
        assertFailureKeys(
                failure,
                "for key",
                "missing",
                "unexpected (1)",
                "#1",
                "diff",
                "---",
                "elements without matching keys:",
                "missing (1)",
                "unexpected (1)",
                "---",
                "expected",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while keying elements for pairing",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("actualKeyFunction.apply(null) threw java.lang.NullPointerException");
    }

    @Test
    void displayingDiffsPairedBy_containsExactlyElementsIn_handlesExpectedKeyerExceptions() {
        List<Record> expected = asList(Record.create(1, 100), Record.create(2, 200), null);
        List<Record> actual =
                asList(Record.create(1, 101), Record.create(2, 211), Record.create(4, 400));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(RECORDS_EQUAL_WITH_SCORE_TOLERANCE_10)
                        .displayingDiffsPairedBy(RECORD_ID)
                        .containsExactlyElementsIn(expected));
        assertFailureKeys(
                failure,
                "for key",
                "missing",
                "unexpected (1)",
                "#1",
                "diff",
                "---",
                "elements without matching keys:",
                "missing (1)",
                "unexpected (1)",
                "---",
                "expected",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while keying elements for pairing",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("expectedKeyFunction.apply(null) threw java.lang.NullPointerException");
    }

    @Test
    void displayingDiffsPairedBy_containsExactlyElementsIn_handlesFormatDiffExceptions() {
        List<Record> expected =
                List.of(Record.create(1, 100), Record.create(2, 200), Record.create(0, 999));
        List<Record> actual = asList(Record.create(1, 101), Record.create(2, 211), null);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(RECORDS_EQUAL_WITH_SCORE_TOLERANCE_10)
                        .displayingDiffsPairedBy(NULL_SAFE_RECORD_ID)
                        .containsExactlyElementsIn(expected));
        assertFailureKeys(
                failure,
                "for key",
                "missing",
                "unexpected (1)",
                "#1",
                "diff",
                "---",
                "for key",
                "missing",
                "unexpected (1)",
                "---",
                "expected",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while formatting diffs",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("formatDiff(null, 0/999) threw java.lang.NullPointerException");
    }

    @Test
    void containsExactlyElementsIn_failsMissingElementInOneToOne() {
        List<Integer> expected = List.of(64, 128, 256, 128);
        List<String> actual = List.of("+128", "+64", "+256");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyElementsIn(expected));
        assertFailureKeys(
                failure,
                "in an assertion requiring a 1:1 mapping between the expected and the actual elements,"
                        + " each actual element matches as least one expected element, and vice versa, but"
                        + " there was no 1:1 mapping",
                "using the most complete 1:1 mapping (or one such mapping, if there is a tie)",
                "missing (1)",
                "---",
                "expected",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "missing (1)", "128");
    }

    @Test
    void containsExactlyElementsIn_failsExtraElementInOneToOne() {
        List<Integer> expected = List.of(64, 128, 256, 128);
        List<String> actual = List.of("+128", "+64", "+256", "0x80", "0x40");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyElementsIn(expected));
        assertFailureKeys(
                failure,
                "in an assertion requiring a 1:1 mapping between the expected and the actual elements,"
                        + " each actual element matches as least one expected element, and vice versa, but"
                        + " there was no 1:1 mapping",
                "using the most complete 1:1 mapping (or one such mapping, if there is a tie)",
                "unexpected (1)",
                "---",
                "expected",
                "testing whether",
                "but was");
        assertThatFailure(failure).factValue("unexpected (1)").isAnyOf("+64", "0x40");
    }

    @Test
    void containsExactlyElementsIn_failsMissingAndExtraInOneToOne() {
        List<Integer> expected = List.of(64, 128, 256, 128);
        List<String> actual = List.of("+128", "+64", "+256", "0x40");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyElementsIn(expected));
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
                "missing (1)", "128");
        assertThatFailure(failure).factValue("unexpected (1)").isAnyOf("[+64]", "[0x40]");
    }

    @Test
    void containsExactlyElementsIn_diffOneMissingAndExtraInOneToOne() {
        List<Integer> expected = List.of(30, 30, 60);
        List<Integer> actual = List.of(25, 55, 65);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(WITHIN_10_OF)
                        .containsExactlyElementsIn(expected));
        assertFailureKeys(
                failure,
                "in an assertion requiring a 1:1 mapping between the expected and the actual elements,"
                        + " each actual element matches as least one expected element, and vice versa, but"
                        + " there was no 1:1 mapping",
                "using the most complete 1:1 mapping (or one such mapping, if there is a tie)",
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
                "missing (1)", "30");
        assertThatFailure(failure).factValue("#1").isAnyOf("55", "65");
        assertThatFailure(failure).factValue("diff").isAnyOf("25", "35");
    }

    @Test
    void containsExactlyElementsIn_inOrder_failsOutOfOrder() {
        List<Integer> expected = List.of(64, 128, 256, 128);
        List<String> actual = List.of("+128", "+64", "0x80", "+256");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyElementsIn(expected)
                        .inOrder());
        assertFailureKeys(
                failure,
                "contents match, but order was wrong", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "expected", "[64, 128, 256, 128]");
    }

    @Test
    void containsExactlyElementsIn_null() {
        List<Integer> expected = Arrays.asList(128, null);
        List<String> actual = Arrays.asList(null, "0x80");
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactlyElementsIn(expected);
    }

    @Test
    void containsExactlyElementsIn_array() {
        Integer[] expected = new Integer[]{64, 128, 256, 128};
        assertThat(List.of("+128", "+64", "0x80", "+256"))
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactlyElementsIn(expected);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(List.of("+64", "+128", "0x40", "0x80"))
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactlyElementsIn(expected));
        assertFailureKeys(
                failure,
                "missing (1)", "---", "expected", "testing whether", "but was");
        assertFailureValue(
                failure,
                "missing (1)", "256");
    }

    @Test
    void containsExactly_inOrder_success() {
        List<String> actual = List.of("+64", "+128", "+256", "0x80");
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactly(64, 128, 256, 128)
                .inOrder();
    }

    @Test
    void containsExactly_successOutOfOrder() {
        List<String> actual = List.of("+128", "+64", "0x80", "+256");
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactly(64, 128, 256, 128);
    }

    @Test
    void containsExactly_failsMissingAndExtraInOneToOne() {
        List<String> actual = List.of("+128", "+64", "+256", "0x40");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsExactly(64, 128, 256, 128));
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
                "missing (1)", "128");
        assertThatFailure(failure).factValue("unexpected (1)").isAnyOf("[+64]", "[0x40]");
    }

    @Test
    void containsExactly_nullValueInArray() {
        List<String> actual = Arrays.asList(null, "0x80");
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactly(128, null);
    }

    @Test
    void containsExactly_nullArray() {
        // Truth is tolerant of this erroneous varargs call.
        List<String> actual = Arrays.asList((String) null);
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsExactly((Integer[]) null)
                .inOrder();
    }

    @Test
    void containsAtLeastElementsIn() {
        List<Integer> expected = List.of(64, 128, 256, 128);
        List<String> actual =
                List.of("fee", "+64", "+128", "fi", "fo", "+256", "0x80", "fum");
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeastElementsIn(expected)
                .inOrder();
    }

    @Test
    void containsAtLeastElementsIn_inOrder_success() {
        List<Integer> expected = List.of(64, 128, 256, 128);
        List<String> actual =
                List.of("fee", "+64", "+128", "fi", "fo", "+256", "0x80", "fum");
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeastElementsIn(expected)
                .inOrder();
    }

    @Test
    void containsAtLeastElementsIn_successOutOfOrder() {
        List<Integer> expected = List.of(64, 128, 256, 128);
        List<String> actual =
                List.of("fee", "+128", "+64", "fi", "fo", "0x80", "+256", "fum");
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeastElementsIn(expected);
    }

    @Test
    void containsAtLeastElementsIn_outOfOrderDoesNotStringify() {
        CountsToStringCalls o = new CountsToStringCalls();
        List<Object> actual = asList(o, 1);
        List<Object> expected = asList(1, o);
        assertThat(actual).comparingElementsUsing(equality()).containsAtLeastElementsIn(expected);
        assertThat(o.calls).isEqualTo(0);
        assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(equality())
                        .containsAtLeastElementsIn(expected)
                        .inOrder());
        assertThat(o.calls).isGreaterThan(0);
    }

    @Test
    void containsAtLeastElementsIn_successNonGreedy() {
        // (We use doubles with approximate equality for this test, because we can't illustrate this
        // case with the string parsing correspondence used in the other tests, because one string
        // won't parse to more than one integer.)
        List<Double> expected = List.of(1.0, 1.1, 1.2);
        List<Double> actual = List.of(99.999, 1.05, 99.999, 1.15, 0.95, 99.999);
        // The comparingElementsUsing test with a tolerance of 0.1 should succeed by pairing 1.0 with
        // 0.95, 1.1 with 1.05, and 1.2 with 1.15. A left-to-right greedy implementation would fail as
        // it would pair 1.0 with 1.05 and 1.1 with 1.15, and fail to pair 1.2 with 0.95. Check that the
        // implementation is truly non-greedy by testing all permutations.
        for (List<Double> permutedActual : permutations(actual)) {
            assertThat(permutedActual)
                    .comparingElementsUsing(tolerance(0.1))
                    .containsAtLeastElementsIn(expected);
        }
    }

    @Test
    void containsAtLeastElementsIn_failsMissingOneCandidate() {
        List<Integer> expected = List.of(64, 128, 256, 128);
        List<String> actual =
                List.of("fee", "+64", "+128", "fi", "fo", "0x40", "0x80", "fum");
        // Actual list has candidate matches for 64, 128, and the other 128, but is missing 256.
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeastElementsIn(expected));
        assertFailureKeys(
                failure,
                "missing (1)", "---", "expected to contain at least", "testing whether", "but was");
        assertFailureValue(
                failure,
                "missing (1)", "256");
        assertFailureValue(
                failure,
                "expected to contain at least", "[64, 128, 256, 128]");
        assertFailureValue(
                failure,
                "testing whether", "actual element parses to expected element");
        assertFailureValue(
                failure,
                "but was", "[fee, +64, +128, fi, fo, 0x40, 0x80, fum]");
    }

    @Test
    void containsAtLeastElementsIn_handlesExceptions() {
        List<String> expected = List.of("ABC", "DEF", "GHI");
        // CASE_INSENSITIVE_EQUALITY.compare throws on the null actual element.
        List<String> actual = asList(null, "xyz", "abc", "ghi");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(CASE_INSENSITIVE_EQUALITY)
                        .containsAtLeastElementsIn(expected));
        // We fail with the more helpful failure message about the mis-matched values, not the NPE.
        assertFailureKeys(
                failure,
                "missing (1)",
                "---",
                "expected to contain at least",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing elements",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(null, ABC) threw java.lang.NullPointerException");
    }

    @Test
    void containsAtLeastElementsIn_handlesExceptions_alwaysFails() {
        List<String> expected = asList("ABC", "DEF", null);
        List<String> actual = asList(null, "def", "ghi", "abc");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(CASE_INSENSITIVE_EQUALITY_HALF_NULL_SAFE)
                        .containsAtLeastElementsIn(expected));
        // CASE_INSENSITIVE_EQUALITY_HALF_NULL_SAFE.compare(null, null) returns true, so there is a
        // mapping between actual and expected elements which includes all the expected. However, no
        // reasonable implementation would find that mapping without hitting the (null, "ABC") case
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
                .startsWith("compare(null, ABC) threw java.lang.NullPointerException");
    }

    @Test
    void displayingElementsPairedBy_containsAtLeastElementsIn() {
        List<Record> expected =
                List.of(Record.create(1, 100), Record.create(2, 200), Record.createWithoutId(999));
        List<Record> actual =
                List.of(
                        Record.create(1, 101),
                        Record.create(2, 211),
                        Record.create(2, 222),
                        Record.create(3, 303),
                        Record.createWithoutId(888));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(RECORDS_EQUAL_WITH_SCORE_TOLERANCE_10)
                        .displayingDiffsPairedBy(RECORD_ID)
                        .containsAtLeastElementsIn(expected));
        assertFailureKeys(
                failure,
                "for key",
                "missing",
                "did contain elements with that key (2)",
                "#1",
                "diff",
                "#2",
                "diff",
                "---",
                "elements without matching keys:",
                "missing (1)",
                "---",
                "expected to contain at least",
                "testing whether",
                "but was");
        // values at key 2:
        assertFailureValue(
                failure,
                "for key", "2");
        assertFailureValue(
                failure,
                "missing", "2/200");
        assertFailureValue(
                failure,
                "#1", "2/211");
        assertFailureValueIndexed(
                failure,
                "diff", 0, "score:11");
        assertFailureValue(
                failure,
                "#2", "2/222");
        assertFailureValueIndexed(
                failure,
                "diff", 1, "score:22");
        // values without matching keys:
        assertFailureValue(
                failure,
                "missing (1)", "none/999");
    }

    @Test
    void displayingElementsPairedBy_containsAtLeastElementsIn_notUnique() {
        List<Record> expected =
                List.of(
                        Record.create(1, 100),
                        Record.create(2, 200),
                        Record.create(2, 201),
                        Record.createWithoutId(999));
        List<Record> actual =
                List.of(Record.create(1, 101), Record.create(3, 303), Record.createWithoutId(999));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(RECORDS_EQUAL_WITH_SCORE_TOLERANCE_10)
                        .displayingDiffsPairedBy(RECORD_ID)
                        .containsAtLeastElementsIn(expected));
        assertFailureKeys(
                failure,
                "missing (2)",
                "---",
                "a key function which does not uniquely key the expected elements was provided and has"
                        + " consequently been ignored",
                "expected to contain at least",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "missing (2)", "2/200, 2/201");
    }

    @Test
    void displayingElementsPairedBy_containsAtLeastElementsIn_handlesFormatDiffExceptions() {
        List<Record> expected =
                List.of(Record.create(1, 100), Record.create(2, 200), Record.create(0, 999));
        List<Record> actual =
                asList(Record.create(1, 101), Record.create(2, 211), Record.create(3, 303), null);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(RECORDS_EQUAL_WITH_SCORE_TOLERANCE_10)
                        .displayingDiffsPairedBy(NULL_SAFE_RECORD_ID)
                        .containsAtLeastElementsIn(expected));
        assertFailureKeys(
                failure,
                "for key",
                "missing",
                "did contain elements with that key (1)",
                "#1",
                "diff",
                "---",
                "for key",
                "missing",
                "did contain elements with that key (1)",
                "---",
                "expected to contain at least",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while formatting diffs",
                "first exception");
        assertFailureValueIndexed(
                failure,
                "for key", 0, "2");
        assertFailureValueIndexed(
                failure,
                "missing", 0, "2/200");
        assertFailureValue(
                failure,
                "#1", "2/211");
        assertFailureValue(
                failure,
                "diff", "score:11");
        assertFailureValueIndexed(
                failure,
                "for key", 1, "0");
        assertFailureValueIndexed(
                failure,
                "missing", 1, "0/999");
        assertFailureValueIndexed(
                failure,
                "did contain elements with that key (1)", 1, "[null]");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("formatDiff(null, 0/999) threw java.lang.NullPointerException");
    }

    @Test
    void containsAtLeastElementsIn_failsMultipleMissingCandidates() {
        List<Integer> expected = List.of(64, 128, 256, 128);
        List<String> actual =
                List.of("fee", "+64", "+64", "fi", "fo", "0x40", "0x40", "fum");
        // Actual list has candidate matches for 64 only, and is missing 128, 256, and the other 128.
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeastElementsIn(expected));
        assertFailureKeys(
                failure,
                "missing (3)", "---", "expected to contain at least", "testing whether", "but was");
        assertFailureValue(
                failure,
                "missing (3)", "128 [2 copies], 256");
    }

    @Test
    void containsAtLeastElementsIn_failsOrderedMissingOneCandidate() {
        List<Integer> expected = List.of(64, 128, 256, 512);
        List<String> actual =
                List.of("fee", "+64", "fi", "fo", "+128", "+256", "fum");
        // Actual list has in-order candidate matches for 64, 128, and 256, but is missing 512.
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeastElementsIn(expected));
        assertFailureKeys(
                failure,
                "missing (1)", "---", "expected to contain at least", "testing whether", "but was");
        assertFailureValue(
                failure,
                "missing (1)", "512");
    }

    @Test
    void containsAtLeastElementsIn_failsMissingElementInOneToOne() {
        List<Integer> expected = List.of(64, 128, 256, 128);
        List<String> actual =
                List.of("fee", "+128", "fi", "fo", "+64", "+256", "fum");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeastElementsIn(expected));
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
                "missing (1)", "128");
    }

    @Test
    void containsAtLeastElementsIn_inOrder_failsOutOfOrder() {
        List<Integer> expected = List.of(64, 128, 256, 128);
        List<String> actual =
                List.of("fee", "+128", "+64", "fi", "fo", "0x80", "+256", "fum");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeastElementsIn(expected)
                        .inOrder());
        assertFailureKeys(
                failure,
                "required elements were all found, but order was wrong",
                "expected order for required elements",
                "testing whether",
                "but was");
        assertFailureValue(
                failure,
                "expected order for required elements", "[64, 128, 256, 128]");
    }

    @Test
    void containsAtLeastElementsIn_null() {
        List<Integer> expected = Arrays.asList(128, null);
        List<String> actual = Arrays.asList(null, "fee", "0x80");
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeastElementsIn(expected);
    }

    @Test
    void containsAtLeastElementsIn_array() {
        Integer[] expected = new Integer[]{64, 128, 256, 128};
        assertThat(List.of("fee", "+128", "+64", "fi", "fo", "0x80", "+256", "fum"))
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeastElementsIn(expected);

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(List.of("fee", "+64", "+128", "fi", "fo", "0x40", "0x80", "fum"))
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeastElementsIn(expected));
        assertFailureKeys(
                failure,
                "missing (1)", "---", "expected to contain at least", "testing whether", "but was");
        assertFailureValue(
                failure,
                "missing (1)", "256");
    }

    @Test
    void containsAtLeast() {
        List<String> actual =
                List.of("fee", "+64", "+128", "fi", "fo", "+256", "0x80", "fum");
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeast(64, 128, 256, 128)
                .inOrder();
    }

    @Test
    void containsAtLeast_inOrder_success() {
        List<String> actual =
                List.of("fee", "+64", "+128", "fi", "fo", "+256", "0x80", "fum");
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeast(64, 128, 256, 128)
                .inOrder();
    }

    @Test
    void containsAtLeast_successOutOfOrder() {
        List<String> actual =
                List.of("fee", "+128", "+64", "fi", "fo", "0x80", "+256", "fum");
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeast(64, 128, 256, 128);
    }

    @Test
    void containsAtLeast_failsMissingElementInOneToOne() {
        List<String> actual =
                List.of("fee", "+128", "fi", "fo", "+64", "+256", "fum");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAtLeast(64, 128, 256, 128));
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
                "missing (1)", "128");
    }

    @Test
    void containsAtLeast_nullValueInArray() {
        List<String> actual = Arrays.asList(null, "fee", "0x80");
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAtLeast(128, null);
    }

    @Test
    void containsAnyOf_success() {
        List<String> actual = List.of("+128", "+64", "+256", "0x40");
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAnyOf(255, 256, 257);
    }

    @Test
    void containsAnyOf_failure() {
        List<String> actual =
                List.of("+128", "+64", "This is not the string you're looking for", "0x40");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAnyOf(255, 256, 257));
        assertFailureKeys(
                failure,
                "expected to contain any of", "testing whether", "but was");
        assertFailureValue(
                failure,
                "expected to contain any of", "[255, 256, 257]");
        assertFailureValue(
                failure,
                "testing whether", "actual element parses to expected element");
        assertFailureValue(
                failure,
                "but was", "[+128, +64, This is not the string you're looking for, 0x40]");
    }

    @Test
    void containsAnyOf_null() {
        List<String> actual = asList("+128", "+64", null, "0x40");
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAnyOf(255, null, 257);
    }

    @Test
    void containsAnyOf_handlesExceptions() {
        // CASE_INSENSITIVE_EQUALITY.compare throws on the null actual element.
        List<String> actual = asList("abc", null, "ghi");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(CASE_INSENSITIVE_EQUALITY)
                        .containsAnyOf("DEF", "FED"));
        // We fail with the more helpful failure message about missing the expected values, not the NPE.
        assertFailureKeys(
                failure,
                "expected to contain any of",
                "testing whether",
                "but was",
                "additionally, one or more exceptions were thrown while comparing elements",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(null, DEF) threw java.lang.NullPointerException");
    }

    @Test
    void containsAnyOf_handlesExceptions_alwaysFails() {
        List<String> actual = asList("abc", null, "ghi");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(CASE_INSENSITIVE_EQUALITY)
                        .containsAnyOf("GHI", "XYZ"));
        // The actual list does contain the required match. However, no reasonable implementation would
        // find that mapping without hitting the null along the way, and that throws NPE, so we are
        // contractually required to fail.
        assertFailureKeys(
                failure,
                "one or more exceptions were thrown while comparing elements",
                "first exception",
                "expected to contain any of",
                "testing whether",
                "found match (but failing because of exception)",
                "full contents");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(null, GHI) threw java.lang.NullPointerException");
    }

    @Test
    void containsAnyIn_success() {
        List<String> actual = List.of("+128", "+64", "+256", "0x40");
        List<Integer> expected = List.of(255, 256, 257);
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAnyIn(expected);
    }

    @Test
    void containsAnyIn_failure() {
        List<String> actual =
                List.of("+128", "+64", "This is not the string you're looking for", "0x40");
        List<Integer> expected = List.of(255, 256, 257);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAnyIn(expected));
        assertFailureKeys(
                failure,
                "expected to contain any of", "testing whether", "but was");
        assertFailureValue(
                failure,
                "expected to contain any of", "[255, 256, 257]");
        assertFailureValue(
                failure,
                "testing whether", "actual element parses to expected element");
        assertFailureValue(
                failure,
                "but was", "[+128, +64, This is not the string you're looking for, 0x40]");
    }

    @Test
    void displayingDiffsPairedBy_containsAnyIn_withKeyMatches() {
        List<Record> expected =
                List.of(
                        Record.create(1, 100),
                        Record.create(2, 200),
                        Record.create(3, 300),
                        Record.createWithoutId(999));
        List<Record> actual =
                List.of(
                        Record.create(3, 311),
                        Record.create(2, 211),
                        Record.create(2, 222),
                        Record.create(4, 404),
                        Record.createWithoutId(888));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(RECORDS_EQUAL_WITH_SCORE_TOLERANCE_10)
                        .displayingDiffsPairedBy(RECORD_ID)
                        .containsAnyIn(expected));
        assertFailureKeys(
                failure,
                "expected to contain any of",
                "testing whether",
                "but was",
                "for key",
                "expected any of",
                "but got (2)",
                "#1",
                "diff",
                "#2",
                "diff",
                "---",
                "for key",
                "expected any of",
                "but got (1)",
                "#1",
                "diff",
                "---");
        // at key 2:
        assertFailureValueIndexed(
                failure,
                "for key", 0, "2");
        assertFailureValueIndexed(
                failure,
                "expected any of", 0, "2/200");
        assertFailureValueIndexed(
                failure,
                "#1", 0, "2/211");
        assertFailureValueIndexed(
                failure,
                "diff", 0, "score:11");
        assertFailureValue(
                failure,
                "#2", "2/222");
        assertFailureValueIndexed(
                failure,
                "diff", 1, "score:22");
        // at key 3:
        assertFailureValueIndexed(
                failure,
                "for key", 1, "3");
        assertFailureValueIndexed(
                failure,
                "expected any of", 1, "3/300");
        assertFailureValueIndexed(
                failure,
                "#1", 1, "3/311");
        assertFailureValueIndexed(
                failure,
                "diff", 2, "score:11");
    }

    @Test
    void displayingDiffsPairedBy_containsAnyIn_withoutKeyMatches() {
        List<Record> expected =
                List.of(Record.create(1, 100), Record.create(2, 200), Record.createWithoutId(999));
        List<Record> actual =
                List.of(Record.create(3, 300), Record.create(4, 411), Record.createWithoutId(888));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(RECORDS_EQUAL_WITH_SCORE_TOLERANCE_10)
                        .displayingDiffsPairedBy(RECORD_ID)
                        .containsAnyIn(expected));
        assertFailureKeys(
                failure,
                "expected to contain any of",
                "testing whether",
                "but was",
                "it does not contain any matches by key, either");
    }

    @Test
    void displayingDiffsPairedBy_containsAnyIn_notUnique() {
        List<Record> expected =
                List.of(
                        Record.create(1, 100),
                        Record.create(2, 200),
                        Record.create(2, 250),
                        Record.createWithoutId(999));
        List<Record> actual =
                List.of(Record.create(3, 300), Record.create(2, 211), Record.createWithoutId(888));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(RECORDS_EQUAL_WITH_SCORE_TOLERANCE_10)
                        .displayingDiffsPairedBy(RECORD_ID)
                        .containsAnyIn(expected));
        assertFailureKeys(
                failure,
                "expected to contain any of",
                "testing whether",
                "but was",
                "a key function which does not uniquely key the expected elements was provided and has"
                        + " consequently been ignored");
    }

    @Test
    void displayingDiffsPairedBy_containsAnyIn_handlesFormatDiffExceptions() {
        List<Record> expected =
                List.of(Record.create(1, 100), Record.create(2, 200), Record.create(0, 999));
        List<Record> actual = asList(Record.create(3, 311), Record.create(4, 404), null);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(RECORDS_EQUAL_WITH_SCORE_TOLERANCE_10)
                        .displayingDiffsPairedBy(NULL_SAFE_RECORD_ID)
                        .containsAnyIn(expected));
        assertFailureKeys(
                failure,
                "expected to contain any of",
                "testing whether",
                "but was",
                "for key",
                "expected any of",
                "but got (1)",
                "---",
                "additionally, one or more exceptions were thrown while formatting diffs",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("formatDiff(null, 0/999) threw java.lang.NullPointerException");
    }

    @Test
    void containsAnyIn_null() {
        List<String> actual = asList("+128", "+64", null, "0x40");
        List<Integer> expected = asList(255, null, 257);
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAnyIn(expected);
    }

    @Test
    void containsAnyIn_array() {
        List<String> actual = List.of("+128", "+64", "+256", "0x40");
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsAnyIn(new Integer[]{255, 256, 257});

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsAnyIn(new Integer[]{511, 512, 513}));
        assertFailureKeys(
                failure,
                "expected to contain any of", "testing whether", "but was");
    }

    @Test
    void containsNoneOf_success() {
        List<String> actual =
                List.of("+128", "+64", "This is not the string you're looking for", "0x40");
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsNoneOf(255, 256, 257);
    }

    @Test
    void containsNoneOf_failure() {
        List<String> actual = List.of("+128", "+64", "+256", "0x40");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsNoneOf(255, 256, 257));
        assertFailureKeys(
                failure,
                "expected not to contain any of",
                "testing whether",
                "but contained",
                "corresponding to",
                "---",
                "full contents");
        assertFailureValue(
                failure,
                "expected not to contain any of", "[255, 256, 257]");
        assertFailureValue(
                failure,
                "testing whether", "actual element parses to expected element");
        assertFailureValue(
                failure,
                "but contained", "[+256]");
        assertFailureValue(
                failure,
                "corresponding to", "256");
        assertFailureValue(
                failure,
                "full contents", "[+128, +64, +256, 0x40]");
    }

    @Test
    void containsNoneOf_multipleFailures() {
        List<String> actual = List.of("+128", "+64", "+256", "0x40");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsNoneOf(64, 128));
        assertFailureKeys(
                failure,
                "expected not to contain any of",
                "testing whether",
                "but contained",
                "corresponding to",
                "---",
                "but contained",
                "corresponding to",
                "---",
                "full contents");
        assertFailureValueIndexed(
                failure,
                "but contained", 0, "[+64, 0x40]");
        assertFailureValueIndexed(
                failure,
                "corresponding to", 0, "64");
        assertFailureValueIndexed(
                failure,
                "but contained", 1, "[+128]");
        assertFailureValueIndexed(
                failure,
                "corresponding to", 1, "128");
    }

    @Test
    void containsNoneOf_null() {
        List<String> actual = asList("+128", "+64", null, "0x40");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsNoneOf(255, null, 257));
        assertFailureKeys(
                failure,
                "expected not to contain any of",
                "testing whether",
                "but contained",
                "corresponding to",
                "---",
                "full contents");
        assertFailureValue(
                failure,
                "but contained", "[null]");
        assertFailureValue(
                failure,
                "corresponding to", "null");
    }

    @Test
    void containsNoneOf_handlesExceptions() {
        // CASE_INSENSITIVE_EQUALITY.compare throws on the null actual element.
        List<String> actual = asList("abc", null, "ghi");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(CASE_INSENSITIVE_EQUALITY)
                        .containsNoneOf("GHI", "XYZ"));
        // We fail with the more helpful failure message about the unexpected value, not the NPE.
        assertFailureKeys(
                failure,
                "expected not to contain any of",
                "testing whether",
                "but contained",
                "corresponding to",
                "---",
                "full contents",
                "additionally, one or more exceptions were thrown while comparing elements",
                "first exception");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(null, GHI) threw java.lang.NullPointerException");
    }

    @Test
    void containsNoneOf_handlesExceptions_alwaysFails() {
        List<String> actual = asList("abc", null, "ghi");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(CASE_INSENSITIVE_EQUALITY)
                        .containsNoneOf("DEF", "XYZ"));
        // The actual list does not contain the forbidden matcesh. However, we cannot establish that
        // without hitting the null along the way, and that throws NPE, so we are contractually required
        // to fail.
        assertFailureKeys(
                failure,
                "one or more exceptions were thrown while comparing elements",
                "first exception",
                "expected not to contain any of",
                "testing whether",
                "found no matches (but failing because of exception)",
                "full contents");
        assertFailureValue(
                failure,
                "expected not to contain any of", "[DEF, XYZ]");
        assertThatFailure(failure)
                .factValue("first exception")
                .startsWith("compare(null, DEF) threw java.lang.NullPointerException");
    }

    @Test
    void containsNoneIn_success() {
        List<String> actual =
                List.of("+128", "+64", "This is not the string you're looking for", "0x40");
        List<Integer> excluded = List.of(255, 256, 257);
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsNoneIn(excluded);
    }

    @Test
    void containsNoneIn_failure() {
        List<String> actual = List.of("+128", "+64", "+256", "0x40");
        List<Integer> excluded = List.of(255, 256, 257);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsNoneIn(excluded));
        assertFailureKeys(
                failure,
                "expected not to contain any of",
                "testing whether",
                "but contained",
                "corresponding to",
                "---",
                "full contents");
        assertFailureValue(
                failure,
                "but contained", "[+256]");
        assertFailureValue(
                failure,
                "corresponding to", "256");
    }

    @Test
    void containsNoneIn_null() {
        List<String> actual = asList("+128", "+64", null, "0x40");
        List<Integer> excluded = asList(255, null, 257);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsNoneIn(excluded));
        assertFailureKeys(
                failure,
                "expected not to contain any of",
                "testing whether",
                "but contained",
                "corresponding to",
                "---",
                "full contents");
        assertFailureValue(
                failure,
                "but contained", "[null]");
        assertFailureValue(
                failure,
                "corresponding to", "null");
    }

    @Test
    void containsNoneIn_array() {
        List<String> actual =
                List.of("+128", "+64", "This is not the string you're looking for", "0x40");
        assertThat(actual)
                .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                .containsNoneIn(new Integer[]{255, 256, 257});

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .comparingElementsUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
                        .containsNoneIn(new Integer[]{127, 128, 129}));
        assertFailureKeys(
                failure,
                "expected not to contain any of",
                "testing whether",
                "but contained",
                "corresponding to",
                "---",
                "full contents");
        assertFailureValue(
                failure,
                "but contained", "[+128]");
        assertFailureValue(
                failure,
                "corresponding to", "128");
    }

    @Test
    void formattingDiffsUsing_success() {
        List<Record> actual =
                List.of(Record.create(3, 300), Record.create(2, 200), Record.create(1, 100));
        assertThat(actual)
                .formattingDiffsUsing(RECORD_DIFF_FORMATTER)
                .displayingDiffsPairedBy(RECORD_ID)
                .containsExactly(Record.create(1, 100), Record.create(2, 200), Record.create(3, 300));
    }

    @Test
    void formattingDiffsUsing_failure() {
        List<Record> actual =
                List.of(
                        Record.create(3, 300),
                        Record.create(2, 201),
                        Record.create(1, 100),
                        Record.create(2, 199));
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(actual)
                        .formattingDiffsUsing(RECORD_DIFF_FORMATTER)
                        .displayingDiffsPairedBy(RECORD_ID)
                        .containsExactly(Record.create(1, 100), Record.create(2, 200), Record.create(3, 300)));
        assertFailureKeys(
                failure,
                "for key",
                "missing",
                "unexpected (2)",
                "#1",
                "diff",
                "#2",
                "diff",
                "---",
                "expected",
                "but was");
        assertFailureValue(
                failure,
                "missing", "2/200");
        assertFailureValue(
                failure,
                "#1", "2/201");
        assertFailureValueIndexed(
                failure,
                "diff", 0, "score:1");
        assertFailureValue(
                failure,
                "#2", "2/199");
        assertFailureValueIndexed(
                failure,
                "diff", 1, "score:-1");
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
