package com.carloseduardo.github.injector.component;

import com.carloseduardo.github.data.source.GitHubRepository;
import com.carloseduardo.github.data.source.local.GitHubLocalDataSource;
import com.carloseduardo.github.helper.GitHubAPIHelper;
import com.carloseduardo.github.injector.module.ApplicationModule;
import com.carloseduardo.github.ui.repositories.RepositoriesActivity;
import com.carloseduardo.github.ui.repositories.RepositoriesPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    void inject(GitHubLocalDataSource gitHubLocalDataSource);
    void inject(GitHubRepository gitHubRepository);
    void inject(RepositoriesActivity repositoriesActivity);
    void inject(RepositoriesPresenter repositoriesPresenter);
    void inject(GitHubAPIHelper gitHubAPIHelper);
}