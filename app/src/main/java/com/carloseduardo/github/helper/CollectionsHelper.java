package com.carloseduardo.github.helper;

import java.util.ArrayList;
import java.util.List;

public class CollectionsHelper {

    public <T> List<T> toModifiableList(List<T> unmodifiableList) {

        return new ArrayList<>(unmodifiableList);
    }
}