package com.carloseduardo.github.ui.pulls;

import com.carloseduardo.github.base.BasePresenter;
import com.carloseduardo.github.base.BaseView;
import com.carloseduardo.github.data.model.Pull;

import java.util.List;

interface PullsContract {

    interface View extends BaseView<Presenter> {

        void showPulls(List<Pull> pulls);

        void loadMoreItems(List<Pull> pulls);
    }

    interface Presenter extends BasePresenter {

        void listPulls(String url, int repositoryId);

        void loadNextPage(int repositoryId, int page);

        void cleanTempData();
    }
}