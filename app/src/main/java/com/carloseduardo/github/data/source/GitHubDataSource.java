package com.carloseduardo.github.data.source;

import com.carloseduardo.github.data.model.Pull;
import com.carloseduardo.github.data.model.Repository;

import java.util.List;

import rx.Observable;

public interface GitHubDataSource {

    List<Repository> getRepositories(int limit);

    List<Pull> getPulls(int repositoryId, int limit);

    Observable<List<Pull>> getPulls(String url, Repository repository);

    Observable<List<Pull>> loadNextPullPage(Repository repository, int page);

    Observable<List<Repository>> getRepositories();

    Observable<List<Repository>> loadNextPage(int page);
}