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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static com.google.common.collect.Iterables.isEmpty;

/**
 * Utility methods used in {@code Subject} implementors.
 *
 * @author Christian Gruber
 * @author Jens Nyman
 */
final class SubjectUtils {
    private SubjectUtils() {
    }

    static final String HUMAN_UNDERSTANDABLE_EMPTY_STRING = "\"\" (empty String)";

    static <T> List<T> accumulate(T first, T second, T... rest) {
        // rest should never be deliberately null, so assume that the caller passed null
        // in the third position but intended it to be the third element in the array of values.
        // Javac makes the opposite inference, so handle that here.
        List<T> items = new ArrayList<T>(2 + ((rest == null) ? 1 : rest.length));
        items.add(first);
        items.add(second);
        if (rest == null) {
            items.add(null);
        } else {
            items.addAll(Arrays.asList(rest));
        }
        return items;
    }

    static String countDuplicates(Iterable<?> items) {
        /*
         * TODO(cpovirk): Remove brackets after migrating all callers to the new message format. But
         * will that look OK when we put the result next to a homogeneous type name? If not, maybe move
         * the homogeneous type name to a separate Fact?
         */
        return toStringWithBrackets(multisetToMap(countDuplicatesToMultiset(items)));
    }

    static String entryString(Object element, int count) {
        String item = String.valueOf(element);
        return (count > 1) ? item + " [" + count + " copies]" : item;
    }

    private static <T> Multiset<T> countDuplicatesToMultiset(Iterable<T> items) {
        // We use avoid hashing in case the elements don't have a proper
        // .hashCode() method (e.g., MessageSet from old versions of protobuf).
        Multiset<T> multiset = LinkedHashMultiset.create();
        for (T item : items) {
            multiset.add(item);
        }
        return multiset;
    }

    /**
     * Makes a String representation of {@code items} with collapsed duplicates and additional class
     * info.
     *
     * <p>Example: {@code countDuplicatesAndAddTypeInfo([1, 2, 2, 3]) == "[1, 2 [3 copies]]
     * (java.lang.Integer)"} and {@code countDuplicatesAndAddTypeInfo([1, 2L]) == "[1
     * (java.lang.Integer), 2 (java.lang.Long)]"}.
     */
    static String countDuplicatesAndAddTypeInfo(Iterable<?> itemsIterable) {
        Collection<?> items = iterableToCollection(itemsIterable);
        Optional<String> homogeneousTypeName = getHomogeneousTypeName(items);

        return homogeneousTypeName.isPresent()
                ? String.format("%s (%s)", countDuplicates(items), homogeneousTypeName.get())
                : countDuplicates(addTypeInfoToEveryItem(items));
    }

    private static Map<?, Integer> multisetToMap(Multiset<?> multiset) {
        LinkedHashMap<Object, Integer> result = new LinkedHashMap<>();
        for (Multiset.Entry<?> entry : multiset.entrySet()) {
            result.put(entry.getElement(), entry.getCount());
        }
        return result;
    }

    /**
     * Similar to {@link #countDuplicatesAndAddTypeInfo} and {@link #countDuplicates} but (a) only
     * adds type info if requested and (b) returns a richer object containing the data.
     */
    static DuplicateGroupedAndTyped countDuplicatesAndMaybeAddTypeInfoReturnObject(
            Iterable<?> itemsIterable, boolean addTypeInfo) {
        if (addTypeInfo) {
            Collection<?> items = iterableToCollection(itemsIterable);
            Optional<String> homogeneousTypeName = getHomogeneousTypeName(items);

            Multiset<?> valuesWithCountsAndMaybeTypes =
                    homogeneousTypeName.isPresent()
                            ? countDuplicatesToMultiset(items)
                            : countDuplicatesToMultiset(addTypeInfoToEveryItem(items));
            return new DuplicateGroupedAndTyped(valuesWithCountsAndMaybeTypes, homogeneousTypeName);
        } else {
            return new DuplicateGroupedAndTyped(
                    countDuplicatesToMultiset(itemsIterable),
                    /* homogeneousTypeToDisplay= */ Optional.empty());
        }
    }

