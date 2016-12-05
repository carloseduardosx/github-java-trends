package com.carloseduardo.github.injector.module;

import android.content.Context;

import com.carloseduardo.github.application.GitHubTrendApplication;
import com.carloseduardo.github.data.preferences.Preferences;
import com.carloseduardo.github.data.preferences.PreferencesImpl;
import com.carloseduardo.github.data.source.GitHubRepository;
import com.carloseduardo.github.constants.API;
import com.carloseduardo.github.data.source.local.GitHubLocalDataSource;
import com.carloseduardo.github.data.source.remote.GitHubRemoteDataSource;
import com.carloseduardo.github.helper.CollectionsHelper;
import com.carloseduardo.github.helper.DatabaseHelper;
import com.carloseduardo.github.helper.StringHelper;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.requery.Persistable;
import io.requery.rx.SingleEntityStore;
import io.requery.sql.EntityDataStore;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ApplicationModule {

    private final GitHubTrendApplication gitHubTrendApplication;

    public ApplicationModule(GitHubTrendApplication gitHubTrendApplication) {

        this.gitHubTrendApplication = gitHubTrendApplication;
    }

    @Provides
    @Singleton
    public Context context() {
        return gitHubTrendApplication;
    }

    @Provides
    @Singleton
    public SingleEntityStore<Persistable> singleEntityStore(Context context) {

        return DatabaseHelper.getInstance(context)
                .getRxEntityStore();
    }

    @Provides
    @Singleton
    public EntityDataStore<Persistable> entityDataStore(Context context) {

        return DatabaseHelper.getInstance(context)
                .getEntityStore();
    }

    @Provides
    @Singleton
    public StringHelper stringHelper() {

        return new StringHelper();
    }

    @Provides
    @Singleton
    public CollectionsHelper collectionsHelper() {

        return new CollectionsHelper();
    }

    @Provides
    @Singleton
    public OkHttpClient client() {

        return new OkHttpClient.Builder()
                .connectTimeout(60000, TimeUnit.MILLISECONDS)
                .writeTimeout(60000, TimeUnit.MILLISECONDS)
                .readTimeout(60000, TimeUnit.MILLISECONDS)
                .build();
    }

    @Provides
    @Singleton
    public Retrofit retrofit(final OkHttpClient client) {

        return new Retrofit.Builder()
                .baseUrl(API.ENDPOINT)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    @Provides
    @Singleton
    public GitHubRemoteDataSource gitHubRemoteDataSource(Retrofit retrofit) {

        return retrofit.create(GitHubRemoteDataSource.class);
    }

    @Provides
    @Singleton
    public GitHubLocalDataSource gitHubLocalDataSource() {

        return new GitHubLocalDataSource();
    }

    @Provides
    @Singleton
    public GitHubRepository gitHubRepository() {

        return new GitHubRepository();
    }

    @Provides
    @Singleton
    public Preferences preferences(Context context) {

        return new PreferencesImpl(context);
    }
}