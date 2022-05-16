package com.leon.counter_reading.di.module;

import android.content.Context;

import com.leon.counter_reading.di.view_model.SharedPreferenceManagerModel;
import com.leon.counter_reading.enums.SharedReferenceNames;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Singleton
@Module
public class SharedPreferenceModule {
    private final SharedPreferenceManagerModel sharedPreference;

    public SharedPreferenceModule(Context context, SharedReferenceNames sharedReferenceNames) {
        sharedPreference = new SharedPreferenceManagerModel(context, sharedReferenceNames.getValue());
    }

    @Singleton
    @Provides
    public SharedPreferenceManagerModel providesSharedPreferenceModel() {
        return sharedPreference;
    }
}
