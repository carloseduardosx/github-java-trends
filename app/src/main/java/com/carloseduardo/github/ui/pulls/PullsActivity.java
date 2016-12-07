package com.carloseduardo.github.ui.pulls;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.carloseduardo.github.R;
import com.carloseduardo.github.application.GitHubTrendApplication;
import com.carloseduardo.github.base.BaseActivity;
import com.carloseduardo.github.constants.BundleKey;
import com.carloseduardo.github.data.model.Pull;
import com.carloseduardo.github.ui.pulls.adapter.PullsAdapter;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PullsActivity extends BaseActivity implements PullsContract.View {

    @BindView(R.id.pull_toolbar)
    Toolbar toolbar;

    @BindView(R.id.pulls)
    SuperRecyclerView recyclerView;

    @BindView(R.id.pull_top_navigation_fab)
    FloatingActionButton fab;

    private PullsContract.Presenter presenter;
    private Parcelable savedRecyclerViewState;
    private Integer savedRecycleViewSize;
    private Integer repositoryId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pulls);
        ButterKnife.bind(this);
        configureToolbar();
        setPresenter(new PullsPresenter(this));
        presenter.cleanTempData();
        configureTopNavigation();
        loadPulls();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putParcelable(
                BundleKey.RECYCLER_VIEW_PULL_STATE,
                recyclerView.getRecyclerView().getLayoutManager().onSaveInstanceState()
        );
        outState.putInt(BundleKey.REPOSITORY_PULLS_ID, getIntent().getIntExtra(BundleKey.REPOSITORY_PULLS_ID, 0));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {

            savedRecyclerViewState = savedInstanceState.getParcelable(BundleKey.RECYCLER_VIEW_PULL_STATE);
            savedRecycleViewSize = savedInstanceState.getInt(BundleKey.RECYCLER_VIEW_SIZE);
            repositoryId = savedInstanceState.getInt(BundleKey.REPOSITORY_PULLS_ID, 0);
        }
    }

    @Override
    protected void inject() {
        GitHubTrendApplication.getComponent()
                .inject(this);
    }

    @Override
    public void setPresenter(PullsContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showPulls(List<Pull> pulls) {

        PullsAdapter pullsAdapter = new PullsAdapter(savedRecycleViewSize == null
                ? pulls : presenter.getPulls(repositoryId, savedRecycleViewSize));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setAdapter(pullsAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {

                int currentPage = recyclerView.getAdapter().getItemCount() / 10;
                int repositoryId = getIntent().getIntExtra(BundleKey.REPOSITORY_PULLS_ID, 0);

                presenter.loadNextPage(repositoryId, ++currentPage);
            }
        }, 5);
        restoreRecyclerViewIfNeeded();
    }

    @Override
    public void loadMoreItems(List<Pull> pulls) {

        if (pulls != null && !pulls.isEmpty()) {

            PullsAdapter pullsAdapter = (PullsAdapter) recyclerView.getAdapter();
            pullsAdapter.appendItems(pulls);
        }
        recyclerView.hideMoreProgress();
    }

    private void restoreRecyclerViewIfNeeded() {

        if (savedRecyclerViewState != null) {

            recyclerView.getRecyclerView()
                    .getLayoutManager()
                    .onRestoreInstanceState(savedRecyclerViewState);

            savedRecyclerViewState = null;
            savedRecycleViewSize = null;
            repositoryId = null;
        }
    }

    private void loadPulls() {

        Intent intent = getIntent();
        String repositoryPullsUrl = intent.getStringExtra(BundleKey.REPOSITORY_PULLS_URL);
        int repositoryId = intent.getIntExtra(BundleKey.REPOSITORY_PULLS_ID, 0);

        presenter.listPulls(repositoryPullsUrl, repositoryId);
    }

    private void configureToolbar() {

        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        String repositoryName = getIntent().getStringExtra(BundleKey.REPOSITORY_NAME);

        if (supportActionBar != null) {

            supportActionBar.setTitle(repositoryName);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
        }
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