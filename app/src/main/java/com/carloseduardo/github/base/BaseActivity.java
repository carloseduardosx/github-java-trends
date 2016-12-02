package com.carloseduardo.github.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.carloseduardo.github.application.GitHubTrendApplication;
import com.carloseduardo.github.injector.component.ApplicationComponent;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inject();
    }

    protected GitHubTrendApplication getGitHubTrendApplication() {

        return (GitHubTrendApplication) getApplication();
    }

    protected ApplicationComponent getComponent() {

        return getGitHubTrendApplication().getComponent();
    }

    abstract protected void inject();
}
