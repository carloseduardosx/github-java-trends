package com.carloseduardo.github.ui.repositories;

import com.carloseduardo.github.base.BasePresenter;
import com.carloseduardo.github.base.BaseView;
import com.carloseduardo.github.data.model.Repository;

import java.util.List;

interface RepositoriesContract {

    interface Presenter extends BasePresenter {

        void listRepositories();
    }

    interface View extends BaseView<Presenter> {

        void showRepositories(List<Repository> repositories);
    }
}