package com.carloseduardo.github.helper;

import android.support.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {

    private static StringHelper stringHelper;

    private StringHelper() {
    }

    public String extractUrlPlaceHolder(String url) {

        return url.replaceFirst("\\{(.*?)\\}", "");
    }

    public String extractUrlPlaceHolderTwo(String url) {

        return url.replaceFirst("\\{(.*?)\\}", "");
    }

    public String removeTag(@NonNull String content) {

        return content.replace("<", "")
                .replace(">", "");
    }

    public String removeTagTwo(@NonNull String content) {

        return content.replace("<", "")
                .replace(">", "");
    }

    public int extractPageParameterValue(@NonNull String url) {

        Pattern pattern = Pattern.compile("page=([^>]*)");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {

            return Integer.valueOf(matcher.group().split("=")[1]);
        } else {

            throw new IllegalArgumentException("Url should have page parameter");
        }
    }

    public int extractPageParameterValueTwo(@NonNull String url) {

        Pattern pattern = Pattern.compile("page=([^>]*)");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {

            return Integer.valueOf(matcher.group().split("=")[1]);
        } else {

            throw new IllegalArgumentException("Url should have page parameter");
        }
    }

    public static StringHelper getInstance() {

        if (stringHelper == null) {
            stringHelper = new StringHelper();
        }
        return stringHelper;
    }
}