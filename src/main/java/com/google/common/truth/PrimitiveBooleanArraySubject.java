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

import com.google.common.primitives.Booleans;

/**
 * A Subject for {@code boolean[]}.
 *
 * @author Christian Gruber (cgruber@israfil.net)
 */
public final class PrimitiveBooleanArraySubject extends AbstractArraySubject {
    private final boolean[] actual;

    PrimitiveBooleanArraySubject(
            FailureMetadata metadata, boolean[] o, String typeDescription) {
        super(metadata, o, typeDescription);
        this.actual = o;
    }

    public IterableSubject asList() {
        return checkNoNeedToDisplayBothValues("asList()").that(Booleans.asList(actual));
    }
}
