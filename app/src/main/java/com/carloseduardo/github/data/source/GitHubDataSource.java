package com.carloseduardo.github.data.source;

import com.carloseduardo.github.data.model.RepositoriesContainer;

import rx.Observable;

public interface GitHubDataSource {

    Observable<RepositoriesContainer> getRepositories();
}