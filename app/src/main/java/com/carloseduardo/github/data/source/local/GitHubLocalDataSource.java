package com.carloseduardo.github.data.source.local;

import android.support.annotation.Nullable;

import com.carloseduardo.github.application.GitHubTrendApplication;
import com.carloseduardo.github.data.model.Owner;
import com.carloseduardo.github.data.model.RepositoriesContainer;
import com.carloseduardo.github.data.model.Repository;
import com.carloseduardo.github.helper.CollectionsHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.requery.Persistable;
import io.requery.query.Result;
import io.requery.rx.SingleEntityStore;
import io.requery.sql.EntityDataStore;
import rx.Observable;
import rx.functions.Func1;

public class GitHubLocalDataSource {

    @Inject
    SingleEntityStore<Persistable> rxDataStore;

    @Inject
    EntityDataStore<Persistable> dataStore;

    @Inject
    CollectionsHelper collectionsHelper;

    public GitHubLocalDataSource() {

        GitHubTrendApplication.getComponent()
                .inject(this);
    }

    public Observable<List<Repository>> getRepositories() {

        return rxDataStore.select(Repository.class)
                .orderBy(Repository.STARGAZERS_COUNT.desc())
                .get()
                .toSelfObservable()
                .take(1)
                .map(new Func1<Result<Repository>, List<Repository>>() {
                    @Override
                    public List<Repository> call(Result<Repository> repositories) {

                        if (repositories == null) {
                            return null;
                        }
                        List<Repository> repositoriesContent = collectionsHelper.toModifiableList(repositories.toList());
                        return repositoriesContent.isEmpty() ? null : repositoriesContent.subList(0, 10);
                    }
                });
    }

    public Observable<List<Repository>> pagination(int page) {

        final int firstPosition = page * 10;
        final int lastPosition = firstPosition + 10;

        return rxDataStore.select(Repository.class)
                .orderBy(Repository.STARGAZERS_COUNT.desc())
                .limit(lastPosition)
                .get()
                .toSelfObservable()
                .take(1)
                .map(new Func1<Result<Repository>, List<Repository>>() {
                    @Override
                    public List<Repository> call(Result<Repository> repositories) {

                        return pagination(firstPosition, lastPosition, repositories);
                    }
                });
    }

    @Nullable
    private List<Repository> pagination(int firstPosition, int lastPosition, Result<Repository> repositories) {

        List<Repository> repositoriesContent = repositories.toList();

        if ((lastPosition > repositoriesContent.size() && repositoriesContent.isEmpty())
                || firstPosition > repositoriesContent.size() - 1) {

            return null;
        } else {

            lastPosition = repositoriesContent.size();
        }
        return repositoriesContent.size() >= lastPosition
                ? repositoriesContent.subList(firstPosition, lastPosition)
                : null;
    }

    public void save(RepositoriesContainer repositoriesContainer) {

        List<Repository> repositories = repositoriesContainer.getRepositories();
        List<Owner> owners = extractOwners(repositories);

        repositoriesContainer = dataStore.upsert(repositoriesContainer);

        for (int i = 0; i < repositories.size(); i++) {

            Repository repository = repositories.get(i);

            repository.setOwner(owners.get(i));
            repository.setRepositoriesContainer(repositoriesContainer);
        }
        dataStore.upsert(repositories);
    }

    private List<Owner> extractOwners(List<Repository> repositories) {

        List<Owner> owners = new ArrayList<>();

        for (Repository repository : repositories) {

            owners.add(repository.getOwner());
        }
        return owners;
    }
}