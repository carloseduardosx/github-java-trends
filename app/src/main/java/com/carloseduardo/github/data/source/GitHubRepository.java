package com.carloseduardo.github.data.source;

import com.carloseduardo.github.application.GitHubTrendApplication;
import com.carloseduardo.github.data.model.RepositoriesContainer;
import com.carloseduardo.github.data.model.Repository;
import com.carloseduardo.github.data.source.local.GitHubLocalDataSource;
import com.carloseduardo.github.data.source.remote.GitHubRemoteDataSource;

import java.util.List;

import javax.inject.Inject;

import io.requery.query.Result;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class GitHubRepository implements GitHubDataSource {

    @Inject
    GitHubLocalDataSource localDataSource;

    @Inject
    GitHubRemoteDataSource remoteDataSource;

    private final String TAG = "GitHubRepository";

    public GitHubRepository() {

        GitHubTrendApplication.getComponent()
                .inject(this);
    }

    @Override
    public Observable<List<Repository>> getRepositories() {

        Observable<Result<RepositoriesContainer>> localRepositoryObservable = localDataSource.getRepositories()
                .observeOn(AndroidSchedulers.mainThread());
        Observable<RepositoriesContainer> remoteRepositoryObservable = remoteDataSource.getRepository()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        return localRepositoryObservable
                .map(new Func1<Result<RepositoriesContainer>, RepositoriesContainer>() {
                    @Override
                    public RepositoriesContainer call(Result<RepositoriesContainer> repositories) {

                        return repositories.firstOrNull();
                    }
                }).concatWith(remoteRepositoryObservable)
                .skipWhile(new Func1<RepositoriesContainer, Boolean>() {
                    @Override
                    public Boolean call(RepositoriesContainer repositoriesContainer) {
                        return repositoriesContainer == null;
                    }
                })
                .take(1)
                .map(new Func1<RepositoriesContainer, List<Repository>>() {
                    @Override
                    public List<Repository> call(RepositoriesContainer repositoriesContainer) {

                        return repositoriesContainer.getRepositories();
                    }
                });
    }
}