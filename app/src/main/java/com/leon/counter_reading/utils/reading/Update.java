package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getLocationTracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;

import com.leon.counter_reading.tables.OnOffLoadDto;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Update extends AsyncTask<Activity, Void, Void> {
    private final OnOffLoadDto onOffLoadDto;

    public Update(OnOffLoadDto onOffLoadDto) {
        super();
        this.onOffLoadDto = onOffLoadDto;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected Void doInBackground(Activity... activities) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy MM dd HH:mm:ss:SSS");

        try {
            final Location location = getLocationTracker(activities[0]).getCurrentLocation();
            if (location != null) {
                onOffLoadDto.x = location.getLongitude();
                onOffLoadDto.y = location.getLatitude();
                onOffLoadDto.gisAccuracy = location.getAccuracy();
//                onOffLoadDto.locationDateTime = dateFormatter.format(new Date(location.getTime()));
                onOffLoadDto.locationDateTime = convertToAscii(dateFormatter.format(new Date(location.getTime())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            onOffLoadDto.phoneDateTime = convertToAscii(dateFormatter.format(new Date(Calendar.getInstance().getTimeInMillis())));
        } catch (Exception e) {
            onOffLoadDto.phoneDateTime = dateFormatter.format(new Date(Calendar.getInstance().getTimeInMillis()));
        }
//        onOffLoadDto.phoneDateTime = dateFormatter.format(new Date(Calendar.getInstance().getTimeInMillis()));
//        date = new Date(Calendar.getInstance().getTimeInMillis());
        getApplicationComponent().MyDatabase().onOffLoadDao().updateOnOffLoad(onOffLoadDto);
        return null;
    }

    private String convertToAscii(String s) {
        String sTemp = Normalizer.normalize(s, Normalizer.Form.NFKD);
        String regex = "[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+";
        String finalString = new String(sTemp.replaceAll(regex, "").getBytes(StandardCharsets.US_ASCII), StandardCharsets.US_ASCII);
        return new String(finalString.getBytes(), StandardCharsets.UTF_8);
    }
}
