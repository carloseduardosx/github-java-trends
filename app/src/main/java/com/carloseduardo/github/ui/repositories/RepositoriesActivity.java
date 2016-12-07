package com.carloseduardo.github.ui.repositories;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.carloseduardo.github.R;
import com.carloseduardo.github.application.GitHubTrendApplication;
import com.carloseduardo.github.base.BaseActivity;
import com.carloseduardo.github.constants.BundleKey;
import com.carloseduardo.github.data.model.Repository;
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

    @Inject
    StringHelper stringHelper;

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
        loadRepositories();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putParcelable(
                BundleKey.RECYCLER_VIEW_STATE,
                recyclerView.getRecyclerView().getLayoutManager().onSaveInstanceState()
        );

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {

            savedRecyclerViewState = savedInstanceState.getParcelable(BundleKey.RECYCLER_VIEW_STATE);
            savedRecycleViewSize = savedInstanceState.getInt(BundleKey.RECYCLER_VIEW_SIZE);
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
    public void showRepositories(List<Repository> repositories) {

        repositories = savedRecycleViewSize == null ? repositories : presenter.getRepositories(savedRecycleViewSize);
        RepositoriesAdapter repositoriesAdapter = new RepositoriesAdapter(repositories, onRepositoryItemClickListener());
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
        restoreRecyclerViewStateIfNeeded();
    }

    private OnRepositoryItemClick onRepositoryItemClickListener() {

        return new OnRepositoryItemClick() {
            @Override
            public void onRepositoryClick(Repository repository) {

                String pullsUrl = stringHelper.extractUrlPlaceHolder(repository.getPullsUrl());
                Intent openRepositoryPullsIntent = new Intent(RepositoriesActivity.this, PullsActivity.class);

                openRepositoryPullsIntent.putExtra(BundleKey.REPOSITORY_NAME, repository.getName());
                openRepositoryPullsIntent.putExtra(BundleKey.REPOSITORY_PULLS_URL, pullsUrl);
                openRepositoryPullsIntent.putExtra(BundleKey.REPOSITORY_PULLS_ID, repository.getId());
                startActivity(openRepositoryPullsIntent);
            }
        };
    }

    private void restoreRecyclerViewStateIfNeeded() {

        if (savedRecyclerViewState != null) {

            recyclerView.getRecyclerView()
                    .getLayoutManager()
                    .onRestoreInstanceState(savedRecyclerViewState);

            savedRecyclerViewState = null;
            savedRecycleViewSize = null;
        }
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
}