    static String toStringWithBrackets(Map<?, Integer> multiset) {
        List<String> parts = new ArrayList<>();
        for (Map.Entry<?, Integer> entry : multiset.entrySet()) {
            parts.add(entryString(entry.getKey(), entry.getValue()));
        }
        return parts.toString();
    }

    static String toStringWithoutBrackets(Map<?, Integer> multiset) {
        String string = toStringWithBrackets(multiset);
        return string.substring(1, string.length() - 1);
    }


    /**
     * Missing or unexpected values from a collection assertion, with equal objects grouped together
     * and, in some cases, type information added. If the type information is present, it is either
     * present in {@code homogeneousTypeToDisplay} (if all objects have the same type) or appended to
     * each individual element (if some elements have different types).
     *
     * <p>This allows collection assertions to the type information on a separate line from the
     * elements and even to output different elements on different lines.
     */
    static final class DuplicateGroupedAndTyped {
        final Map<?, Integer> valuesAndMaybeTypes;
        final Optional<String> homogeneousTypeToDisplay;

        DuplicateGroupedAndTyped(
                Multiset<?> valuesAndMaybeTypes, Optional<String> homogeneousTypeToDisplay) {
            this.valuesAndMaybeTypes = multisetToMap(valuesAndMaybeTypes);
            this.homogeneousTypeToDisplay = homogeneousTypeToDisplay;
        }

        int totalCopies() {
            return valuesAndMaybeTypes.values().stream()
                    .mapToInt(i -> i).sum();
        }

        boolean isEmpty() {
            return valuesAndMaybeTypes.isEmpty();
        }

        Map<?, Integer> entrySet() {
            return valuesAndMaybeTypes;
        }


        @Override
        public String toString() {
            return homogeneousTypeToDisplay.isPresent()
                    ? toStringWithoutBrackets(valuesAndMaybeTypes) + " (" + homogeneousTypeToDisplay.get() + ")"
                    : toStringWithoutBrackets(valuesAndMaybeTypes);
        }
    }

