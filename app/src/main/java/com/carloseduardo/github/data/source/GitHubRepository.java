package com.carloseduardo.github.data.source;

import android.support.annotation.NonNull;

import com.carloseduardo.github.application.GitHubTrendApplication;
import com.carloseduardo.github.constants.API;
import com.carloseduardo.github.constants.PreferencesKey;
import com.carloseduardo.github.data.model.Pull;
import com.carloseduardo.github.data.model.RepositoriesContainer;
import com.carloseduardo.github.data.model.Repository;
import com.carloseduardo.github.data.preferences.Preferences;
import com.carloseduardo.github.data.source.local.GitHubLocalDataSource;
import com.carloseduardo.github.data.source.remote.GitHubRemoteDataSource;
import com.carloseduardo.github.helper.GitHubAPIHelper;
import com.carloseduardo.github.helper.RxHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;
import retrofit2.adapter.rxjava.Result;
import rx.Observable;
import rx.functions.Func1;

public class GitHubRepository implements GitHubDataSource {

    @Inject
    GitHubLocalDataSource localDataSource;

    @Inject
    GitHubRemoteDataSource remoteDataSource;

    @Inject
    GitHubAPIHelper gitHubAPIHelper;

    @Inject
    Preferences preferences;

    public GitHubRepository() {

        GitHubTrendApplication.getComponent()
                .inject(this);
    }

    @Override
    public Observable<List<Pull>> getPulls(String url, Repository repository) {

        Observable<List<Pull>> pullsLocalObservable = localDataSource.getPulls(repository.getId())
                .compose(RxHelper.<List<Pull>>applySchedulers());
        Observable<List<Pull>> pullsRemoteObservable = remoteDataSource.getPulls(url)
                .compose(RxHelper.<Result<List<Pull>>>applySchedulers())
                .map(mapPullsResponse())
                .map(mapToPullListAndSave(repository));

        return Observable.concat(pullsLocalObservable, pullsRemoteObservable)
                .skipWhile(this.<List<Pull>>skipWhileHasNoDataToConsume())
                .take(1)
                .map(new Func1<List<Pull>, List<Pull>>() {
                    @Override
                    public List<Pull> call(List<Pull> pulls) {

                        List<Pull> paginatedPulls = localDataSource.pagination(0, 10, pulls);

                        return paginatedPulls == null ? Collections.<Pull>emptyList() : paginatedPulls;
                    }
                });

    }

    @Override
    public Observable<List<Pull>> loadNextPullPage(Repository repository, int page) {

        Observable<List<Pull>> pullsLocalObservable = localDataSource.pullPagination(repository.getId(), page)
                .compose(RxHelper.<List<Pull>>applySchedulers());
        Observable<List<Pull>> pullRemoteObservable = remoteDataSource.getPulls(preferences.getString(PreferencesKey.NEXT_PULL_PAGE_URL))
                .compose(RxHelper.<Result<List<Pull>>>applySchedulers())
                .map(mapPullsResponse())
                .map(mapToPullListAndSave(repository));

        return Observable.concat(pullsLocalObservable, pullRemoteObservable)
                .skipWhile(this.<List<Pull>>skipWhileHasNoDataToConsume())
                .take(1)
                .map(new Func1<List<Pull>, List<Pull>>() {
                    @Override
                    public List<Pull> call(List<Pull> pulls) {

                        List<Pull> paginatedPulls = localDataSource.pagination(0, 10, pulls);

                        return paginatedPulls == null ? Collections.<Pull>emptyList() : paginatedPulls;
                    }
                });
    }

    @Override
    public Observable<List<Repository>> cleanAllDataAndListRepositories() {

        return localDataSource.cleanAllData()
                .buffer(4)
                .flatMap(new Func1<List<Integer>, Observable<List<Repository>>>() {
                    @Override
                    public Observable<List<Repository>> call(List<Integer> integers) {

                        cleanRepositoryPreferences();
                        return getRepositories();
                    }
                });
    }

    @Override
    public List<Repository> getRepositories(int limit) {

        return localDataSource.getRepositories(limit);
    }

