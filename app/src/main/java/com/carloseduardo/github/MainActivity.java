package com.carloseduardo.github;

import android.os.Bundle;
import android.util.Log;

import com.carloseduardo.github.application.GitHubTrendApplication;
import com.carloseduardo.github.base.BaseActivity;
import com.carloseduardo.github.data.model.RepositoriesContainer;
import com.carloseduardo.github.data.source.GitHubRepository;

import javax.inject.Inject;

import rx.functions.Action1;

public class MainActivity extends BaseActivity {

    @Inject
    GitHubRepository gitHubRepository;

    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logRepositories();
    }

    private void logRepositories() {

        //TODO Apply presenter logic
        gitHubRepository.getRepositories()
                .subscribe(new Action1<RepositoriesContainer>() {
                    @Override
                    public void call(RepositoriesContainer repositoriesContainer) {

                        Log.d(TAG, "Receiving repositories");
                        if (repositoriesContainer != null) {

                            Log.d(TAG, "RepositoriesContainer is not null");
                        } else {

                            Log.d(TAG, "RepositoriesContainer is null");
                        }
                    }
                });
    }

    @Override
    protected void inject() {

        GitHubTrendApplication.getComponent()
                .inject(this);
    }
}