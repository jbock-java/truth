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
package io.jbock.common.truth;


import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static io.jbock.common.truth.ExpectFailure.assertThat;
import static io.jbock.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for String Subjects.
 *
 * @author David Saff
 * @author Christian Gruber (cgruber@israfil.net)
 */
class StringSubjectTest extends BaseSubjectTestCase {

    @Test
    void hasLength() {
        assertThat("kurt").hasLength(4);
    }

    @Test
    void hasLengthZero() {
        assertThat("").hasLength(0);
    }

    @Test
    void hasLengthFails() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("kurt")
                        .hasLength(5));
        assertFailureValue(
                failure,
                "value of", "string.length()");
    }

    @Test
    void hasLengthNegative() {
        try {
            assertThat("kurt").hasLength(-1);
            fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    void stringIsEmpty() {
        assertThat("").isEmpty();
    }

    @Test
    void stringIsEmptyFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("abc")
                        .isEmpty());
        assertFailureKeys(
                failure,
                "expected to be empty", "but was");
    }

    @Test
    void stringIsEmptyFailNull() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat((String) null)
                        .isEmpty());
        assertFailureKeys(
                failure,
                "expected empty string", "but was");
    }

    @Test
    void stringIsNotEmpty() {
        assertThat("abc").isNotEmpty();
    }

    @Test
    void stringIsNotEmptyFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("")
                        .isNotEmpty());
        assertFailureKeys(
                failure,
                "expected not to be empty");
    }

    @Test
    void stringIsNotEmptyFailNull() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat((String) null)
                        .isNotEmpty());
        assertFailureKeys(
                failure,
                "expected nonempty string", "but was");
    }

    @Test
    void stringContains() {
        assertThat("abc").contains("c");
    }

    @Test
    void stringContainsCharSeq() {
        CharSequence charSeq = new StringBuilder("c");
        assertThat("abc").contains(charSeq);
    }

    @Test
    void stringContainsFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("abc")
                        .contains("d"));
        assertFailureValue(
                failure,
                "expected to contain", "d");
    }

    @Test
    void stringDoesNotContain() {
        assertThat("abc").doesNotContain("d");
    }

    @Test
    void stringDoesNotContainCharSequence() {
        CharSequence charSeq = new StringBuilder("d");
        assertThat("abc").doesNotContain(charSeq);
    }

    @Test
    void stringDoesNotContainFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("abc")
                        .doesNotContain("b"));
        assertFailureValue(
                failure,
                "expected not to contain", "b");
    }

    @Test
    void stringEquality() {
        assertThat("abc").isEqualTo("abc");
    }

    @Test
    void stringEqualityToNull() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("abc")
                        .isEqualTo(null));
        assertThat(failure).isNotInstanceOf(ComparisonFailureWithFacts.class);
    }

    @Test
    void stringEqualityToEmpty() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("abc")
                        .isEqualTo(""));
        assertFailureKeys(
                failure,
                "expected an empty string", "but was");
    }

    @Test
    void stringEqualityEmptyToNonEmpty() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("")
                        .isEqualTo("abc"));
        assertFailureKeys(
                failure,
                "expected", "but was an empty string");
    }

    @Test
    void stringEqualityFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("abc")
                        .isEqualTo("ABC"));
        assertThat(failure).isInstanceOf(ComparisonFailureWithFacts.class);
    }

    @Test
    void stringStartsWith() {
        assertThat("abc").startsWith("ab");
    }

    @Test
    void stringStartsWithFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("abc")
                        .startsWith("bc"));
        assertFailureValue(
                failure,
                "expected to start with", "bc");
    }

    @Test
    void stringEndsWith() {
        assertThat("abc").endsWith("bc");
    }

    @Test
    void stringEndsWithFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("abc")
                        .endsWith("ab"));
        assertFailureValue(
                failure,
                "expected to end with", "ab");
    }

    @Test
    void emptyStringTests() {
        assertThat("").contains("");
        assertThat("").startsWith("");
        assertThat("").endsWith("");
        assertThat("a").contains("");
        assertThat("a").startsWith("");
        assertThat("a").endsWith("");
    }

    @Test
    void stringMatchesString() {
        assertThat("abcaaadev").matches(".*aaa.*");
    }

    @Test
    void stringMatchesStringWithFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("abcaqadev")
                        .matches(".*aaa.*"));
        assertFailureValue(
                failure,
                "expected to match", ".*aaa.*");
    }

    @Test
    void stringMatchesStringFailNull() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat((String) null)
                        .matches(".*aaa.*"));
        assertFailureValue(
                failure,
                "expected a string that matches", ".*aaa.*");
    }

    @Test
    void stringMatchesStringLiteralFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("$abc")
                        .matches("$abc"));
        assertFailureValue(
                failure,
                "expected to match", "$abc");
        assertFailureValue(
                failure,
                "but was", "$abc");
        assertThat(failure)
                .factKeys()
                .contains("Looks like you want to use .isEqualTo() for an exact equality assertion.");
    }

    @Test
    void stringMatchesPattern() {
        assertThat("abcaaadev").matches(Pattern.compile(".*aaa.*"));
    }

    @Test
    void stringMatchesPatternWithFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("abcaqadev")
                        .matches(Pattern.compile(".*aaa.*")));
        assertFailureValue(
                failure,
                "expected to match", ".*aaa.*");
    }

    @Test
    void stringMatchesPatternFailNull() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat((String) null)
                        .matches(Pattern.compile(".*aaa.*")));
        assertFailureValue(
                failure,
                "expected a string that matches", ".*aaa.*");
    }

    @Test
    void stringMatchesPatternLiteralFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("$abc")
                        .matches(Pattern.compile("$abc")));
        assertFailureValue(
                failure,
                "expected to match", "$abc");
        assertFailureValue(
                failure,
                "but was", "$abc");
        assertThat(failure)
                .factKeys()
                .contains(
                        "If you want an exact equality assertion you can escape your regex with"
                                + " Pattern.quote().");
    }

    @Test
    void stringDoesNotMatchString() {
        assertThat("abcaqadev").doesNotMatch(".*aaa.*");
    }

    @Test
    void stringDoesNotMatchStringWithFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("abcaaadev")
                        .doesNotMatch(".*aaa.*"));
        assertFailureValue(
                failure,
                "expected not to match", ".*aaa.*");
    }

    @Test
    void stringDoesNotMatchStringFailNull() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat((String) null)
                        .doesNotMatch(".*aaa.*"));
        assertFailureValue(
                failure,
                "expected a string that does not match", ".*aaa.*");
    }

    @Test
    void stringDoesNotMatchPattern() {
        assertThat("abcaqadev").doesNotMatch(Pattern.compile(".*aaa.*"));
    }

    @Test
    void stringDoesNotMatchPatternWithFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("abcaaadev")
                        .doesNotMatch(Pattern.compile(".*aaa.*")));
        assertFailureValue(
                failure,
                "expected not to match", ".*aaa.*");
    }

    @Test
    void stringDoesNotMatchPatternFailNull() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat((String) null)
                        .doesNotMatch(Pattern.compile(".*aaa.*")));
        assertFailureValue(
                failure,
                "expected a string that does not match", ".*aaa.*");
    }

    @Test
    void stringContainsMatchStringUsesFind() {
        assertThat("aba").containsMatch("[b]");
        assertThat("aba").containsMatch(Pattern.compile("[b]"));
    }

    @Test
    void stringContainsMatchString() {
        assertThat("aba").containsMatch(".*b.*");

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("aaa")
                        .containsMatch(".*b.*"));
        assertFailureValue(
                failure,
                "expected to contain a match for", ".*b.*");
    }

    @Test
    void stringContainsMatchStringFailNull() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat((String) null)
                        .containsMatch(".*b.*"));
        assertFailureValue(
                failure,
                "expected a string that contains a match for", ".*b.*");
    }

    @Test
    void stringContainsMatchPattern() {
        assertThat("aba").containsMatch(Pattern.compile(".*b.*"));

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("aaa")
                        .containsMatch(Pattern.compile(".*b.*")));
        assertFailureValue(
                failure,
                "expected to contain a match for", ".*b.*");
    }

    @Test
    void stringContainsMatchPatternFailNull() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat((String) null)
                        .containsMatch(Pattern.compile(".*b.*")));
        assertFailureValue(
                failure,
                "expected a string that contains a match for", ".*b.*");
    }

    @Test
    void stringDoesNotContainMatchString() {
        assertThat("aaa").doesNotContainMatch(".*b.*");

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("aba")
                        .doesNotContainMatch(".*b.*"));
        assertFailureValue(
                failure,
                "expected not to contain a match for", ".*b.*");
    }

    @Test
    void stringDoesNotContainMatchStringUsesFind() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("aba")
                        .doesNotContainMatch("[b]"));
        assertFailureValue(
                failure,
                "expected not to contain a match for", "[b]");
    }

    @Test
    void stringDoesNotContainMatchStringUsesFindFailNull() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat((String) null)
                        .doesNotContainMatch("[b]"));
        assertFailureValue(
                failure,
                "expected a string that does not contain a match for", "[b]");
    }

    @Test
    void stringDoesNotContainMatchPattern() {
        assertThat("zzaaazz").doesNotContainMatch(Pattern.compile(".b."));

        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("zzabazz")
                        .doesNotContainMatch(Pattern.compile(".b.")));
        assertFailureValue(
                failure,
                "expected not to contain a match for", ".b.");
        assertFailureValue(
                failure,
                "but contained", "aba");
        assertFailureValue(
                failure,
                "full string", "zzabazz");
    }

    @Test
    void stringDoesNotContainMatchPatternFailNull() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat((String) null)
                        .doesNotContainMatch(Pattern.compile(".b.")));
        assertFailureValue(
                failure,
                "expected a string that does not contain a match for", ".b.");
    }

    @Test
    void stringEqualityIgnoringCase() {
        assertThat("café").ignoringCase().isEqualTo("CAFÉ");
    }

    @Test
    void stringEqualityIgnoringCaseWithNullSubject() {
        assertThat((String) null).ignoringCase().isEqualTo(null);
    }

    @Test
    void stringEqualityIgnoringCaseFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("abc")
                        .ignoringCase()
                        .isEqualTo("abd"));
        assertFailureValue(
                failure,
                "expected", "abd");
        assertThat(failure).factKeys().contains("(case is ignored)");
    }

    @Test
    void stringEqualityIgnoringCaseFailWithNullSubject() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat((String) null)
                        .ignoringCase().isEqualTo("abc"));
        assertFailureValue(
                failure,
                "expected a string that is equal to", "abc");
        assertThat(failure).factKeys().contains("(case is ignored)");
    }

    @Test
    void stringEqualityIgnoringCaseFailWithNullExpectedString() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("abc")
                        .ignoringCase().isEqualTo(null));
        assertFailureValue(
                failure,
                "expected", "null (null reference)");
        assertThat(failure).factKeys().contains("(case is ignored)");
    }

    @Test
    void stringInequalityIgnoringCase() {
        assertThat("café").ignoringCase().isNotEqualTo("AFÉ");
    }

    @Test
    void stringInequalityIgnoringCaseWithNullSubject() {
        assertThat((String) null).ignoringCase().isNotEqualTo("abc");
    }

    @Test
    void stringInequalityIgnoringCaseWithNullExpectedString() {
        assertThat("abc").ignoringCase().isNotEqualTo(null);
    }

    @Test
    void stringInequalityIgnoringCaseFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("café")
                        .ignoringCase()
                        .isNotEqualTo("CAFÉ"));
        assertFailureValue(
                failure,
                "expected not to be", "CAFÉ");
        assertThat(failure).factKeys().contains("(case is ignored)");
    }

    @Test
    void stringInequalityIgnoringCaseFailWithNullSubject() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat((String) null)
                        .ignoringCase().isNotEqualTo(null));

        assertFailureValue(
                failure,
                "expected a string that is not equal to", "null (null reference)");
        assertThat(failure).factKeys().contains("(case is ignored)");
    }

    @Test
    void stringContainsIgnoringCase() {
        assertThat("äbc").ignoringCase().contains("Ä");
    }

    @Test
    void stringContainsIgnoringCaseEmptyString() {
        assertThat("abc").ignoringCase().contains("");
    }

    @Test
    void stringContainsIgnoringCaseWithWord() {
        assertThat("abcdé").ignoringCase().contains("CdÉ");
    }

    @Test
    void stringContainsIgnoringCaseWholeWord() {
        assertThat("abcde").ignoringCase().contains("ABCde");
    }

    @Test
    void stringContainsIgnoringCaseCharSeq() {
        CharSequence charSeq = new StringBuilder("C");
        assertThat("abc").ignoringCase().contains(charSeq);
    }

    @Test
    void stringContainsIgnoringCaseFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("abc")
                        .ignoringCase()
                        .contains("d"));

        assertFailureValue(
                failure,
                "expected to contain", "d");
        assertThat(failure).factKeys().contains("(case is ignored)");
    }

    @Test
    void stringContainsIgnoringCaseFailBecauseTooLarge() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("abc")
                        .ignoringCase()
                        .contains("abcc"));
        assertFailureValue(
                failure,
                "expected to contain", "abcc");
        assertThat(failure).factKeys().contains("(case is ignored)");
    }

    @Test
    void stringContainsIgnoringCaseFailBecauseNullSubject() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat((String) null)
                        .ignoringCase()
                        .contains("d"));
        assertFailureValue(
                failure,
                "expected a string that contains", "d");
        assertThat(failure).factKeys().contains("(case is ignored)");
    }

    @Test
    void stringDoesNotContainIgnoringCase() {
        assertThat("äbc").ignoringCase().doesNotContain("Äc");
    }

    @Test
    void stringDoesNotContainIgnoringCaseCharSeq() {
        CharSequence charSeq = new StringBuilder("cb");
        assertThat("abc").ignoringCase().doesNotContain(charSeq);
    }

    @Test
    void stringDoesNotContainIgnoringCaseFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("äbc")
                        .ignoringCase().doesNotContain("Äb"));
        assertFailureValue(
                failure,
                "expected not to contain", "Äb");
        assertThat(failure).factKeys().contains("(case is ignored)");
    }

    @Test
    void stringDoesNotContainIgnoringCaseFailWithEmptyString() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("abc")
                        .ignoringCase().doesNotContain(""));
        assertFailureValue(
                failure,
                "expected not to contain", "");
        assertThat(failure).factKeys().contains("(case is ignored)");
    }

    @Test
    void stringDoesNotContainIgnoringCaseFailBecauseNullSubject() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat((String) null)
                        .ignoringCase()
                        .doesNotContain("d"));
        assertFailureValue(
                failure,
                "expected a string that does not contain", "d");
        assertThat(failure).factKeys().contains("(case is ignored)");
    }

    @Test
    void trailingWhitespaceInActual() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("foo\n")
                        .isEqualTo("foo"));
        assertFailureKeys(
                failure,
                "expected", "but contained extra trailing whitespace");
        assertFailureValue(
                failure,
                "but contained extra trailing whitespace", "\\n");
    }

    @Test
    void trailingWhitespaceInExpected() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("foo")
                        .isEqualTo("foo "));
        assertFailureKeys(
                failure,
                "expected", "but was missing trailing whitespace");
        assertFailureValue(
                failure,
                "but was missing trailing whitespace", "␣");
    }

    @Test
    void trailingWhitespaceInBoth() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("foo ")
                        .isEqualTo("foo\n"));
        assertFailureKeys(
                failure,
                "expected", "with trailing whitespace", "but trailing whitespace was");
        assertFailureValue(
                failure,
                "with trailing whitespace", "\\n");
        assertFailureValue(
                failure,
                "but trailing whitespace was", "␣");
    }

    @Test
    void trailingWhitespaceVsEmptyString() {
        /*
         * The code has special cases for both trailing whitespace and an empty string. Make sure that
         * it specifically reports the trailing whitespace. (It might be nice to *also* report the empty
         * string specially, but that's less important.)
         */
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat("\t")
                        .isEqualTo(""));
        assertFailureKeys(
                failure,
                "expected", "but contained extra trailing whitespace");
        assertFailureValue(
                failure,
                "but contained extra trailing whitespace", "\\t");
    }
}
