package com.leon.counter_reading.di.view_model;


import static com.leon.counter_reading.helpers.Constants.FASTEST_INTERVAL;
import static com.leon.counter_reading.helpers.Constants.MIN_TIME_BW_UPDATES;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.infrastructure.ILocationTracking;
import com.leon.counter_reading.tables.SavedLocation;

import org.jetbrains.annotations.NotNull;

public class GoogleLocationTracking extends Service implements ILocationTracking {
    private static GoogleLocationTracking instance;
    private static Location location;
    private static LocationCallback locationCallback;
    private final OnSuccessListener<Location> onSuccessListener = this::addLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;

    public GoogleLocationTracking(Activity activity) {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NotNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    addLocation(location);
                }
            }
        };
        startFusedLocation(activity);
    }

    public static synchronized GoogleLocationTracking getInstance(Activity activity) {
        if (instance == null) {
            instance = new GoogleLocationTracking(activity);
        }
        return instance;
    }

    public static GoogleLocationTracking getInstance() {
        return instance;
    }

    public static void setInstance(GoogleLocationTracking instance) {
        GoogleLocationTracking.instance = instance;
    }

    void startFusedLocation(Activity activity) {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(MIN_TIME_BW_UPDATES);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        registerRequestUpdateGoogle(activity);
    }

    void stopFusedLocation() {
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @SuppressLint("MissingPermission")
    void registerRequestUpdateGoogle(Activity activity) {
        fusedLocationClient.getLastLocation().addOnSuccessListener(activity, onSuccessListener);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    public void addLocation(Location location) {
        if (location != null) {
            GoogleLocationTracking.location = location;
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
        return location;
    }

    @Override
    public double getAccuracy() {
        return location.getAccuracy();
    }

    @Override
    public double getLongitude() {
        return location.getLongitude();
    }

    @Override
    public double getLatitude() {
        return location.getLatitude();
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public IBinder onBind(Intent intent) {
        stopFusedLocation();
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopFusedLocation();
    }
}