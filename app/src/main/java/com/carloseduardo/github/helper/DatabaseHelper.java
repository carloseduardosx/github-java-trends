package com.carloseduardo.github.helper;

import android.content.Context;

import com.carloseduardo.github.data.model.Models;

import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.rx.RxSupport;
import io.requery.rx.SingleEntityStore;
import io.requery.sql.EntityDataStore;
import rx.schedulers.Schedulers;

public class DatabaseHelper {

    private final Context context;
    private final int DATABASE_VERSION = 1;

    private DatabaseHelper(Context context) {

        this.context = context;
    }

    public DatabaseSource getDatabaseSource() {

        return new DatabaseSource(context, Models.DEFAULT, DATABASE_VERSION);
    }

    public SingleEntityStore<Persistable> getEntityStore() {

        return RxSupport.toReactiveStore(
                        new EntityDataStore<Persistable>(getDatabaseSource().getConfiguration()),
                        Schedulers.io()
                );
    }

    public static DatabaseHelper getInstance(Context context) {

        return new DatabaseHelper(context);
    }
}