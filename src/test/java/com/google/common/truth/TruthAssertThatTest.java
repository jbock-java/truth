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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.reflect.TypeToken;
import java.util.Set;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static com.google.common.truth.Truth.assert_;
import static java.util.Arrays.asList;

/**
 * Tests for the FEST-alike assertThat() entry point.
 *
 * @author Christian Gruber (cgruber@israfil.net)
 */
class TruthAssertThatTest {
    private static final Function<Method, TypeToken<?>> METHOD_TO_RETURN_TYPE_TOKEN =
            input -> TypeToken.of(Iterables.getOnlyElement(asList(input.getParameterTypes())));

    @Test
    void staticAssertThatMethodsMatchStandardSubjectBuilderInstanceMethods() {
        Set<TypeToken<?>> verbTypes =
                FluentIterable.from(asList(StandardSubjectBuilder.class.getMethods()))
                        .filter(
                                new Predicate<Method>() {
                                    @Override
                                    public boolean apply(Method input) {
                                        return input.getName().equals("that");
                                    }
                                })
                        .transform(METHOD_TO_RETURN_TYPE_TOKEN)
                        .toSortedSet(Ordering.usingToString());
        Set<TypeToken<?>> truthTypes =
                FluentIterable.from(asList(Truth.class.getMethods()))
                        .filter(
                                new Predicate<Method>() {
                                    @Override
                                    public boolean apply(Method input) {
                                        return input.getName().equals("assertThat")
                                                && Modifier.isStatic(input.getModifiers());
                                    }
                                })
                        .transform(METHOD_TO_RETURN_TYPE_TOKEN)
                        .toSortedSet(Ordering.usingToString());

        assert_().that(verbTypes).isNotEmpty();
        assert_().that(truthTypes).isNotEmpty();
        assert_().that(truthTypes).containsExactlyElementsIn(verbTypes);
    }
}
