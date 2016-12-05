package com.carloseduardo.github.helper;

import com.carloseduardo.github.application.GitHubTrendApplication;
import com.carloseduardo.github.constants.PreferencesKey;
import com.carloseduardo.github.data.preferences.Preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

public class GitHubAPIHelper {

    @Inject
    StringHelper stringHelper;

    @Inject
    Preferences preferences;

    @Inject
    public GitHubAPIHelper() {

        GitHubTrendApplication.getComponent()
                .inject(this);
    }

    public void extractAndSaveLinkHeaderValues(String links) {

        Pattern pattern = Pattern.compile("<(.*?)>");
        Matcher matcher = pattern.matcher(links);
        List<String> nextLinks = new ArrayList<>();

        while (matcher.find()) {
            nextLinks.add(matcher.group());
        }

        if (nextLinks.size() > 0) {

            String nextPage = stringHelper.removeTag(nextLinks.get(0));
            int nextPageNumber = stringHelper.extractPageParameterValue(nextLinks.get(0));
            int lastPageNumber = stringHelper.extractPageParameterValue(nextLinks.get(1));

            preferences.putString(PreferencesKey.NEXT_PAGE_URL, nextPage);
            preferences.putInt(PreferencesKey.NEXT_PAGE_NUMBER, nextPageNumber);
            preferences.putInt(PreferencesKey.LAST_PAGE_NUMBER, lastPageNumber);
        }
    }
}