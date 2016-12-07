package com.carloseduardo.github.helper;

import rx.Observable;
import rx.Observable.Transformer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RxHelper {

    private static final Transformer transformer = new Transformer<Object, Object>() {

        @Override
        @SuppressWarnings("unchecked")
        public Observable call(Observable observable) {

            return observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    private RxHelper() {
    }

    @SuppressWarnings("unchecked")
    public static <T> Transformer<T, T> applySchedulers() {

        return (Transformer<T, T>) transformer;
    }
}
