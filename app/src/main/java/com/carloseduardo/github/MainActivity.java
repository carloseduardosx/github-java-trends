package com.carloseduardo.github;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.carloseduardo.github.base.BaseActivity;

import javax.inject.Inject;

public class MainActivity extends BaseActivity {

    @Inject
    Context context;

    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, context.toString()); //For dependency injection test purpose
    }

    @Override
    protected void inject() {

        getGitHubTrendApplication()
                .getComponent()
                .inject(this);
    }
}