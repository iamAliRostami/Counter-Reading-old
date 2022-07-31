package com.leon.counter_reading.di.module;

import android.content.Context;

import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.utils.custom_dialog.LovelyStandardDialog;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

//@Singleton
@Module
public class CustomDialogModule {
    private final LovelyStandardDialog lovelyStandardDialog;

    public CustomDialogModule(Context context) {
        final CustomDialogModel dialog = new CustomDialogModel(context);
        this.lovelyStandardDialog = dialog.getLovelyStandardDialog();
    }

    @Singleton
    @Provides
    public LovelyStandardDialog provideLovelyStandardDialog() {
        return lovelyStandardDialog;
    }
}
