package com.carloseduardo.github.helper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class StringHelperTest {

    @Test
    public void shouldRemoveTagFromHeaderUrl() {

        StringHelper stringHelper = StringHelper.getInstance();
        String tagUrl = "<https://api.github.com/search/repositories?q=language%3Ajava&sort=stars&order=desc&page=2>";
        String url = stringHelper.removeTag(tagUrl);

        assertFalse(url.contains(">"));
        assertFalse(url.contains("<"));
    }

    @Test
    public void shouldRemovePlaceHolderFromPullUrl() {

        StringHelper stringHelper = StringHelper.getInstance();
        String pullUrl = "https://api.github.com/repos/elastic/elasticsearch/pulls{/number}";
        String url = stringHelper.extractUrlPlaceHolder(pullUrl);

        assertFalse(url.contains("{"));
        assertFalse(url.contains("}"));
    }

    @Test
    public void shouldRemovePlaceHolderFromCommitUrl() {

        StringHelper stringHelper = StringHelper.getInstance();
        String commitUrl = "https://api.github.com/repos/elastic/elasticsearch/git/commits{/sha}";
        String url = stringHelper.extractUrlPlaceHolder(commitUrl);

        assertFalse(url.contains("{"));
        assertFalse(url.contains("}"));
    }

    @Test
    public void shouldExtractPageParameterValue() {

        StringHelper stringHelper = StringHelper.getInstance();
        String searchUrl = "https://api.github.com/search/repositories?q=language%3Ajava&sort=stars&order=desc&page=2";
        Integer value = stringHelper.extractPageParameterValue(searchUrl);

        assertTrue(value.equals(2));
    }
}