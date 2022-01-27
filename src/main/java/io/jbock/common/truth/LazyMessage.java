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

import java.util.ArrayList;
import java.util.List;

import static io.jbock.common.truth.Preconditions.checkArgument;

final class LazyMessage {
    private static final String PLACEHOLDER_ERR =
            "Incorrect number of args (%d) for the given placeholders (%d) in string template:\"%s\"";

    private final String format;
    private final Object[] args;

    LazyMessage(String format, Object... args) {
        this.format = format;
        this.args = args;
        int placeholders = countPlaceholders(format);
        checkArgument(placeholders == args.length, String.format(PLACEHOLDER_ERR, args.length, placeholders, format));
    }

    @Override
    public String toString() {
        return String.format(format, args);
    }

    static int countPlaceholders(String template) {
        int index = 0;
        int count = 0;
        while (true) {
            index = template.indexOf("%s", index);
            if (index == -1) {
                break;
            }
            index++;
            count++;
        }
        return count;
    }

    static List<String> evaluateAll(List<LazyMessage> messages) {
        List<String> result = new ArrayList<>();
        for (LazyMessage message : messages) {
            result.add(message.toString());
        }
        return result;
    }
}
