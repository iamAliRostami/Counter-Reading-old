package com.leon.counter_reading.di.module;

import android.content.Context;

import com.leon.counter_reading.di.view_model.MyDatabaseClientModel;
import com.leon.counter_reading.utils.MyDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Singleton
@Module
public class MyDatabaseModule {
    private final MyDatabase database;

    public MyDatabaseModule(Context context) {
        this.database = MyDatabaseClientModel.getInstance(context).getMyDatabase();
    }

    @Singleton
    @Provides
    public MyDatabase providesMyDatabase() {
        return database;
    }
}
