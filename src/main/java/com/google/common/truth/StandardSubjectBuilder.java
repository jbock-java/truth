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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * In a fluent assertion chain, an object with which you can do any of the following:
 *
 * <ul>
 *   <li>Set an optional message with {@link #withMessage}.
 *   <li>Specify the type of {@code Subject} to create with {@link #about(Subject.Factory)}.
 *   <li>For the types of {@code Subject} built into Truth, directly specify the value under test
 *       with {@link #that(Object)}.
 * </ul>
 *
 * <p>For more information about the methods in this class, see <a
 * href="https://truth.dev/faq#full-chain">this FAQ entry</a>.
 *
 * <h3>For people extending Truth</h3>
 *
 * <p>You won't extend this type. When you write a custom subject, see <a
 * href="https://truth.dev/extension">our doc on extensions</a>.
 */
public class StandardSubjectBuilder {
    /**
     * Returns a new instance that invokes the given {@code FailureStrategy} when a check fails. Most
     * users should not need this. If you think you do, see the documentation on {@link
     * FailureStrategy}.
     */
    public static StandardSubjectBuilder forCustomFailureStrategy(FailureStrategy failureStrategy) {
        return new StandardSubjectBuilder(FailureMetadata.forFailureStrategy(failureStrategy));
    }

    private final FailureMetadata metadataDoNotReferenceDirectly;

    StandardSubjectBuilder(FailureMetadata metadata) {
        this.metadataDoNotReferenceDirectly = requireNonNull(metadata);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public final <ComparableT extends Comparable<?>> ComparableSubject<ComparableT> that(
            ComparableT actual) {
        return new ComparableSubject(metadata(), actual) {
        };
    }

    public final BigDecimalSubject that(BigDecimal actual) {
        return new BigDecimalSubject(metadata(), actual);
    }

    public final Subject that(Object actual) {
        return new Subject(metadata(), actual);
    }

    public final ClassSubject that(Class<?> actual) {
        return new ClassSubject(metadata(), actual);
    }

    public final ThrowableSubject that(Throwable actual) {
        return new ThrowableSubject(metadata(), actual, "throwable");
    }

    public final LongSubject that(Long actual) {
        return new LongSubject(metadata(), actual);
    }

    public final DoubleSubject that(Double actual) {
        return new DoubleSubject(metadata(), actual);
    }

    public final FloatSubject that(Float actual) {
        return new FloatSubject(metadata(), actual);
    }

    public final IntegerSubject that(Integer actual) {
        return new IntegerSubject(metadata(), actual);
    }

    public final BooleanSubject that(Boolean actual) {
        return new BooleanSubject(metadata(), actual);
    }

    public final StringSubject that(String actual) {
        return new StringSubject(metadata(), actual);
    }

    public final IterableSubject that(Iterable<?> actual) {
        return new IterableSubject(metadata(), actual);
    }

    public final <T> ObjectArraySubject<T> that(T[] actual) {
        return new ObjectArraySubject<>(metadata(), actual, "array");
    }

    public final PrimitiveBooleanArraySubject that(boolean[] actual) {
        return new PrimitiveBooleanArraySubject(metadata(), actual, "array");
    }

    public final PrimitiveShortArraySubject that(short[] actual) {
        return new PrimitiveShortArraySubject(metadata(), actual, "array");
    }

    public final PrimitiveIntArraySubject that(int[] actual) {
        return new PrimitiveIntArraySubject(metadata(), actual, "array");
    }

    public final PrimitiveLongArraySubject that(long[] actual) {
        return new PrimitiveLongArraySubject(metadata(), actual, "array");
    }

    public final PrimitiveCharArraySubject that(char[] actual) {
        return new PrimitiveCharArraySubject(metadata(), actual, "array");
    }

    public final PrimitiveByteArraySubject that(byte[] actual) {
        return new PrimitiveByteArraySubject(metadata(), actual, "array");
    }

    public final PrimitiveFloatArraySubject that(float[] actual) {
        return new PrimitiveFloatArraySubject(metadata(), actual, "array");
    }

    public final PrimitiveDoubleArraySubject that(double[] actual) {
        return new PrimitiveDoubleArraySubject(metadata(), actual, "array");
    }

    public final OptionalSubject that(Optional<?> actual) {
        return new OptionalSubject(metadata(), actual, "optional");
    }

    public final MapSubject that(Map<?, ?> actual) {
        return new MapSubject(metadata(), actual);
    }

    /**
     * Returns a new instance that will output the given message before the main failure message. If
     * this method is called multiple times, the messages will appear in the order that they were
     * specified.
     */
    public final StandardSubjectBuilder withMessage(String messageToPrepend) {
        return withMessage("%s", messageToPrepend);
    }

    /**
     * Returns a new instance that will output the given message before the main failure message. If
     * this method is called multiple times, the messages will appear in the order that they were
     * specified.
     *
     * <p><b>Note:</b> the arguments will be substituted into the format template using {@link
     * com.google.common.base.Strings#lenientFormat Strings.lenientFormat}. Note this only supports
     * the {@code %s} specifier.
     *
     * @throws IllegalArgumentException if the number of placeholders in the format string does not
     *     equal the number of given arguments
     */
    public final StandardSubjectBuilder withMessage(String format, Object... args) {
        return new StandardSubjectBuilder(metadata().withMessage(format, args));
    }

    /**
     * Given a factory for some {@code Subject} class, returns a builder whose {@code that(actual)}
     * method creates instances of that class. Created subjects use the previously set failure
     * strategy and any previously set failure message.
     */
    public final <S extends Subject, A> SimpleSubjectBuilder<S, A> about(
            Subject.Factory<S, A> factory) {
        return new SimpleSubjectBuilder<>(metadata(), factory);
    }

    public final <CustomSubjectBuilderT extends CustomSubjectBuilder> CustomSubjectBuilderT about(
            CustomSubjectBuilder.Factory<CustomSubjectBuilderT> factory) {
        return factory.createSubjectBuilder(metadata());
    }

    /**
     * Reports a failure.
     *
     * <p>To set a message, first call {@link #withMessage} (or, more commonly, use the shortcut
     * {@link Truth#assertWithMessage}).
     */
    public final void fail() {
        metadata().fail(List.of());
    }

    private FailureMetadata metadata() {
        checkStatePreconditions();
        return metadataDoNotReferenceDirectly;
    }

    /**
     * Extension point invoked before every assertion. This allows {@link Expect} to check that it's
     * been set up properly as a {@code TestRule}.
     */
    void checkStatePreconditions() {
    }
}
