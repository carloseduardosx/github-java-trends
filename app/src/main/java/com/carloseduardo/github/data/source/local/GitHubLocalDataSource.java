package com.carloseduardo.github.data.source.local;

import com.carloseduardo.github.application.GitHubTrendApplication;
import com.carloseduardo.github.data.model.RepositoriesContainer;

import javax.inject.Inject;

import io.requery.Persistable;
import io.requery.query.Result;
import io.requery.rx.SingleEntityStore;
import rx.Observable;

public class GitHubLocalDataSource {

    @Inject
    SingleEntityStore<Persistable> dataStore;

    public GitHubLocalDataSource() {

        GitHubTrendApplication.getComponent()
                .inject(this);
    }

    public Observable<Result<RepositoriesContainer>> getRepositories() {

        return dataStore.select(RepositoriesContainer.class)
                .get()
                .toSelfObservable()
                .take(1);
    }
}