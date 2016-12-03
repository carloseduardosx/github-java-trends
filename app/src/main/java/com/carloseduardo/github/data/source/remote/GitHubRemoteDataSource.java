package com.carloseduardo.github.data.source.remote;

import com.carloseduardo.github.data.model.RepositoriesContainer;

import retrofit2.http.GET;
import rx.Observable;

public interface GitHubRemoteDataSource {

    //TODO Should increment page parameter when necessary
    @GET("/search/repositories?q=language:java&sort=stars&order=desc&page=1")
    Observable<RepositoriesContainer> getRepository();
}