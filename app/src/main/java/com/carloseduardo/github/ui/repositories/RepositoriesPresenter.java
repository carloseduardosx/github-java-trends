package com.carloseduardo.github.ui.repositories;

import android.support.annotation.NonNull;

import com.carloseduardo.github.application.GitHubTrendApplication;
import com.carloseduardo.github.data.model.Repository;
import com.carloseduardo.github.data.preferences.Preferences;
import com.carloseduardo.github.data.source.GitHubRepository;
import com.carloseduardo.github.helper.RxHelper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class RepositoriesPresenter implements RepositoriesContract.Presenter {

    @Inject
    GitHubRepository gitHubRepository;

    @Inject
    Preferences preferences;

    private RepositoriesContract.View view;

    private PublishSubject<Observable<List<Repository>>> cleanAndListRepositoriesSubject = PublishSubject.create();

    public RepositoriesPresenter(RepositoriesContract.View view) {

        this.view = view;
        GitHubTrendApplication.getComponent()
                .inject(this);
        configureCleanAndListRepositoriesSubject();
    }

    @Override
    public void cleanAllDataAndListRepositories() {

        cleanAndListRepositoriesSubject.onNext(gitHubRepository.cleanAllDataAndListRepositories());
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

    @Override
    public void loadNextPage(int page) {

        gitHubRepository.loadNextPage(page)
                .subscribe(new Action1<List<Repository>>() {
                    @Override
                    public void call(List<Repository> repositories) {

                        view.loadMoreItems(repositories);
                    }
                });
    }

    @Override
    public List<Repository> getRepositories(int limit) {

        return gitHubRepository.getRepositories(limit);
    }

    private void configureCleanAndListRepositoriesSubject() {

        cleanAndListRepositoriesSubject.debounce(2000, TimeUnit.MILLISECONDS)
                .compose(RxHelper.<Observable<List<Repository>>>applySchedulers())
                .subscribe(cleanAndListRepositoriesObserver());
    }

    @NonNull
    private Observer<Observable<List<Repository>>> cleanAndListRepositoriesObserver() {

        return new Observer<Observable<List<Repository>>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Observable<List<Repository>> repositories) {

                repositories.subscribe(new Action1<List<Repository>>() {
                    @Override
                    public void call(List<Repository> repositories) {

                        view.cleanAdapterAndShowRepositories(repositories);
                    }
                });
            }
        };
    }
}