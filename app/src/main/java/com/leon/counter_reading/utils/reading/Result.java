package com.leon.counter_reading.utils.reading;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.tables.OnOffLoadDto;

import java.util.ArrayList;

public class Result extends AsyncTask<Activity, Void, Void> {
    private final Intent data;
    private final ArrayList<OnOffLoadDto> onOffLoadDtos;
    private final ArrayList<OnOffLoadDto> onOffLoadDtosTemp;

    public Result(Intent data, ArrayList<OnOffLoadDto> onOffLoadDtos, ArrayList<OnOffLoadDto> onOffLoadDtosTemp) {
        super();
        this.data = data;
        this.onOffLoadDtosTemp = new ArrayList<>(onOffLoadDtosTemp);
        this.onOffLoadDtos = new ArrayList<>(onOffLoadDtos);
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        int position = data.getExtras().getInt(BundleEnum.POSITION.getValue()), i = 0;
        String uuid = data.getExtras().getString(BundleEnum.BILL_ID.getValue());
        MyApplication.getApplicationComponent().MyDatabase().onOffLoadDao().updateOnOffLoad(true, uuid);


        onOffLoadDtos.set(position, MyApplication.getApplicationComponent().MyDatabase()
                .onOffLoadDao().getAllOnOffLoadById(uuid, onOffLoadDtos.get(position).trackNumber));
        boolean found = false;
        while (!found) {
            if (onOffLoadDtosTemp.get(i).id.equals(uuid)) {
                onOffLoadDtosTemp.set(i, onOffLoadDtos.get(position));
                found = true;
            }
            i++;
        }
        ((ReadingActivity) (activities[0])).setReadingDataOnOffload(onOffLoadDtos, onOffLoadDtosTemp);
        return null;
    }
}