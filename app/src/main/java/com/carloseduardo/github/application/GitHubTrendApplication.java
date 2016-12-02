package com.carloseduardo.github.application;

import android.app.Application;

import com.carloseduardo.github.injector.component.ApplicationComponent;
import com.carloseduardo.github.injector.component.DaggerApplicationComponent;
import com.carloseduardo.github.injector.module.ApplicationModule;

public class GitHubTrendApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        initComponent();
    }

    private void initComponent() {

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getComponent() {
        return applicationComponent;
    }
}
