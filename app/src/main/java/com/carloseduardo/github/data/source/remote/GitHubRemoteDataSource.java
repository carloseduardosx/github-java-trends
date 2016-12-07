package com.carloseduardo.github.data.source.remote;

import com.carloseduardo.github.data.model.Pull;
import com.carloseduardo.github.data.model.RepositoriesContainer;

import java.util.List;

import retrofit2.adapter.rxjava.Result;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

public interface GitHubRemoteDataSource {

    @GET()
    Observable<Result<RepositoriesContainer>> getRepository(@Url String url);

    @GET()
    Observable<Result<List<Pull>>> getPulls(@Url String url);
}