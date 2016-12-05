package com.carloseduardo.github.data.source;

import com.carloseduardo.github.data.model.Repository;

import java.util.List;

import rx.Observable;

public interface GitHubDataSource {

    List<Repository> getRepositories(int limit);

    Observable<List<Repository>> getRepositories();

    Observable<List<Repository>> loadNextPage(int page);
}