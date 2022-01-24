package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;

import com.leon.counter_reading.tables.OnOffLoadDto;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Update extends AsyncTask<Activity, Void, Void> {
    private final OnOffLoadDto onOffLoadDto;
    private final Location location;

    public Update(OnOffLoadDto onOffLoadDto, Location location) {
        super();
        this.location = location;
        this.onOffLoadDto = onOffLoadDto;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected Void doInBackground(Activity... activities) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy MM dd HH:mm:ss:SSS");

        if (location != null) {
            onOffLoadDto.x = location.getLongitude();
            onOffLoadDto.y = location.getLatitude();
            onOffLoadDto.gisAccuracy = location.getAccuracy();
            onOffLoadDto.locationDateTime = dateFormatter.format(new Date(location.getTime()));
        }
        onOffLoadDto.phoneDateTime = dateFormatter.format(new Date(Calendar.getInstance().getTimeInMillis()));
//        date = new Date(Calendar.getInstance().getTimeInMillis());
        getApplicationComponent().MyDatabase().onOffLoadDao().updateOnOffLoad(onOffLoadDto);
        return null;
    }
}
