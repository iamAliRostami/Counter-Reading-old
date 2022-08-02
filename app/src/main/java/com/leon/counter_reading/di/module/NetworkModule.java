package com.leon.counter_reading.di.module;

import static com.leon.counter_reading.enums.SharedReferenceKeys.TOKEN;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import com.google.gson.Gson;
import com.leon.counter_reading.di.view_model.NetworkHelperModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

//@Singleton
@Module
public class NetworkModule {
    final NetworkHelperModel networkHelper;

    public NetworkModule() {
        networkHelper = new NetworkHelperModel();
    }

    @Singleton
    @Provides
    public Gson providesGson() {
        return networkHelper.getGson();
    }

    @Singleton
    @Provides
    public Retrofit providesRetrofit() {
        return networkHelper.getInstance(getApplicationComponent().SharedPreferenceModel()
                .getStringData(TOKEN.getValue()));
    }

    @Singleton
    @Provides
    public NetworkHelperModel providesNetworkHelperModel() {
        return networkHelper;
    }
}
