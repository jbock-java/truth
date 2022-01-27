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

package io.jbock.common.truth;

import org.junit.jupiter.api.Test;

import java.util.List;

import static io.jbock.common.truth.ComparisonFailures.formatExpectedAndActual;
import static io.jbock.common.truth.Fact.fact;
import static io.jbock.common.truth.Fact.simpleFact;
import static io.jbock.common.truth.Truth.assertThat;

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
                "a\n".repeat(100) + "b",
                "a\n".repeat(100) + "c",
                joinNewline("@@ -98,4 +98,4 @@", " a", " a", " a", "-b", "+c"));
    }

    @Test
    void formatDiffOmitEnd() {
        runFormatTest(
                "a" + "\nz".repeat(100),
                "b" + "\nz".repeat(100),
                joinNewline("@@ -1,4 +1,4 @@", "-a", "+b", " z", " z", " z"));
    }

    @Test
    void formatDiffOmitBoth() {
        runFormatTest(
                "a\n".repeat(100) + "m" + "\nz".repeat(100),
                "a\n".repeat(100) + "n" + "\nz".repeat(100),
                joinNewline("@@ -98,7 +98,7 @@", " a", " a", " a", "-m", "+n", " z", " z", " z"));
    }

    @Test
    void formatDiffOmitBothMultipleDifferingLines() {
        runFormatTest(
                "a\n".repeat(100) + "m\nn\no\np" + "\nz".repeat(100),
                "a\n".repeat(100) + "q\nr\ns\nt" + "\nz".repeat(100),
                joinNewline(
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
                "a\n".repeat(100) + "m\nn\no\np" + "\nz".repeat(100),
                "a\n".repeat(100) + "q\nr\ns\nt\nu\nv" + "\nz".repeat(100),
                joinNewline(
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
                "a\n".repeat(40) + "l\nm\nn\no\np\n" + "a\n".repeat(40),
                "a\n".repeat(40) + "l\nm\nn\no\np\nl\nm\nn\no\np\n" + "a\n".repeat(40),
                joinNewline(
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
                "a\n".repeat(80),
                "a\n".repeat(82),
                joinNewline("@@ -78,4 +78,6 @@", " a", " a", " a", "+a", "+a", " "));
        /*
         * The final blank line here is odd, and it's different than what Unix diff produces. Maybe look
         * into removing it if we can do so safely?
         */
    }

    @Test
    void formatDiffSameExceptNewlineStyle() {
        runFormatTest(
                "a\n".repeat(10),
                "a\r\n".repeat(10),
                "(line contents match, but line-break characters differ)");
    }

    @Test
    void formatDiffSameExceptTrailingNewline() {
        runFormatTest(
                "a\n".repeat(19) + "a",
                "a\n".repeat(19) + "a\n",
                joinNewline("@@ -18,3 +18,4 @@", " a", " a", " a", "+"));
    }

    @Test
    void testSerialization_ComparisonFailureWithFacts() {
        List<String> messages = List.of("hello");
        List<Fact> facts = List.of(fact("first", "value"), simpleFact("second"));
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
        List<String> messages = List.of("hello");
        List<Fact> facts = List.of(fact("first", "value"), simpleFact("second"));
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

    private static String joinNewline(String... strings) {
        return String.join("\n", strings);
    }
}
