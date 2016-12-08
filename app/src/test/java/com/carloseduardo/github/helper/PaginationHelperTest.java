package com.carloseduardo.github.helper;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class PaginationHelperTest {

    @Test
    public void shouldReturnTenResults() {

        PaginationHelper paginationHelper = PaginationHelper.getInstance();
        List<String> strings = getList(15);
        List<String> stringsPaginated = paginationHelper.pagination(0, 10, strings);

        assertTrue(stringsPaginated.size() == 10);
    }

    @Test
    public void shouldReturnAllDataWhenLastPositionIsBiggerThanListSize() {

        PaginationHelper paginationHelper = PaginationHelper.getInstance();
        List<String> strings = getList(15);
        List<String> stringsPaginated = paginationHelper.pagination(0, 20, strings);

        assertTrue(stringsPaginated.size() == 15);
    }

    @Test
    public void shouldReturnAllDataFromFirstPositionToLastPosition() {

        PaginationHelper paginationHelper = PaginationHelper.getInstance();
        List<String> strings = getList(15);
        List<String> stringsPaginated = paginationHelper.pagination(10, 20, strings);

        assertTrue(stringsPaginated.size() == 5);
    }

    @Test
    public void shouldReturnNullWhenListIsEmpty() {

        PaginationHelper paginationHelper = PaginationHelper.getInstance();
        List<String> strings = Collections.emptyList();
        List<String> stringsPaginated = paginationHelper.pagination(10, 20, strings);

        assertTrue(stringsPaginated == null);
    }

    @Test
    public void shouldReturnNullWhenFirstPositionIsBiggerListSize() {

        PaginationHelper paginationHelper = PaginationHelper.getInstance();
        List<String> strings = getList(20);
        List<String> stringsPaginated = paginationHelper.pagination(25, 20, strings);

        assertTrue(stringsPaginated == null);
    }

    @Test
    public void shouldReturnNullWhenFirstPositionIsEqualListSize() {

        PaginationHelper paginationHelper = PaginationHelper.getInstance();
        List<String> strings = getList(20);
        List<String> stringsPaginated = paginationHelper.pagination(20, 20, strings);

        assertTrue(stringsPaginated == null);
    }

    private List<String> getList(int size) {

        List<String> strings = new ArrayList<>();

        for (int i = 0; i < size; i++) {

            strings.add(String.valueOf(i));
        }
        return strings;
    }
}