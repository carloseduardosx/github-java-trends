package com.carloseduardo.github.data.source.local;

import android.support.annotation.Nullable;

import com.carloseduardo.github.application.GitHubTrendApplication;
import com.carloseduardo.github.data.model.Owner;
import com.carloseduardo.github.data.model.Pull;
import com.carloseduardo.github.data.model.RepositoriesContainer;
import com.carloseduardo.github.data.model.Repository;
import com.carloseduardo.github.helper.CollectionsHelper;
import com.carloseduardo.github.helper.RxHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.requery.Persistable;
import io.requery.query.Result;
import io.requery.rx.SingleEntityStore;
import io.requery.sql.EntityDataStore;
import rx.Observable;
import rx.Single;
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

    public Observable<List<Pull>> getPulls(int repositoryId) {

        return pullPagination(repositoryId, 0);
    }

    public Observable<List<Repository>> getRepositories() {

        return pagination(0);
    }

    public List<Repository> getRepositories(int limit) {

        List<Repository> repositories = dataStore.select(Repository.class)
                .orderBy(Repository.STARGAZERS_COUNT.desc())
                .limit(limit)
                .get()
                .toList();

        return collectionsHelper.toModifiableList(repositories);
    }

    public List<Pull> getPulls(int repositoryId, int limit) {

        return dataStore.select(Pull.class)
                .where(Pull.REPOSITORY_ID.eq(repositoryId))
                .orderBy(Pull.CREATED_AT.desc())
                .limit(limit)
                .get()
                .toList();
    }

    public Observable<List<Pull>> pullPagination(int repositoryId, int page) {

        final int firstPosition = page * 10;
        final int lastPosition = firstPosition + 10;

        return rxDataStore.select(Pull.class)
                .where(Pull.REPOSITORY_ID.eq(repositoryId))
                .orderBy(Pull.CREATED_AT.desc())
                .limit(lastPosition)
                .get()
                .toSelfObservable()
                .take(1)
                .map(new Func1<Result<Pull>, List<Pull>>() {
                    @Override
                    public List<Pull> call(Result<Pull> pulls) {

                        List<Pull> pullsContent = pagination(firstPosition, lastPosition, pulls);

                        if (pullsContent == null || pullsContent.isEmpty()) {
                            return null;
                        } else {
                            return collectionsHelper.toModifiableList(pullsContent);
                        }
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

                        List<Repository> repositoriesContent = pagination(firstPosition, lastPosition, repositories);

                        if (repositoriesContent == null || repositoriesContent.isEmpty()) {
                            return null;
                        } else {
                            return collectionsHelper.toModifiableList(repositoriesContent);
                        }
                    }
                });
    }

    public Observable<Integer> cleanAllData() {

        Single<Integer> ownersDeletionSingle = rxDataStore.delete(Owner.class).get().toSingle();
        Single<Integer> pullsDeletionSingle = rxDataStore.delete(Pull.class).get().toSingle();
        Single<Integer> repositoriesDeletionSingle = rxDataStore.delete(Repository.class).get().toSingle();
        Single<Integer> repositoriesContainerDeletionSingle = rxDataStore.delete(RepositoriesContainer.class)
                .get().toSingle();

        return Single.concat(
                ownersDeletionSingle,
                pullsDeletionSingle,
                repositoriesDeletionSingle,
                repositoriesContainerDeletionSingle
        ).compose(RxHelper.<Integer>applySchedulers());
    }

    @Nullable
    private <T> List<T> pagination(int firstPosition, int lastPosition, Result<T> items) {

        List<T> itemsContent = items.toList();

        if ((lastPosition > itemsContent.size() && itemsContent.isEmpty())
                || firstPosition > itemsContent.size() - 1) {

            return null;
        } else {

            lastPosition = itemsContent.size();
        }
        return itemsContent.size() >= lastPosition
                ? itemsContent.subList(firstPosition, lastPosition)
                : null;
    }

    @Nullable
    public <T> List<T> pagination(int firstPosition, int lastPosition, List<T> items) {

        if ((lastPosition > items.size() && items.isEmpty())
                || firstPosition > items.size() - 1) {

            return null;
        } else {

            lastPosition = items.size();
        }
        return items.size() >= lastPosition
                ? items.subList(firstPosition, lastPosition)
                : null;
    }

    public void save(List<Pull> pulls, Repository repository) {

        List<Owner> users = extractOwnersFromPull(pulls);

        for (int i = 0; i < users.size(); i++) {

            Pull pull = pulls.get(i);

            pull.setUser(users.get(i));
            pull.setRepository(repository);
        }
        dataStore.upsert(pulls);
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

    public Repository getRepository(int id) {

        return dataStore.select(Repository.class)
                .where(Repository.ID.eq(id))
                .get()
                .first();
    }

    private List<Owner> extractOwnersFromPull(List<Pull> pulls) {

        List<Owner> users = new ArrayList<>();

        for (Pull pull : pulls) {

            users.add(pull.getUser());
        }
        return users;
    }

    private List<Owner> extractOwners(List<Repository> repositories) {

        List<Owner> owners = new ArrayList<>();

        for (Repository repository : repositories) {

            owners.add(repository.getOwner());
        }
        return owners;
    }
}