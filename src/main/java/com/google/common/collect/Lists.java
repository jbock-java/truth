package com.google.common.collect;

import java.util.ArrayList;
import java.util.Iterator;

public class Lists {

    public static <E> ArrayList<E> newArrayList(
            Iterator<? extends E> elements) {
        ArrayList<E> list = new ArrayList<>();
        Iterators.addAll(list, elements);
        return list;
    }
}
