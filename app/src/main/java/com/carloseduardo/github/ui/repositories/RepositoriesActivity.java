package com.carloseduardo.github.ui.repositories;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.carloseduardo.github.R;
import com.carloseduardo.github.application.GitHubTrendApplication;
import com.carloseduardo.github.base.BaseActivity;
import com.carloseduardo.github.constants.BundleKey;
import com.carloseduardo.github.data.model.Repository;
import com.carloseduardo.github.helper.NetworkHelper;
import com.carloseduardo.github.helper.StringHelper;
import com.carloseduardo.github.ui.pulls.PullsActivity;
import com.carloseduardo.github.ui.repositories.adapter.RepositoriesAdapter;
import com.carloseduardo.github.ui.repositories.listener.OnRepositoryItemClick;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RepositoriesActivity extends BaseActivity implements RepositoriesContract.View {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.repositories)
    SuperRecyclerView recyclerView;

    @BindView(R.id.top_navigation_fab)
    FloatingActionButton fab;

    @BindView(R.id.swipe_refresh_repositories)
    SwipeRefreshLayout swipeRefreshLayout;

    @Inject
    StringHelper stringHelper;

    @Inject
    NetworkHelper networkHelper;

    private RepositoriesContract.Presenter presenter;
    private Parcelable savedRecyclerViewState;
    private Integer savedRecycleViewSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repositories);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setPresenter(new RepositoriesPresenter(this));
        configureTopNavigation();
        configureSwipeToRefresh();
        loadRepositories();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        RecyclerView.LayoutManager layoutManager = recyclerView.getRecyclerView().getLayoutManager();

        if (layoutManager != null) {

            outState.putParcelable(
                    BundleKey.RECYCLER_VIEW_STATE,
                    layoutManager.onSaveInstanceState()
            );
            outState.putInt(BundleKey.RECYCLER_VIEW_SIZE, layoutManager.getItemCount());
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {

            savedRecyclerViewState = savedInstanceState.getParcelable(BundleKey.RECYCLER_VIEW_STATE);
            savedRecycleViewSize = savedInstanceState.getInt(BundleKey.RECYCLER_VIEW_SIZE);

            if (savedRecycleViewSize == 0) {

                cleanSavedInstanceData();
            }
        }
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
    public void cleanAdapterAndShowRepositories(List<Repository> repositories) {

        RepositoriesAdapter repositoriesAdapter = (RepositoriesAdapter) recyclerView.getAdapter();

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        repositoriesAdapter.setContent(repositories);
    }

    @Override
    public void showRepositories(List<Repository> repositories) {

        repositories = savedRecycleViewSize == null ? repositories : presenter.getRepositories(savedRecycleViewSize);
        RepositoriesAdapter repositoriesAdapter = new RepositoriesAdapter(repositories, onRepositoryItemClickListener());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setAdapter(repositoriesAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {

                if (swipeRefreshLayout.isRefreshing()) {

                    recyclerView.hideMoreProgress();
                } else {

                    RepositoriesAdapter adapter = (RepositoriesAdapter) recyclerView.getAdapter();
                    int currentPage = adapter.getItemCount() / 10;

                    presenter.loadNextPage(++currentPage);
                }
            }
        }, 5);
        restoreRecyclerViewStateIfNeeded();
    }

    private OnRepositoryItemClick onRepositoryItemClickListener() {

        return new OnRepositoryItemClick() {
            @Override
            public void onRepositoryClick(Repository repository) {

                if (!swipeRefreshLayout.isRefreshing()) {

                    String pullsUrl = stringHelper.extractUrlPlaceHolder(repository.getPullsUrl());
                    Intent openRepositoryPullsIntent = new Intent(RepositoriesActivity.this, PullsActivity.class);

                    openRepositoryPullsIntent.putExtra(BundleKey.REPOSITORY_NAME, repository.getName());
                    openRepositoryPullsIntent.putExtra(BundleKey.REPOSITORY_PULLS_URL, pullsUrl);
                    openRepositoryPullsIntent.putExtra(BundleKey.REPOSITORY_PULLS_ID, repository.getId());
                    startActivity(openRepositoryPullsIntent);
                }
            }
        };
    }

    private void restoreRecyclerViewStateIfNeeded() {

        if (savedRecyclerViewState != null) {

            recyclerView.getRecyclerView()
                    .getLayoutManager()
                    .onRestoreInstanceState(savedRecyclerViewState);

            cleanSavedInstanceData();
        }
    }

    private void cleanSavedInstanceData() {
        savedRecyclerViewState = null;
        savedRecycleViewSize = null;
    }

    @Override
    public void loadMoreItems(List<Repository> repositories) {

        if (repositories != null && !repositories.isEmpty()) {

            RepositoriesAdapter repositoriesAdapter = (RepositoriesAdapter) recyclerView.getAdapter();
            repositoriesAdapter.appendItems(repositories);
        }
        recyclerView.hideMoreProgress();
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

    private void configureSwipeToRefresh() {

        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (networkHelper.hasNetwork()) {

                    presenter.cleanAllDataAndListRepositories();
                } else {

                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }
}