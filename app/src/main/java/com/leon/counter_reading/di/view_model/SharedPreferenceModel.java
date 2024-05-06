package com.leon.counter_reading.di.view_model;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;

import javax.inject.Inject;

public class SharedPreferenceModel implements ISharedPreferenceManager {
    private final SharedPreferences appPrefs;

    @Inject
    public SharedPreferenceModel(Context context, String xml) {
        appPrefs = context.getSharedPreferences(xml, MODE_PRIVATE);
    }

    @Override
    public boolean checkIsNotEmpty(String key) {
        if (appPrefs == null) {
            return false;
        } else if (!appPrefs.getString(key, "").isEmpty()) {
            return true;
        } else return !appPrefs.getString(key, "").isEmpty();
    }

    @Override
    public boolean checkIsNotEmpty(String key, boolean b) {
        return appPrefs != null;
    }

    @Override
    public void putData(String key, int value) {
        final SharedPreferences.Editor prefsEditor = appPrefs.edit();
        prefsEditor.putInt(key, value);
        prefsEditor.apply();

    }

    @Override
    public void putData(String key, String data) {
        final SharedPreferences.Editor prefsEditor = appPrefs.edit();
        prefsEditor.putString(key, data);
        prefsEditor.apply();
    }

    @Override
    public void putData(String key, boolean value) {
        final SharedPreferences.Editor prefsEditor = appPrefs.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.apply();
    }

    @Override
    public String getStringData(String key) {
        return appPrefs.getString(key, "");
    }

    @Override
    public int getIntData(String key) {
        return appPrefs.getInt(key, 1);
    }

    @Override
    public int getIntNullData(String key) {
        return appPrefs.getInt(key, 0);
    }

    @Override
    public boolean getBoolData(String key) {
        return appPrefs.getBoolean(key, false);
    }
}
