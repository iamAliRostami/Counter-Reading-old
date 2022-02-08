package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.helpers.Constants.readingData;
import static com.leon.counter_reading.helpers.Constants.readingDataTemp;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.app.Activity;
import android.os.AsyncTask;

public class Result extends AsyncTask<Activity, Void, Void> {
    final int position;
    final String uuid;

    public Result(int position, String uuid) {
        super();
        this.position = position;
        this.uuid = uuid;
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        getApplicationComponent().MyDatabase().onOffLoadDao().updateOnOffLoad(true, uuid);
        readingData.onOffLoadDtos.set(position, getApplicationComponent().MyDatabase()
                .onOffLoadDao().getAllOnOffLoadById(uuid, readingData.onOffLoadDtos.get(position).trackingId));
        int i = 0;
        boolean found = false;
        while (!found && i < readingDataTemp.onOffLoadDtos.size()) {
            if (readingDataTemp.onOffLoadDtos.get(i).id.equals(uuid)) {
                readingDataTemp.onOffLoadDtos.set(i, readingData.onOffLoadDtos.get(position));
                found = true;
            }
            i++;
        }
        return null;
    }
}