    /**
     * Returns a new collection containing all elements in {@code items} for which there exists at
     * least one element in {@code itemsToCheck} that has the same {@code toString()} value without
     * being equal.
     *
     * <p>Example: {@code retainMatchingToString([1L, 2L, 2L], [2, 3]) == [2L, 2L]}
     */
    static List<Object> retainMatchingToString(Iterable<?> items, Iterable<?> itemsToCheck) {
        SetMultimap<String, Object> stringValueToItemsToCheck = HashMultimap.create();
        for (Object itemToCheck : itemsToCheck) {
            stringValueToItemsToCheck.put(String.valueOf(itemToCheck), itemToCheck);
        }

        List<Object> result = new ArrayList<>();
        for (Object item : items) {
            for (Object itemToCheck : stringValueToItemsToCheck.get(String.valueOf(item))) {
                if (!Objects.equals(itemToCheck, item)) {
                    result.add(item);
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Returns true if there is a pair of an item from {@code items1} and one in {@code items2} that
     * has the same {@code toString()} value without being equal.
     *
     * <p>Example: {@code hasMatchingToStringPair([1L, 2L], [1]) == true}
     */
    static boolean hasMatchingToStringPair(Iterable<?> items1, Iterable<?> items2) {
        if (isEmpty(items1) || isEmpty(items2)) {
            return false; // Bail early to avoid calling hashCode() on the elements unnecessarily.
        }
        return !retainMatchingToString(items1, items2).isEmpty();
    }

    static String objectToTypeName(Object item) {
        // TODO(cpovirk): Merge this with the code in Subject.failEqualityCheck().
        if (item == null) {
            // The name "null type" comes from the interface javax.lang.model.type.NullType.
            return "null type";
        } else if (item instanceof Map.Entry) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) item;
            // Fix for interesting bug when entry.getValue() returns itself b/170390717
            String valueTypeName =
                    entry.getValue() == entry ? "Map.Entry" : objectToTypeName(entry.getValue());

            return String.format("Map.Entry<%s, %s>", objectToTypeName(entry.getKey()), valueTypeName);
        } else {
            return item.getClass().getName();
        }
    }

    /**
     * Returns the name of the single type of all given items or {@link Optional#empty()} if no such
     * type exists.
     */
    private static Optional<String> getHomogeneousTypeName(Iterable<?> items) {
        Optional<String> homogeneousTypeName = Optional.empty();
        for (Object item : items) {
            if (item == null) {
                /*
                 * TODO(cpovirk): Why? We could have multiple nulls, which would be homogeneous. More
                 * likely, we could have exactly one null, which is still homogeneous. Arguably it's weird
                 * to call a single element "homogeneous" at all, but that's not specific to null.
                 */
                return Optional.empty();
            } else if (!homogeneousTypeName.isPresent()) {
                // This is the first item
                homogeneousTypeName = Optional.of(objectToTypeName(item));
            } else if (!objectToTypeName(item).equals(homogeneousTypeName.get())) {
                // items is a heterogeneous collection
                return Optional.empty();
            }
        }
        return homogeneousTypeName;
    }

    private static List<String> addTypeInfoToEveryItem(Iterable<?> items) {
        List<String> itemsWithTypeInfo = new ArrayList<>();
        for (Object item : items) {
            itemsWithTypeInfo.add(String.format("%s (%s)", item, objectToTypeName(item)));
        }
        return itemsWithTypeInfo;
    }

    static <T> Collection<T> iterableToCollection(Iterable<T> iterable) {
        if (iterable instanceof Collection) {
            // Should be safe to assume that any Iterable implementing Collection isn't a one-shot
            // iterable, right? I sure hope so.
            return (Collection<T>) iterable;
        } else {
            ArrayList<T> result = new ArrayList<>();
            iterable.forEach(result::add);
            return result;
        }
    }

    static <T> List<T> iterableToList(Iterable<T> iterable) {
        if (iterable instanceof List) {
            return (List<T>) iterable;
        } else {
            ArrayList<T> result = new ArrayList<>();
            iterable.forEach(result::add);
            return result;
        }
    }

    /**
     * Returns an iterable with all empty strings replaced by a non-empty human understandable
     * indicator for an empty string.
     *
     * <p>Returns the given iterable if it contains no empty strings.
     */
    static <T> Iterable<T> annotateEmptyStrings(Iterable<T> items) {
        boolean containsEmptyString = StreamSupport.stream(items.spliterator(), false)
                .anyMatch(""::equals);
        if (containsEmptyString) {
            List<T> annotatedItems = new ArrayList<>();
            for (T item : items) {
                if (Objects.equals(item, "")) {
                    // This is a safe cast because know that at least one instance of T (this item) is a
                    // String.
                    @SuppressWarnings("unchecked")
                    T newItem = (T) HUMAN_UNDERSTANDABLE_EMPTY_STRING;
                    annotatedItems.add(newItem);
                } else {
                    annotatedItems.add(item);
                }
            }
            return annotatedItems;
        } else {
            return items;
        }
    }

    @SafeVarargs
    static <E> List<E> concat(Iterable<? extends E>... inputs) {
        List<E> result = new ArrayList<>();
        for (Iterable<? extends E> it : inputs) {
            it.forEach(result::add);
        }
        return result;
    }

    static <E> List<E> append(E[] array, E object) {
        List<E> result = new ArrayList<>();
        Collections.addAll(result, array);
        result.add(object);
        return result;
    }

    static <E> List<E> append(List<? extends E> list, E object) {
        List<E> result = new ArrayList<>(list);
        result.add(object);
        return result;
    }

    static <E> List<E> sandwich(E first, E[] array, E last) {
        List<E> result = new ArrayList<>();
        result.add(first);
        Collections.addAll(result, array);
        result.add(last);
        return result;
    }
}
