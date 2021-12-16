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

import com.google.common.truth.Platform.PlatformComparisonFailure;

import java.util.List;

import static com.google.common.truth.Fact.makeMessage;
import static java.util.Objects.requireNonNull;

/**
 * An {@link AssertionError} (usually a JUnit {@code ComparisonFailure}, but not under GWT) composed
 * of structured {@link Fact} instances and other string messages.
 */
final class ComparisonFailureWithFacts extends PlatformComparisonFailure implements ErrorWithFacts {
    private final List<Fact> facts;

    ComparisonFailureWithFacts(
            List<String> messages,
            List<Fact> facts,
            String expected,
            String actual,
            Throwable cause) {
        super(makeMessage(messages, facts), requireNonNull(expected), requireNonNull(actual), cause);
        this.facts = requireNonNull(facts);
    }

    @Override
    public List<Fact> facts() {
        return facts;
    }
}
