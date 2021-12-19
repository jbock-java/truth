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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assert_;
import static java.util.Arrays.asList;

/**
 * Tests for the FEST-alike assertThat() entry point.
 *
 * @author Christian Gruber (cgruber@israfil.net)
 */
class TruthAssertThatTest {
    private static final Function<Method, String> METHOD_TO_RETURN_TYPE_TOKEN =
            input -> asList(input.getParameterTypes()).get(0/* expecting exactly one parameter */)
                    .getCanonicalName();

    @Test
    void staticAssertThatMethodsMatchStandardSubjectBuilderInstanceMethods() {
        Set<String> verbTypes =
                Arrays.stream(StandardSubjectBuilder.class.getMethods())
                        .filter(input -> input.getName().equals("that"))
                        .map(METHOD_TO_RETURN_TYPE_TOKEN)
                        .collect(Collectors.toCollection(TreeSet::new));

        Set<String> truthTypes =
                Arrays.stream(Truth.class.getMethods())
                        .filter(input -> input.getName().equals("assertThat")
                                && Modifier.isStatic(input.getModifiers()))
                        .map(METHOD_TO_RETURN_TYPE_TOKEN)
                        .collect(Collectors.toCollection(TreeSet::new));

        assert_().that(verbTypes).isNotEmpty();
        assert_().that(truthTypes).isNotEmpty();
        assert_().that(truthTypes).containsExactlyElementsIn(verbTypes);
    }
}
