package com.leon.counter_reading.utils.navigation;

import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.fragments.dialog.NavigationFragment;

public class Navigating extends AsyncTask<Activity, Void, Void> {
    private final int possibleEmpty;
    private final int position;
    private final String uuid;
    private final String possibleEshterak;
    private final String possibleMobile;
    private final String phoneNumber;
    private final String serialNumber;
    private final String address;

    public Navigating(int position, String uuid, int possibleEmpty, String possibleEshterak,
                      String possibleMobile, String phoneNumber, String serialNumber, String address) {
        super();
        this.possibleEmpty = possibleEmpty;
        this.position = position;
        this.uuid = uuid;
        this.possibleEshterak = possibleEshterak;
        this.possibleMobile = possibleMobile;
        this.phoneNumber = phoneNumber;
        this.serialNumber = serialNumber;
        this.address = address;
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        getApplicationComponent().MyDatabase().onOffLoadDao().updateOnOffLoad(uuid,
                possibleEshterak, possibleMobile, possibleEmpty, phoneNumber,
                serialNumber, address);
        NavigationFragment.newInstance().readingActivity.setResult(position, uuid);
        return null;
    }
}