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

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/**
 * The primary entry point for <a href="https://truth.dev">Truth</a>, a library for fluent test
 * assertions.
 *
 * <p>Compare these example JUnit assertions...
 *
 * <pre>{@code
 * assertEquals(b, a);
 * assertTrue(c);
 * assertTrue(d.contains(a));
 * assertTrue(d.contains(a) && d.contains(b));
 * assertTrue(d.contains(a) || d.contains(b) || d.contains(c));
 * }</pre>
 *
 * ...to their Truth equivalents...
 *
 * <pre>{@code
 * assertThat(a).isEqualTo(b);
 * assertThat(c).isTrue();
 * assertThat(d).contains(a);
 * assertThat(d).containsAtLeast(a, b);
 * assertThat(d).containsAnyOf(a, b, c);
 * }</pre>
 *
 * <p>Advantages of Truth:
 *
 * <ul>
 *   <li>aligns all the "actual" values on the left
 *   <li>produces more detailed failure messages
 *   <li>provides richer operations (like {@link IterableSubject#containsExactly})
 * </ul>
 *
 * <p>For more information about the methods in this class, see <a
 * href="https://truth.dev/faq#full-chain">this FAQ entry</a>.
 *
 * <p><b>For people extending Truth</b>
 *
 * <p>The most common way to extend Truth is to write a custom {@link Subject}. (The other, much
 * less common way is to write a custom {@link FailureStrategy}.) For more information, visit those
 * types' docs.
 *
 * @author David Saff
 * @author Christian Gruber (cgruber@israfil.net)
 */
public final class Truth {
    private Truth() {
    }

    private static final FailureStrategy THROW_ASSERTION_ERROR =
            new FailureStrategy() {
                @Override
                public void fail(AssertionError failure) {
                    throw failure;
                }
            };

    private static final StandardSubjectBuilder ASSERT =
            StandardSubjectBuilder.forCustomFailureStrategy(THROW_ASSERTION_ERROR);

    /**
     * Begins a call chain with the fluent Truth API. If the check made by the chain fails, it will
     * throw {@link AssertionError}.
     */
    public static StandardSubjectBuilder assert_() {
        return ASSERT;
    }

    /**
     * Begins an assertion that, if it fails, will prepend the given message to the failure message.
     *
     * <p>This method is a shortcut for {@code assert_().withMessage(...)}.
     *
     * <p>To set a message when using a custom subject, use {@code assertWithMessage(...).}{@link
     * StandardSubjectBuilder#about about(...)}, as discussed in <a
     * href="https://truth.dev/faq#java8">this FAQ entry</a>.
     */
    public static StandardSubjectBuilder assertWithMessage(String messageToPrepend) {
        return assert_().withMessage(messageToPrepend);
    }

    /**
     * Begins an assertion that, if it fails, will prepend the given message to the failure message.
     *
     * <p><b>Note:</b> Note this only supports the {@code %s} specifier.
     *
     * <p>This method is a shortcut for {@code assert_().withMessage(...)}.
     *
     * <p>To set a message when using a custom subject, use {@code assertWithMessage(...).}{@link
     * StandardSubjectBuilder#about about(...)}, as discussed in <a
     * href="https://truth.dev/faq#java8">this FAQ entry</a>.
     *
     * @throws IllegalArgumentException if the number of placeholders in the format string does not
     *     equal the number of given arguments
     */
    public static StandardSubjectBuilder assertWithMessage(String format, Object... args) {
        return assert_().withMessage(format, args);
    }

    /**
     * Given a factory for some {@code Subject} class, returns a builder whose {@code that(actual)}
     * method creates instances of that class.
     */
    public static <S extends Subject, T> SimpleSubjectBuilder<S, T> assertAbout(
            Subject.Factory<S, T> factory) {
        return assert_().about(factory);
    }

    /**
     * A generic, advanced method of extension of Truth to new types, which is documented on {@link
     * CustomSubjectBuilder}. Extension creators should prefer {@link Subject.Factory} if possible.
     */
    public static <CustomSubjectBuilderT extends CustomSubjectBuilder>
    CustomSubjectBuilderT assertAbout(
            CustomSubjectBuilder.Factory<CustomSubjectBuilderT> factory) {
        return assert_().about(factory);
    }

