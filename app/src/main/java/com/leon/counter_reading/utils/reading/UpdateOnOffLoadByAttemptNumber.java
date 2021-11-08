package com.leon.counter_reading.utils.reading;

import android.os.AsyncTask;

import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.tables.OnOffLoadDto;

public class UpdateOnOffLoadByAttemptNumber extends AsyncTask<Void, Void, Void> {
    private final OnOffLoadDto onOffLoadDto;

    public UpdateOnOffLoadByAttemptNumber(OnOffLoadDto onOffLoadDto) {
        super();
        this.onOffLoadDto = onOffLoadDto;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        MyApplication.getApplicationComponent().MyDatabase().onOffLoadDao().updateOnOffLoad(onOffLoadDto);
        return null;
    }
}