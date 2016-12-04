package com.carloseduardo.github.ui.repositories;

import com.carloseduardo.github.application.GitHubTrendApplication;
import com.carloseduardo.github.data.model.Repository;
import com.carloseduardo.github.data.source.GitHubRepository;

import java.util.List;

import javax.inject.Inject;

import rx.functions.Action1;

public class RepositoriesPresenter implements RepositoriesContract.Presenter {

    @Inject
    GitHubRepository gitHubRepository;

    private RepositoriesContract.View view;

    public RepositoriesPresenter(RepositoriesContract.View view) {

        this.view = view;
        GitHubTrendApplication.getComponent()
                .inject(this);
    }

    @Override
    public void listRepositories() {

        gitHubRepository.getRepositories()
                .subscribe(new Action1<List<Repository>>() {

                    @Override
                    public void call(List<Repository> repositories) {

                        view.showRepositories(repositories);
                    }
                });
    }
}