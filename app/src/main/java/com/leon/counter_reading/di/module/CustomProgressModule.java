package com.leon.counter_reading.di.module;


import static com.leon.counter_reading.di.view_model.CustomProgressModel.getInstance;

import com.leon.counter_reading.di.view_model.CustomProgressModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

//@Singleton
@Module
public class CustomProgressModule {
    private final CustomProgressModel progress;

    public CustomProgressModule() {
        progress = getInstance();
    }

    @Singleton
    @Provides
    public CustomProgressModel providesCustomProgressModel() {
        return progress;
    }
}
