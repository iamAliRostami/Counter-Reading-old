package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.enums.BundleEnum.BILL_ID;
import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.helpers.Constants.readingData;
import static com.leon.counter_reading.helpers.Constants.readingDataTemp;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

public class DataResult extends AsyncTask<Activity, Void, Void> {
    private final Intent data;

    public DataResult(Intent data) {
        super();
        this.data = data;
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        final int position = data.getExtras().getInt(POSITION.getValue());
        final String uuid = data.getExtras().getString(BILL_ID.getValue());
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