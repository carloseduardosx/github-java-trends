package com.carloseduardo.github.injector.module;

import android.content.Context;

import com.carloseduardo.github.application.GitHubTrendApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private final GitHubTrendApplication gitHubTrendApplication;

    public ApplicationModule(GitHubTrendApplication gitHubTrendApplication) {

        this.gitHubTrendApplication = gitHubTrendApplication;
    }

    @Provides
    @Singleton
    public Context context() {
        return gitHubTrendApplication;
    }
}