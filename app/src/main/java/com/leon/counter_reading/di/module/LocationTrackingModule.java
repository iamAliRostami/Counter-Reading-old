package com.leon.counter_reading.di.module;

import android.app.Activity;

import com.leon.counter_reading.di.view_model.GoogleLocationTracking;
import com.leon.counter_reading.di.view_model.GpsLocationTracking;
import com.leon.counter_reading.utils.locating.CheckSensor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Singleton
@Module
public class LocationTrackingModule {
    private GoogleLocationTracking googleTracking;
    private GpsLocationTracking gpsTracking;

    public LocationTrackingModule(Activity activity) {
        if (CheckSensor.checkSensor(activity, true))
            googleTracking = GoogleLocationTracking.getInstance(activity);
        else
            gpsTracking = GpsLocationTracking.getInstance();
    }

    @Singleton
    @Provides
    public GpsLocationTracking providesLocationTrackingGps() {
        return gpsTracking;
    }


    @Singleton
    @Provides
    public GoogleLocationTracking providesLocationTrackingGoogle() {
        return googleTracking;
    }
}
