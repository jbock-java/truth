package com.google.common.truth;

import java.util.ArrayList;
import java.util.List;

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
}
