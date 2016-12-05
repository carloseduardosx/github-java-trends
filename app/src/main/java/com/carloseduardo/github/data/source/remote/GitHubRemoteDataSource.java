package com.carloseduardo.github.data.source.remote;

import com.carloseduardo.github.data.model.RepositoriesContainer;

import retrofit2.adapter.rxjava.Result;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

public interface GitHubRemoteDataSource {

    //TODO Should increment page parameter when necessary
    @GET()
    Observable<Result<RepositoriesContainer>> getRepository(@Url String url);
}