    @Override
    public List<Pull> getPulls(int repositoryId, int limit) {

        return localDataSource.getPulls(repositoryId, limit);
    }

    @Override
    public Observable<List<Repository>> getRepositories() {

        Observable<List<Repository>> localRepositoryObservable = localDataSource.getRepositories()
                .compose(RxHelper.<List<Repository>>applySchedulers());
        Observable<List<Repository>> remoteRepositoryObservable = remoteDataSource.getRepository(API.FIRST_SEARCH_URL)
                .compose(RxHelper.<Result<RepositoriesContainer>>applySchedulers())
                .map(mapRepositoriesContainerResponse())
                .map(mapToRepositoryListAndSave());

        return localRepositoryObservable
                .concatWith(remoteRepositoryObservable)
                .skipWhile(this.<List<Repository>>skipWhileHasNoDataToConsume())
                .take(1)
                .map(new Func1<List<Repository>, List<Repository>>() {
                    @Override
                    public List<Repository> call(List<Repository> repositories) {

                        List<Repository> repositoriesPaginated = localDataSource.pagination(0, 10, repositories);

                        return repositoriesPaginated == null
                                ? Collections.<Repository>emptyList() : repositoriesPaginated;
                    }
                });
    }

    @Override
    public Observable<List<Repository>> loadNextPage(int page) {

        boolean isToStopLoading = preferences.getBoolean(PreferencesKey.IS_STOP_LOADING);
        Observable<List<Repository>> nextLocalPageObservable = localDataSource.pagination(page)
                .compose(RxHelper.<List<Repository>>applySchedulers());
        Observable<List<Repository>> nextRemotePageObservable = isToStopLoading
                ? Observable.just(Collections.<Repository>emptyList())
                : remoteDataSource.getRepository(preferences.getString(PreferencesKey.NEXT_PAGE_URL))
                .compose(RxHelper.<Result<RepositoriesContainer>>applySchedulers())
                .map(mapRepositoriesContainerResponse())
                .map(mapToRepositoryListAndSave());

        return Observable.concat(nextLocalPageObservable, nextRemotePageObservable)
                .skipWhile(this.<List<Repository>>skipWhileHasNoDataToConsume())
                .take(1);
    }

    public Repository getRepository(int id) {

        return localDataSource.getRepository(id);
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
    private Func1<retrofit2.adapter.rxjava.Result<List<Pull>>, List<Pull>> mapPullsResponse() {

        return new Func1<Result<List<Pull>>, List<Pull>>() {
            @Override
            public List<Pull> call(Result<List<Pull>> pullsResult) {

                Response<List<Pull>> response = pullsResult.response();

                if (response != null && response.isSuccessful()) {

                    int lastPage = preferences.getInt(PreferencesKey.LAST_PULL_PAGE_NUMBER);
                    int nextPage = preferences.getInt(PreferencesKey.NEXT_PULL_PAGE_NUMBER);

                    if ((nextPage != 0 && lastPage != 0) && nextPage == lastPage) {

                        preferences.putBoolean(PreferencesKey.IS_STOP_PULL_LOADING, true);
                    } else {

                        String links = response.headers()
                                .get(API.NEXT_SEARCH_HEADER);

                        gitHubAPIHelper.extractAndSavePullLinkHeaderValues(links);
                    }
                    return response.body();
                } else {

                    Collections.emptyList();
                }
                return null;
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
    private Func1<List<Pull>, List<Pull>> mapToPullListAndSave(final Repository repository) {

        return new Func1<List<Pull>, List<Pull>>() {
            @Override
            public List<Pull> call(List<Pull> pulls) {

                if (pulls == null || pulls.isEmpty()) {

                    return Collections.emptyList();
                } else {

                    List<Pull> pullsContent = new ArrayList<>(pulls);

                    localDataSource.save(pulls, repository);
                    return pullsContent;
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

    private void cleanRepositoryPreferences() {

        preferences.remove(PreferencesKey.NEXT_PAGE_URL);
        preferences.remove(PreferencesKey.LAST_PAGE_NUMBER);
        preferences.remove(PreferencesKey.NEXT_PAGE_NUMBER);
        preferences.remove(PreferencesKey.IS_STOP_LOADING);
    }
}