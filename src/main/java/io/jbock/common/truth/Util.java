package io.jbock.common.truth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Util {

    private Util() {
    }

    static List<Boolean> booleansAsList(boolean... backingArray) {
        List<Boolean> result = new ArrayList<>(backingArray.length);
        for (boolean i : backingArray) {
            result.add(i);
        }
        return result;
    }

    static List<Integer> intsAsList(int... backingArray) {
        List<Integer> result = new ArrayList<>(backingArray.length);
        for (int i : backingArray) {
            result.add(i);
        }
        return result;
    }

    static List<Long> longsAsList(long... backingArray) {
        List<Long> result = new ArrayList<>(backingArray.length);
        for (long i : backingArray) {
            result.add(i);
        }
        return result;
    }

    static List<Short> shortsAsList(short... backingArray) {
        List<Short> result = new ArrayList<>(backingArray.length);
        for (short i : backingArray) {
            result.add(i);
        }
        return result;
    }

    static List<Float> floatsAsList(float... backingArray) {
        List<Float> result = new ArrayList<>(backingArray.length);
        for (float i : backingArray) {
            result.add(i);
        }
        return result;
    }

    static List<Double> doublesAsList(double... backingArray) {
        List<Double> result = new ArrayList<>(backingArray.length);
        for (double i : backingArray) {
            result.add(i);
        }
        return result;
    }

    static List<Byte> bytesAsList(byte... backingArray) {
        List<Byte> result = new ArrayList<>(backingArray.length);
        for (byte i : backingArray) {
            result.add(i);
        }
        return result;
    }

    static List<Character> charsAsList(char... backingArray) {
        List<Character> result = new ArrayList<>(backingArray.length);
        for (char i : backingArray) {
            result.add(i);
        }
        return result;
    }

    static String padEnd(String string, int minLength, char padChar) {
        if (string.length() >= minLength) {
            return string;
        }
        int paddingLength = minLength - string.length();
        String padding = String.valueOf(padChar).repeat(paddingLength);
        return string + padding;
    }

    static <E> List<E> iterableToList(Iterable<E> iterable) {
        List<E> result = new ArrayList<>();
        iterable.forEach(result::add);
        return result;
    }

    static <E> Set<E> intersection(Set<E> set1, Set<?> set2) {
        return set1.stream()
                .filter(set2::contains)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    static <E> Set<E> union(Set<? extends E> set1, Set<? extends E> set2) {
        LinkedHashSet<E> result = Stream.concat(
                        set1.stream(), set2.stream().filter((E e) -> !set1.contains(e)))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return Collections.unmodifiableSet(result);
    }
}
