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

import java.util.List;

import static com.google.common.truth.Fact.makeMessage;
import static java.util.Objects.requireNonNull;

/**
 * An {@link AssertionError} composed of structured {@link Fact} instances and other string
 * messages.
 */
@SuppressWarnings("OverrideThrowableToString") // We intentionally hide the class name.
final class AssertionErrorWithFacts extends AssertionError implements ErrorWithFacts {
    private final List<Fact> facts;

    /** Separate cause field, in case initCause() fails. */
    private final Throwable cause;

    AssertionErrorWithFacts(
            List<String> messages, List<Fact> facts, Throwable cause) {
        super(makeMessage(messages, facts));
        this.facts = requireNonNull(facts);

        this.cause = cause;
        try {
            initCause(cause);
        } catch (IllegalStateException alreadyInitializedBecauseOfHarmonyBug) {
            // See Truth.SimpleAssertionError.
        }
    }

    @Override
    @SuppressWarnings("UnsynchronizedOverridesSynchronized")
    public Throwable getCause() {
        return cause;
    }

    @Override
    public String toString() {
        return getLocalizedMessage();
    }

    @Override
    public List<Fact> facts() {
        return facts;
    }
}
