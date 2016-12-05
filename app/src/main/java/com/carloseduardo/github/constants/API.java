package com.carloseduardo.github.constants;

public interface API {

    String ENDPOINT = "https://api.github.com";
    String FIRST_SEARCH_URL = "/search/repositories?q=language:java&sort=stars&order=desc&page=1";
    String NEXT_SEARCH_HEADER = "Link";
}