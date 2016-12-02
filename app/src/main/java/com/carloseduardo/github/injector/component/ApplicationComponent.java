package com.carloseduardo.github.injector.component;

import com.carloseduardo.github.MainActivity;
import com.carloseduardo.github.injector.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    void inject(MainActivity mainActivity);
}