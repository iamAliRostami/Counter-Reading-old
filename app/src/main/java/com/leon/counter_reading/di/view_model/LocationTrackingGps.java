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
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.infrastructure.ILocationTracking;
import com.leon.counter_reading.tables.SavedLocation;
import com.leon.counter_reading.utils.CustomToast;

public class LocationTrackingGps extends Service implements LocationListener, ILocationTracking {
    private static LocationTrackingGps instance = null;
    private volatile static Location location;
    protected LocationManager locationManager;
    private double latitude;
    private double longitude;

    public LocationTrackingGps() {
//        this.context = context;
        getLocation();
    }

    //    public static LocationTrackingGps getInstance() {
//        return instance;
//    }
    public static synchronized LocationTrackingGps getInstance() {
        if (instance == null) {
            instance = new LocationTrackingGps();
            instance.addLocation(location);
        }
        return instance;
    }

    @SuppressLint("MissingPermission")
    @Override
    public Location getLocation() {
        try {
//            Log.e("here", "1");
            locationManager = (LocationManager) MyApplication.getContext().getSystemService(LOCATION_SERVICE);

            // get GPS status
            boolean checkGPS = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // get network provider status
            boolean checkNetwork = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (checkGPS || checkNetwork) {
//                Log.e("here", "2");
                // if GPS Enabled get lat/long using GPS Services
                if (checkGPS) {
//                    Log.e("here", "3");
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
//                        Log.e("here", "4");
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
//                            Log.e("here", "5");
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                if (checkNetwork) {
//                    Log.e("here", "6");
                    if (locationManager != null) {
//                        Log.e("here", "7");
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    }
                    if (locationManager != null) {
//                        Log.e("here", "8");
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                    if (location != null) {
//                        Log.e("here", "9");
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            } /*else {
//                Toast.makeText(context, "No Service Provider is available", Toast.LENGTH_SHORT).show();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
//            Log.e("error", e.toString());
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
            LocationTrackingGps.location = location;
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
    public Location getCurrentLocation(/*Context context*/) {
        return getLocation();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null)
            try {
                instance.addLocation(location);
            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.e("onStatusChanged", String.valueOf(status));
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        new CustomToast().error("مکانیابی شما با مشکل مواجه شده است. \nپیش از ادامه ی کار موضوع را به پشتیبان اطلاع دهید.", Toast.LENGTH_LONG);
        Log.e("onStatusChanged", provider);
    }

    public void stopListener() {
        if (locationManager != null) {
            locationManager.removeUpdates(LocationTrackingGps.this);
        }
    }

    public static void setInstance(LocationTrackingGps instance) {
        LocationTrackingGps.instance = instance;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
