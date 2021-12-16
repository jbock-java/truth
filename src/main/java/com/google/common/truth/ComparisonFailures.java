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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.truth.Fact.fact;

/**
 * Contains part of the code responsible for creating a JUnit {@code ComparisonFailure} (if
 * available) or a plain {@code AssertionError} (if not).
 *
 * <p>This particular class is responsible for the fallback when a platform offers {@code
 * ComparisonFailure} but it is not available in a particular test environment. In practice, that
 * should mean open-source JRE users who choose to exclude our JUnit 4 dependency.
 *
 * <p>(This class also includes logic to format expected and actual values for easier reading.)
 *
 * <p>Another part of the fallback logic is {@code Platform.ComparisonFailureWithFacts}, which has a
 * different implementation under GWT/j2cl, where {@code ComparisonFailure} is also unavailable but
 * we can't just recover from that at runtime.
 */
final class ComparisonFailures {
    static List<Fact> makeComparisonFailureFacts(
            List<Fact> headFacts,
            List<Fact> tailFacts,
            String expected,
            String actual) {
        return Stream.of(headFacts, formatExpectedAndActual(expected, actual), tailFacts)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns one or more facts describing the difference between the given expected and actual
     * values.
     *
     * <p>Currently, that means either 2 facts (one each for expected and actual) or 1 fact with a
     * diff-like (but much simpler) view.
     *
     * <p>In the case of 2 facts, the facts contain either the full expected and actual values or, if
     * the values have a long prefix or suffix in common, abbreviated values with "…" at the beginning
     * or end.
     */
    static List<Fact> formatExpectedAndActual(String expected, String actual) {
        List<Fact> result;

        // TODO(cpovirk): Call attention to differences in trailing whitespace.
        // TODO(cpovirk): And changes in the *kind* of whitespace characters in the middle of the line.

        result = Platform.makeDiff(expected, actual);
        if (result != null) {
            return result;
        }

        return List.of(fact("expected", expected), fact("but was", actual));
    }

    private ComparisonFailures() {
    }
}
