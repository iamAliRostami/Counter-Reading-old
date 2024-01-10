package com.leon.counter_reading.di.view_model;

import static com.leon.counter_reading.helpers.Constants.MIN_DISTANCE_CHANGE_FOR_UPDATES;
import static com.leon.counter_reading.helpers.Constants.MIN_TIME_BW_UPDATES;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.infrastructure.ILocationTracking;
import com.leon.counter_reading.tables.SavedLocation;
import com.leon.counter_reading.utils.CustomToast;

public class GpsLocationTracking extends Service implements LocationListener, ILocationTracking {
    private static GpsLocationTracking instance = null;
    private volatile static Location location;
    protected LocationManager locationManager;
    private double latitude;
    private double longitude;

    public GpsLocationTracking() {
        getLocation();
    }

    public static synchronized GpsLocationTracking getInstance() {
        if (instance == null) {
            instance = new GpsLocationTracking();
            instance.addLocation(location);
        }
        return instance;
    }

    public static void setInstance(GpsLocationTracking instance) {
        GpsLocationTracking.instance = instance;
    }

    @SuppressLint("MissingPermission")
    @Override
    public Location getLocation() {
        try {
            locationManager = (LocationManager) MyApplication.getContext().getSystemService(LOCATION_SERVICE);
            boolean checkGPS = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean checkNetwork = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (checkGPS || checkNetwork) {
                if (checkGPS) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                if (checkNetwork) {
                    if (locationManager != null) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    }
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    @Override
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    @Override
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    @Override
    public double getAccuracy() {
        return location.getAccuracy();
    }

    @Override
    public void addLocation(Location location) {
        if (location != null && (location.getLatitude() != 0 || location.getLongitude() != 0)) {
            GpsLocationTracking.location = location;
            if (getApplicationComponent().SharedPreferenceModel()
                    .getBoolData(SharedReferenceKeys.POINT.getValue())) {
                SavedLocation savedLocation = new SavedLocation(location.getAccuracy(),
                        location.getLongitude(), location.getLatitude());
                getApplicationComponent().MyDatabase().savedLocationDao()
                        .insertSavedLocation(savedLocation);
            }
        }
    }

    @Override
    public Location getCurrentLocation() {
        return getLocation();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            instance.addLocation(location);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        new CustomToast().error("مکانیابی شما با مشکل مواجه شده است. \nپیش از ادامه ی کار موضوع را به پشتیبان اطلاع دهید.", Toast.LENGTH_LONG);
    }

    public void stopListener() {
        if (locationManager != null) {
            locationManager.removeUpdates(GpsLocationTracking.this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