    public static <T extends Comparable<?>> ComparableSubject<T> assertThat(T actual) {
        return assert_().that(actual);
    }

    public static BigDecimalSubject assertThat(BigDecimal actual) {
        return assert_().that(actual);
    }

    public static Subject assertThat(Object actual) {
        return assert_().that(actual);
    }

    public static ClassSubject assertThat(Class<?> actual) {
        return assert_().that(actual);
    }

    public static ThrowableSubject assertThat(Throwable actual) {
        return assert_().that(actual);
    }

    public static LongSubject assertThat(Long actual) {
        return assert_().that(actual);
    }

    public static DoubleSubject assertThat(Double actual) {
        return assert_().that(actual);
    }

    public static FloatSubject assertThat(Float actual) {
        return assert_().that(actual);
    }

    public static IntegerSubject assertThat(Integer actual) {
        return assert_().that(actual);
    }

    public static BooleanSubject assertThat(Boolean actual) {
        return assert_().that(actual);
    }

    public static StringSubject assertThat(String actual) {
        return assert_().that(actual);
    }

    public static IterableSubject assertThat(Iterable<?> actual) {
        return assert_().that(actual);
    }

    public static <T> ObjectArraySubject<T> assertThat(T[] actual) {
        return assert_().that(actual);
    }

    public static PrimitiveBooleanArraySubject assertThat(boolean[] actual) {
        return assert_().that(actual);
    }

    public static PrimitiveShortArraySubject assertThat(short[] actual) {
        return assert_().that(actual);
    }

    public static PrimitiveIntArraySubject assertThat(int[] actual) {
        return assert_().that(actual);
    }

    public static PrimitiveLongArraySubject assertThat(long[] actual) {
        return assert_().that(actual);
    }

    public static PrimitiveByteArraySubject assertThat(byte[] actual) {
        return assert_().that(actual);
    }

    public static PrimitiveCharArraySubject assertThat(char[] actual) {
        return assert_().that(actual);
    }

    public static PrimitiveFloatArraySubject assertThat(float[] actual) {
        return assert_().that(actual);
    }

    public static PrimitiveDoubleArraySubject assertThat(double[] actual) {
        return assert_().that(actual);
    }

    public static OptionalSubject assertThat(Optional<?> actual) {
        return Truth8.assertThat(actual);
    }

    public static MapSubject assertThat(Map<?, ?> actual) {
        return assert_().that(actual);
    }

    /**
     * An {@code AssertionError} that (a) always supports a cause, even under old versions of Android
     * and (b) omits "java.lang.AssertionError:" from the beginning of its toString() representation.
     */
    // TODO(cpovirk): Consider eliminating this, adding its functionality to AssertionErrorWithFacts?
    @SuppressWarnings("OverrideThrowableToString") // We intentionally replace the normal format.
    static final class SimpleAssertionError extends AssertionError {
        /** Separate cause field, in case initCause() fails. */
        private final Throwable cause;

        private SimpleAssertionError(String message, Throwable cause) {
            super(requireNonNull(message));
            this.cause = cause;

            try {
                initCause(cause);
            } catch (IllegalStateException alreadyInitializedBecauseOfHarmonyBug) {
                /*
                 * initCause() throws under old versions of Android:
                 * https://issuetracker.google.com/issues/36945167
                 *
                 * Yes, it's *nice* if initCause() works:
                 *
                 * - It ensures that, if someone tries to call initCause() later, the call will fail loudly
                 *   rather than be silently ignored.
                 *
                 * - It populates the usual `Throwable.cause` field, where users of debuggers and other
                 *   tools are likely to look first.
                 *
                 * But if it doesn't work, that's fine: Most consumers of the cause should be retrieving it
                 * through getCause(), which we've overridden to return *our* `cause` field, which we've
                 * populated with the correct value.
                 */
            }
        }

        static SimpleAssertionError create(String message, Throwable cause) {
            return new SimpleAssertionError(message, cause);
        }

        static SimpleAssertionError createWithNoStack(String message, Throwable cause) {
            SimpleAssertionError error = create(message, cause);
            error.setStackTrace(new StackTraceElement[0]);
            return error;
        }

        static SimpleAssertionError createWithNoStack(String message) {
            return createWithNoStack(message, /*cause=*/ null);
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
    }
}
