package com.carloseduardo.github.helper;

import android.support.annotation.Nullable;

import java.util.List;

public class PaginationHelper {

    private static PaginationHelper paginationHelper;

    private PaginationHelper() {
    }

    @Nullable
    public <T> List<T> pagination(int firstPosition, int lastPosition, List<T> items) {

        if ((lastPosition > items.size() && items.isEmpty())
                || firstPosition > items.size() - 1) {

            return null;
        } else if (lastPosition > items.size()) {

            lastPosition = items.size();
        }
        return items.size() >= lastPosition && firstPosition <= items.size()
                ? items.subList(firstPosition, lastPosition)
                : null;
    }

    public static PaginationHelper getInstance() {

        if (paginationHelper == null) {

            paginationHelper = new PaginationHelper();
        }
        return paginationHelper;
    }
}
