/*
 * Copyright (c) 2018 Google, Inc.
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

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.google.common.base.Strings.repeat;
import static com.google.common.truth.ComparisonFailures.formatExpectedAndActual;
import static com.google.common.truth.Fact.fact;
import static com.google.common.truth.Fact.simpleFact;
import static com.google.common.truth.Truth.assertThat;

/** Test for {@link ComparisonFailureWithFacts}. */
class ComparisonFailureWithFactsTest {
    @Test
    void formatAllDifferent() {
        runFormatTest(
                "foo", "bar",
                "foo", "bar");
    }

    @Test
    void formatShortOverlap() {
        runFormatTest(
                "bar", "baz",
                "bar", "baz");
    }

    @Test
    void formatDiffOmitStart() {
        runFormatTest(
                repeat("a\n", 100) + "b",
                repeat("a\n", 100) + "c",
                Joiner.on('\n').join("@@ -98,4 +98,4 @@", " a", " a", " a", "-b", "+c"));
    }

    @Test
    void formatDiffOmitEnd() {
        runFormatTest(
                "a" + repeat("\nz", 100),
                "b" + repeat("\nz", 100),
                Joiner.on('\n').join("@@ -1,4 +1,4 @@", "-a", "+b", " z", " z", " z"));
    }

    @Test
    void formatDiffOmitBoth() {
        runFormatTest(
                repeat("a\n", 100) + "m" + repeat("\nz", 100),
                repeat("a\n", 100) + "n" + repeat("\nz", 100),
                Joiner.on('\n').join("@@ -98,7 +98,7 @@", " a", " a", " a", "-m", "+n", " z", " z", " z"));
    }

    @Test
    void formatDiffOmitBothMultipleDifferingLines() {
        runFormatTest(
                repeat("a\n", 100) + "m\nn\no\np" + repeat("\nz", 100),
                repeat("a\n", 100) + "q\nr\ns\nt" + repeat("\nz", 100),
                Joiner.on('\n')
                        .join(
                                "@@ -98,10 +98,10 @@",
                                " a",
                                " a",
                                " a",
                                "-m",
                                "-n",
                                "-o",
                                "-p",
                                "+q",
                                "+r",
                                "+s",
                                "+t",
                                " z",
                                " z",
                                " z"));
    }

    @Test
    void formatDiffOmitBothMultipleDifferingLinesDifferentLength() {
        runFormatTest(
                repeat("a\n", 100) + "m\nn\no\np" + repeat("\nz", 100),
                repeat("a\n", 100) + "q\nr\ns\nt\nu\nv" + repeat("\nz", 100),
                Joiner.on('\n')
                        .join(
                                "@@ -98,10 +98,12 @@",
                                " a",
                                " a",
                                " a",
                                "-m",
                                "-n",
                                "-o",
                                "-p",
                                "+q",
                                "+r",
                                "+s",
                                "+t",
                                "+u",
                                "+v",
                                " z",
                                " z",
                                " z"));
    }

    @Test
    void formatDiffPrefixAndSuffixWouldOverlapSimple() {
        runFormatTest(
                repeat("a\n", 40) + "l\nm\nn\no\np\n" + repeat("a\n", 40),
                repeat("a\n", 40) + "l\nm\nn\no\np\nl\nm\nn\no\np\n" + repeat("a\n", 40),
                Joiner.on('\n')
                        .join(
                                "@@ -43,6 +43,11 @@",
                                " n",
                                " o",
                                " p",
                                "+l",
                                "+m",
                                "+n",
                                "+o",
                                "+p",
                                " a",
                                " a",
                                " a"));
    }

    @Test
    void formatDiffPrefixAndSuffixWouldOverlapAllSame() {
        runFormatTest(
                repeat("a\n", 80),
                repeat("a\n", 82),
                Joiner.on('\n').join("@@ -78,4 +78,6 @@", " a", " a", " a", "+a", "+a", " "));
        /*
         * The final blank line here is odd, and it's different than what Unix diff produces. Maybe look
         * into removing it if we can do so safely?
         */
    }

    @Test
    void formatDiffSameExceptNewlineStyle() {
        runFormatTest(
                repeat("a\n", 10),
                repeat("a\r\n", 10),
                "(line contents match, but line-break characters differ)");
    }

    @Test
    void formatDiffSameExceptTrailingNewline() {
        runFormatTest(
                repeat("a\n", 19) + "a",
                repeat("a\n", 19) + "a\n",
                Joiner.on('\n').join("@@ -18,3 +18,4 @@", " a", " a", " a", "+"));
    }

    @Test
    void testSerialization_ComparisonFailureWithFacts() {
        ImmutableList<String> messages = ImmutableList.of("hello");
        ImmutableList<Fact> facts = ImmutableList.of(fact("first", "value"), simpleFact("second"));
        String expected = "expected";
        String actual = "actual";
        Throwable cause = new Throwable("cause");
        ComparisonFailureWithFacts original =
                new ComparisonFailureWithFacts(messages, facts, expected, actual, cause);

        assertThat(original.facts().get(0).key).isEqualTo("first");
        assertThat(original.facts().get(0).value).isEqualTo("value");
        assertThat(original.facts().get(1).key).isEqualTo("second");
        assertThat(original.getExpected()).isEqualTo("expected");
        assertThat(original.getActual()).isEqualTo("actual");
    }

    @Test
    void testSerialization_AssertionErrorWithFacts() {
        ImmutableList<String> messages = ImmutableList.of("hello");
        ImmutableList<Fact> facts = ImmutableList.of(fact("first", "value"), simpleFact("second"));
        Throwable cause = new Throwable("cause");
        AssertionErrorWithFacts original = new AssertionErrorWithFacts(messages, facts, cause);

        assertThat(original.facts().get(0).key).isEqualTo("first");
        assertThat(original.facts().get(0).value).isEqualTo("value");
        assertThat(original.facts().get(1).key).isEqualTo("second");
    }

    private static void runFormatTest(
            String expected, String actual, String expectedExpected, String expectedActual) {
        List<Fact> facts = formatExpectedAndActual(expected, actual);
        assertThat(facts).hasSize(2);
        assertThat(facts.get(0).key).isEqualTo("expected");
        assertThat(facts.get(1).key).isEqualTo("but was");
        assertThat(facts.get(0).value).isEqualTo(expectedExpected);
        assertThat(facts.get(1).value).isEqualTo(expectedActual);
    }

    private static void runFormatTest(String expected, String actual, String expectedDiff) {
        List<Fact> facts = formatExpectedAndActual(expected, actual);
        assertThat(facts).hasSize(1);
        assertThat(facts.get(0).key).isEqualTo("diff (-expected +actual)");
        assertThat(facts.get(0).value).isEqualTo(expectedDiff);
    }
}
