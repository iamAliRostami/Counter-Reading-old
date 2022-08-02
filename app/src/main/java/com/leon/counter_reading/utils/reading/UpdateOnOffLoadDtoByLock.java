package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.os.AsyncTask;

import com.leon.counter_reading.tables.OnOffLoadDto;

public class UpdateOnOffLoadDtoByLock extends AsyncTask<Void, Void, Void> {
    private final OnOffLoadDto onOffLoadDto;

    public UpdateOnOffLoadDtoByLock(OnOffLoadDto onOffLoadDto) {
        super();
        this.onOffLoadDto = onOffLoadDto;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        getApplicationComponent().MyDatabase().onOffLoadDao().updateOnOffLoad(onOffLoadDto);
        return null;
    }
}