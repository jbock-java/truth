/*
 * Copyright (c) 2019 Google, Inc.
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

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for {@link Correspondence.ExceptionStore}.
 *
 * <p>These should not be run under j2cl, because the descriptions don't include the expected stack
 * traces there.
 *
 * @author Pete Gillin
 */
final class CorrespondenceExceptionStoreTest {

    @Test
    void hasCompareException_empty() {
        Correspondence.ExceptionStore exceptions = Correspondence.ExceptionStore.forIterable();
        assertThat(exceptions.hasCompareException()).isFalse();
    }

    @Test
    void hasCompareException_hasCompareException() {
        Correspondence.ExceptionStore exceptions = Correspondence.ExceptionStore.forIterable();
        addCompareException(exceptions);
        assertThat(exceptions.hasCompareException()).isTrue();
    }

    @Test
    void describeAsMainCause_empty() {
        Correspondence.ExceptionStore exceptions = Correspondence.ExceptionStore.forIterable();
        try {
            exceptions.describeAsMainCause();
            fail("Expected IllegalStateException");
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    void describeAsAdditionalInfo_empty() {
        Correspondence.ExceptionStore exceptions = Correspondence.ExceptionStore.forIterable();
        assertThat(exceptions.describeAsAdditionalInfo()).isEmpty();
    }

    /** Adds a somewhat realistic exception from {@link Correspondence#compare} to the given store. */
    private static void addCompareException(Correspondence.ExceptionStore exceptions) {
        try {
            boolean unused = TestCorrespondences.WITHIN_10_OF.compare(null, 123);
        } catch (RuntimeException e) {
            exceptions.addCompareException(CorrespondenceExceptionStoreTest.class, e, null, 123);
        }
    }
}
