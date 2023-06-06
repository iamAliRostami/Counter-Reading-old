package com.leon.counter_reading.helpers;

import android.Manifest;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;
import com.leon.counter_reading.tables.ReadingData;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Constants {
    public static final Pattern IP_PATTERN = Pattern.compile("https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)");
    public static final String FONT_NAME = "font/font_1.ttf";
    public final static String DBName = "MyDatabase_32";
    public static final int TOAST_TEXT_SIZE = 20;

    public static final int GPS_CODE = 1231;
    public static final int ALL_FILES_ACCESS_REQUEST = 921;
    public static final int CARRIER_PRIVILEGE_STATUS = 901;
    public static final int REQUEST_NETWORK_CODE = 1232;
    public static final int REQUEST_WIFI_CODE = 1233;
    public static final int CAMERA_REQUEST = 1888;
    public static final int SETTING_REQUEST = 922;
    public static final int GALLERY_REQUEST = 1889;

    public static final int CAMERA = 1446;

    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    public static final long MIN_TIME_BW_UPDATES = 10000;
    public static final long FASTEST_INTERVAL = 10000;
//    public static final int MAX_IMAGE_SIZE = 100000;

    public static final ArrayList<Integer> IS_MANE = new ArrayList<>();
    public static final String[] STORAGE_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    public static final String[] PHOTO_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    public static final String[] RECORD_AUDIO_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
    public static final String[] LOCATION_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    public static final String ACTION_USB_PERMISSION = "com.leon.counter_reading.activities.USB_PERMISSION";
    public static final int INTERFACE_SUBCLASS = 6;
    public static final int INTERFACE_PROTOCOL = 80;

    public final static int ZIP_ROOT = 7896;
    public final static int SORT_BY_NAME = 0;
    public final static int SORT_BY_DATE = 1;
    public final static int SORT_BY_SIZE = 2;
    public final static int MAX_OFFLINE_ATTEMPT = 5;
    public final static File otgViewerCachePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/OTGViewer/cache");
    public final static File otgViewerPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/OTGViewer");


    public static long CURRENT_IMAGE_SIZE;

    public static int currentOfflineAttempts = 0;
    public static int POSITION = -1;

    public static Bitmap BITMAP_SELECTED_IMAGE;
    public static boolean FOCUS_ON_EDIT_TEXT;
    public static String zipAddress;
    public static Uri PHOTO_URI;


    public static final ReadingData readingData = new ReadingData(), readingDataTemp = new ReadingData();
    public static final ArrayList<ReadingConfigDefaultDto> readingConfigDefaultDtos = new ArrayList<>();
    public static final ArrayList<CounterStateDto> counterStateDtos = new ArrayList<>();
    public static final ArrayList<OnOffLoadDto> onOffLoadDtos = new ArrayList<>();
    public static final ArrayList<KarbariDto> karbariDtos = new ArrayList<>();
}
