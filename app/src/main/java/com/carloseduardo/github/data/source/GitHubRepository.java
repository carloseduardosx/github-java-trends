package com.carloseduardo.github.data.source;

import android.support.annotation.NonNull;

import com.carloseduardo.github.application.GitHubTrendApplication;
import com.carloseduardo.github.constants.API;
import com.carloseduardo.github.constants.PreferencesKey;
import com.carloseduardo.github.data.model.RepositoriesContainer;
import com.carloseduardo.github.data.model.Repository;
import com.carloseduardo.github.data.preferences.Preferences;
import com.carloseduardo.github.data.source.local.GitHubLocalDataSource;
import com.carloseduardo.github.data.source.remote.GitHubRemoteDataSource;
import com.carloseduardo.github.helper.GitHubAPIHelper;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class GitHubRepository implements GitHubDataSource {

    @Inject
    GitHubLocalDataSource localDataSource;

    @Inject
    GitHubRemoteDataSource remoteDataSource;

    @Inject
    GitHubAPIHelper gitHubAPIHelper;

    @Inject
    Preferences preferences;

    private final String TAG = "GitHubRepository";

    public GitHubRepository() {

        GitHubTrendApplication.getComponent()
                .inject(this);
    }

    @Override
    public List<Repository> getRepositories(int limit) {

        return localDataSource.getRepositories(limit);
    }

    @Override
    public Observable<List<Repository>> getRepositories() {

        Observable<List<Repository>> localRepositoryObservable = localDataSource.getRepositories()
                .observeOn(AndroidSchedulers.mainThread());
        Observable<List<Repository>> remoteRepositoryObservable = remoteDataSource.getRepository(API.FIRST_SEARCH_URL)
                .map(mapRepositoriesContainerResponse())
                .map(mapToRepositoryListAndSave())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        return localRepositoryObservable
                .concatWith(remoteRepositoryObservable)
                .skipWhile(this.<List<Repository>>skipWhileHasNoDataToConsume())
                .take(1)
                .map(new Func1<List<Repository>, List<Repository>>() {
                    @Override
                    public List<Repository> call(List<Repository> repositories) {

                        return repositories.subList(0, 10);
                    }
                });
    }

    @Override
    public Observable<List<Repository>> loadNextPage(int page) {

        boolean isToStopLoading = preferences.getBoolean(PreferencesKey.IS_STOP_LOADING);
        Observable<List<Repository>> nextLocalPageObservable = localDataSource.pagination(page)
                .observeOn(AndroidSchedulers.mainThread());
        Observable<List<Repository>> nextRemotePageObservable = isToStopLoading
                ? Observable.just(Collections.<Repository>emptyList())
                : remoteDataSource.getRepository(preferences.getString(PreferencesKey.NEXT_PAGE_URL))
                .map(mapRepositoriesContainerResponse())
                .map(mapToRepositoryListAndSave())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        return Observable.concat(nextLocalPageObservable, nextRemotePageObservable)
                .skipWhile(this.<List<Repository>>skipWhileHasNoDataToConsume())
                .take(1);
    }

    private <T> Func1<T, Boolean> skipWhileHasNoDataToConsume() {

        return new Func1<T, Boolean>() {
            @Override
            public Boolean call(T t) {
                return t == null;
            }
        };
    }

    @NonNull
    private Func1<retrofit2.adapter.rxjava.Result<RepositoriesContainer>, RepositoriesContainer> mapRepositoriesContainerResponse() {

        return new Func1<retrofit2.adapter.rxjava.Result<RepositoriesContainer>, RepositoriesContainer>() {
            @Override
            public RepositoriesContainer call(retrofit2.adapter.rxjava.Result<RepositoriesContainer> repositoriesContainerResult) {

                Response<RepositoriesContainer> response = repositoriesContainerResult.response();

                if (response != null && response.isSuccessful()) {

                    int lastPage = preferences.getInt(PreferencesKey.LAST_PAGE_NUMBER);
                    int nextPage = preferences.getInt(PreferencesKey.NEXT_PAGE_NUMBER);

                    if ((nextPage != 0 && lastPage != 0) && nextPage == lastPage) {

                        preferences.putBoolean(PreferencesKey.IS_STOP_LOADING, true);
                    } else {

                        String links = response.headers()
                                .get(API.NEXT_SEARCH_HEADER);

                        gitHubAPIHelper.extractAndSaveLinkHeaderValues(links);
                    }
                    return response.body();
                } else {

                    return new RepositoriesContainer();
                }
            }
        };
    }

    @NonNull
    private Func1<RepositoriesContainer, List<Repository>> mapToRepositoryListAndSave() {
        return new Func1<RepositoriesContainer, List<Repository>>() {
            @Override
            public List<Repository> call(RepositoriesContainer repositoriesContainer) {

                if (repositoriesContainer == null) {

                    return Collections.emptyList();
                } else {

                    List<Repository> repositories = repositoriesContainer.getRepositories();
                    localDataSource.save(repositoriesContainer);
                    return repositories;
                }
            }
        };
    }
}