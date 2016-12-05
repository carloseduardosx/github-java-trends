package com.carloseduardo.github.ui.repositories;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.carloseduardo.github.R;
import com.carloseduardo.github.application.GitHubTrendApplication;
import com.carloseduardo.github.base.BaseActivity;
import com.carloseduardo.github.data.model.Repository;
import com.carloseduardo.github.ui.repositories.adapter.RepositoriesAdapter;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RepositoriesActivity extends BaseActivity implements RepositoriesContract.View {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.repositories)
    SuperRecyclerView recyclerView;

    @BindView(R.id.top_navigation_fab)
    FloatingActionButton fab;

    private RepositoriesContract.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repositories);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setPresenter(new RepositoriesPresenter(this));
        configureTopNavigation();
        loadRepositories();
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

    @Override
    public void showRepositories(List<Repository> repositories) {

        RepositoriesAdapter repositoriesAdapter = new RepositoriesAdapter(repositories);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setAdapter(repositoriesAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {

                RepositoriesAdapter adapter = (RepositoriesAdapter) recyclerView.getAdapter();
                int currentPage = adapter.getItemCount() / 10;

                presenter.loadNextPage(++currentPage);
            }
        }, 5);
    }

    @Override
    public void loadMoreItems(List<Repository> repositories) {

        RepositoriesAdapter repositoriesAdapter = (RepositoriesAdapter) recyclerView.getAdapter();
        repositoriesAdapter.appendItems(repositories);
    }

    private void loadRepositories() {

        presenter.listRepositories();
    }

    private void configureTopNavigation() {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                recyclerView.getRecyclerView()
                        .getLayoutManager()
                        .scrollToPosition(0);
                fab.hide();
            }
        });
    }
}