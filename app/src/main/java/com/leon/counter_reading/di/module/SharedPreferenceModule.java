package com.leon.counter_reading.di.module;

import android.content.Context;

import com.leon.counter_reading.di.view_model.SharedPreferenceModel;
import com.leon.counter_reading.enums.SharedReferenceNames;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

//@Singleton
@Module
public class SharedPreferenceModule {
    private final SharedPreferenceModel sharedPreference;

    public SharedPreferenceModule(Context context, SharedReferenceNames sharedReferenceNames) {
        sharedPreference = new SharedPreferenceModel(context, sharedReferenceNames.getValue());
    }

    @Singleton
    @Provides
    public SharedPreferenceModel providesSharedPreferenceModel() {
        return sharedPreference;
    }
}
