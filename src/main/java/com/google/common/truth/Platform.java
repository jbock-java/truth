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

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.regex.Pattern;

import static com.google.common.base.Throwables.throwIfUnchecked;
import static com.google.common.truth.DiffUtils.generateUnifiedDiff;
import static com.google.common.truth.Fact.fact;

/**
 * Extracted routines that need to be swapped in for GWT, to allow for minimal deltas between the
 * GWT and non-GWT version.
 *
 * @author Christian Gruber (cgruber@google.com)
 */
final class Platform {
    private Platform() {
    }

    /** Returns true if the instance is assignable to the type Clazz. */
    static boolean isInstanceOfType(Object instance, Class<?> clazz) {
        return clazz.isInstance(instance);
    }

    /** Determines if the given subject contains a match for the given regex. */
    static boolean containsMatch(String actual, String regex) {
        return Pattern.compile(regex).matcher(actual).find();
    }


    /**
     * Tries to infer a name for the root actual value from the bytecode. The "root" actual value is
     * the value passed to {@code assertThat} or {@code that}, as distinct from any later actual
     * values produced by chaining calls like {@code hasMessageThat}.
     */
    static String inferDescription() {
        return null;
    }

    private static final String DIFF_KEY = "diff (-expected +actual)";

    static ImmutableList<Fact> makeDiff(String expected, String actual) {
        ImmutableList<String> expectedLines = splitLines(expected);
        ImmutableList<String> actualLines = splitLines(actual);
        List<String> unifiedDiff =
                generateUnifiedDiff(expectedLines, actualLines, /* contextSize= */ 3);
        if (unifiedDiff.isEmpty()) {
            return ImmutableList.of(
                    fact(DIFF_KEY, "(line contents match, but line-break characters differ)"));
            // TODO(cpovirk): Possibly include the expected/actual value, too?
        }
        String result = Joiner.on("\n").join(unifiedDiff);
        if (result.length() > expected.length() && result.length() > actual.length()) {
            return null;
        }
        return ImmutableList.of(fact(DIFF_KEY, result));
    }

    private static ImmutableList<String> splitLines(String s) {
        // splitToList is @Beta, so we avoid it.
        return ImmutableList.copyOf(Splitter.onPattern("\r?\n").split(s));
    }

    abstract static class PlatformComparisonFailure extends AssertionError {
        private final String message;

        /*
         * We have to use the f prefix until the next major release to ensure
         * serialization compatibility.
         * See https://github.com/junit-team/junit4/issues/976
         */
        private final String fExpected;
        private final String fActual;

        /** Separate cause field, in case initCause() fails. */
        private final Throwable cause;

        PlatformComparisonFailure(
                String message, String expected, String actual, Throwable cause) {
            this.message = message;
            this.cause = cause;
            this.fActual = actual;
            this.fExpected = expected;

            try {
                initCause(cause);
            } catch (IllegalStateException alreadyInitializedBecauseOfHarmonyBug) {
                // See Truth.SimpleAssertionError.
            }
        }

        @Override
        public final String getMessage() {
            return message;
        }

        /**
         * Returns the actual string value
         *
         * @return the actual string value
         */
        public String getActual() {
            return fActual;
        }

        /**
         * Returns the expected string value
         *
         * @return the expected string value
         */
        public String getExpected() {
            return fExpected;
        }

        @Override
        @SuppressWarnings("UnsynchronizedOverridesSynchronized")
        public final Throwable getCause() {
            return cause;
        }

        // To avoid printing the class name before the message.
        // TODO(cpovirk): Write a test that fails without this. Ditto for SimpleAssertionError.
        @Override
        public final String toString() {
            return getLocalizedMessage();
        }
    }

    static String doubleToString(double value) {
        return Double.toString(value);
    }

    static String floatToString(float value) {
        return Float.toString(value);
    }

    /** Turns a non-double, non-float object into a string. */
    static String stringValueOfNonFloatingPoint(Object o) {
        return String.valueOf(o);
    }

    /** Returns a human readable string representation of the throwable's stack trace. */
    static String getStackTraceAsString(Throwable throwable) {
        return Throwables.getStackTraceAsString(throwable);
    }

    static AssertionError makeComparisonFailure(
            List<String> messages,
            List<Fact> facts,
            String expected,
            String actual,
            Throwable cause) {
        Class<?> comparisonFailureClass;
        try {
            comparisonFailureClass = Class.forName("com.google.common.truth.ComparisonFailureWithFacts");
        } catch (LinkageError | ClassNotFoundException probablyJunitNotOnClasspath) {
            /*
             * LinkageError makes sense, but ClassNotFoundException shouldn't happen:
             * ComparisonFailureWithFacts should be there, even if its JUnit 4 dependency is not. But it's
             * harmless to catch an "impossible" exception, and if someone decides to strip the class out
             * (perhaps along with Platform.PlatformComparisonFailure, to satisfy a tool that is unhappy
             * because it can't find the latter's superclass because JUnit 4 is also missing?), presumably
             * we should still fall back to a plain AssertionError.
             *
             * TODO(cpovirk): Consider creating and using yet another class like AssertionErrorWithFacts,
             * not actually extending ComparisonFailure but still exposing getExpected() and getActual()
             * methods.
             */
            return new AssertionErrorWithFacts(messages, facts, cause);
        }
        Class<? extends AssertionError> asAssertionErrorSubclass =
                comparisonFailureClass.asSubclass(AssertionError.class);

        Constructor<? extends AssertionError> constructor;
        try {
            constructor =
                    asAssertionErrorSubclass.getDeclaredConstructor(
                            ImmutableList.class,
                            ImmutableList.class,
                            String.class,
                            String.class,
                            Throwable.class);
        } catch (NoSuchMethodException e) {
            // That constructor exists.
            throw newLinkageError(e);
        }

        try {
            return constructor.newInstance(messages, facts, expected, actual, cause);
        } catch (InvocationTargetException e) {
            throwIfUnchecked(e.getCause());
            // That constructor has no `throws` clause.
            throw newLinkageError(e);
        } catch (InstantiationException e) {
            // The class is a concrete class.
            throw newLinkageError(e);
        } catch (IllegalAccessException e) {
            // We're accessing a class from within its package.
            throw newLinkageError(e);
        }
    }

    private static LinkageError newLinkageError(Throwable cause) {
        LinkageError error = new LinkageError(cause.toString());
        error.initCause(cause);
        return error;
    }
}
