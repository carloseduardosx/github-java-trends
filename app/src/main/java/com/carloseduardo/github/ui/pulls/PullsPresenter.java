package com.carloseduardo.github.ui.pulls;

import com.carloseduardo.github.application.GitHubTrendApplication;
import com.carloseduardo.github.constants.PreferencesKey;
import com.carloseduardo.github.data.model.Pull;
import com.carloseduardo.github.data.model.Repository;
import com.carloseduardo.github.data.preferences.Preferences;
import com.carloseduardo.github.data.source.GitHubRepository;

import java.util.List;

import javax.inject.Inject;

import rx.functions.Action1;

public class PullsPresenter implements PullsContract.Presenter {

    @Inject
    GitHubRepository gitHubRepository;

    @Inject
    Preferences preferences;

    private PullsContract.View view;

    public PullsPresenter(PullsContract.View view) {
        this.view = view;
        GitHubTrendApplication.getComponent()
                .inject(this);
    }

    @Override
    public void listPulls(String url, int repositoryId) {

        Repository repository = gitHubRepository.getRepository(repositoryId);

        gitHubRepository.getPulls(url, repository)
                .subscribe(new Action1<List<Pull>>() {
                    @Override
                    public void call(List<Pull> pulls) {

                        view.showPulls(pulls);
                    }
                });
    }

    @Override
    public void loadNextPage(int repositoryId, int page) {

        Repository repository = gitHubRepository.getRepository(repositoryId);

        gitHubRepository.loadNextPullPage(repository, page)
                .subscribe(new Action1<List<Pull>>() {
                    @Override
                    public void call(List<Pull> pulls) {

                        view.loadMoreItems(pulls);
                    }
                });
    }

    @Override
    public void cleanTempData() {

        preferences.remove(PreferencesKey.IS_STOP_PULL_LOADING);
        preferences.remove(PreferencesKey.LAST_PULL_PAGE_NUMBER);
        preferences.remove(PreferencesKey.NEXT_PULL_PAGE_NUMBER);
        preferences.remove(PreferencesKey.NEXT_PULL_PAGE_URL);
    }

    @Override
    public List<Pull> getPulls(int repositoryId, int limit) {

        return gitHubRepository.getPulls(repositoryId, limit);
    }
}