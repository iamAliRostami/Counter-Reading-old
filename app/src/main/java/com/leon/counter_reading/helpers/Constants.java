package com.leon.counter_reading.helpers;

import android.Manifest;
import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;

public class Constants {

    public static final String FONT_NAME = "font/font_1.ttf";
    public static final int TOAST_TEXT_SIZE = 20;

    public static final int GPS_CODE = 1231;
    public static final int REQUEST_NETWORK_CODE = 1232;
    public static final int REQUEST_WIFI_CODE = 1233;
    public static final int CAMERA_REQUEST = 1888;
    public static final int GALLERY_REQUEST = 1889;
    public static final int CARRIER_PRIVILEGE_STATUS = 901;
    public static final int ALL_FILES_ACCESS_REQUEST = 921;

    public static final int CAMERA = 1446;
    public static final int REPORT = 1445;
    public static final int NAVIGATION = 1903;
    public static final int COUNTER_LOCATION = 1914;
    public static final int DESCRIPTION = 1909;

    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    public static final long MIN_TIME_BW_UPDATES = 10000;
    public static final long FASTEST_INTERVAL = 10000;
    public static final int MAX_IMAGE_SIZE = 200000;

    public static int POSITION = -1;
    public static Bitmap BITMAP_SELECTED_IMAGE;
    public static Uri PHOTO_URI;

    public static boolean FOCUS_ON_EDIT_TEXT;
    public static final ArrayList<Integer> IS_MANE = new ArrayList<>();

    public static final String[] STORAGE_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    public static final String[] PHOTO_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    public static final String[] RECORD_AUDIO_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

    public static final String[] LOCATION_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    public static final String PHONE_PERMISSIONS = "android.permission.READ_PRIVILEGED_PHONE_STATE";
}
