package com.carloseduardo.github.ui.repositories;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.carloseduardo.github.R;
import com.carloseduardo.github.application.GitHubTrendApplication;
import com.carloseduardo.github.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RepositoriesActivity extends BaseActivity implements RepositoriesContract.View {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private RepositoriesContract.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repositories);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setPresenter(new RepositoriesPresenter());
    }

    @Override
    protected void inject() {

        GitHubTrendApplication.getComponent()
                .inject(this);
    }

    @Override
    public void setPresenter(RepositoriesContract.Presenter presenter) {

        this.presenter = presenter;
    }
}
