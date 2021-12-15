/*
 * Copyright (c) 2016 Google, Inc.
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

import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertAbout;

/**
 * The primary entry point for assertions about Java 8 types.
 *
 * <p>To use {@link Truth#assertWithMessage} with a Java 8 type, use {@code
 * assertWithMessage(...).about(}{@link OptionalSubject#optionals optionals()}{@code ).that(...)}
 * (or similarly for the other types).
 *
 * <p>For more information about combining different messages, failure strategies, and subjects, see
 * <a href="https://truth.dev/faq#full-chain">How do I specify a custom message/failure
 * behavior/{@code Subject} type?</a> in the Truth FAQ.
 */
public final class Truth8 {
    public static OptionalSubject assertThat(Optional<?> target) {
        return assertAbout(OptionalSubject.optionals()).that(target);
    }

    public static OptionalIntSubject assertThat(OptionalInt target) {
        return assertAbout(OptionalIntSubject.optionalInts()).that(target);
    }

    public static OptionalLongSubject assertThat(OptionalLong target) {
        return assertAbout(OptionalLongSubject.optionalLongs()).that(target);
    }

    public static OptionalDoubleSubject assertThat(OptionalDouble target) {
        return assertAbout(OptionalDoubleSubject.optionalDoubles()).that(target);
    }

    public static StreamSubject assertThat(Stream<?> target) {
        return assertAbout(StreamSubject.streams()).that(target);
    }

    public static IntStreamSubject assertThat(IntStream target) {
        return assertAbout(IntStreamSubject.intStreams()).that(target);
    }

    public static LongStreamSubject assertThat(LongStream target) {
        return assertAbout(LongStreamSubject.longStreams()).that(target);
    }

    // TODO(b/64757353): Add support for DoubleStream?

    // Not actually a Java 8 feature, but for now this is the best option since core Truth still has
    // to support Java environments without java.nio.file such as Android and J2CL.
    public static PathSubject assertThat(Path target) {
        return assertAbout(PathSubject.paths()).that(target);
    }

    private Truth8() {
    }
}
