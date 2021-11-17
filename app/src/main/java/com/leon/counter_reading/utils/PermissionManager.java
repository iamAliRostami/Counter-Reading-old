package com.leon.counter_reading.utils;

import static android.content.Context.TELEPHONY_SERVICE;
import static com.leon.counter_reading.helpers.Constants.CARRIER_PRIVILEGE_STATUS;
import static com.leon.counter_reading.helpers.Constants.GPS_CODE;
import static com.leon.counter_reading.helpers.Constants.LOCATION_PERMISSIONS;
import static com.leon.counter_reading.helpers.Constants.PHONE_PERMISSIONS;
import static com.leon.counter_reading.helpers.Constants.PHOTO_PERMISSIONS;
import static com.leon.counter_reading.helpers.Constants.POSITION;
import static com.leon.counter_reading.helpers.Constants.RECORD_AUDIO_PERMISSIONS;
import static com.leon.counter_reading.helpers.Constants.REQUEST_NETWORK_CODE;
import static com.leon.counter_reading.helpers.Constants.REQUEST_WIFI_CODE;
import static com.leon.counter_reading.helpers.Constants.STORAGE_PERMISSIONS;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationManagerCompat;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class PermissionManager {
    public static boolean checkRecorderPermission(Context context) {
        for (String s : RECORD_AUDIO_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(context, s) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    public static void checkRecorderPermission(Activity activity) {
        for (String s : RECORD_AUDIO_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(activity, s) != PackageManager.PERMISSION_GRANTED)
                askRecorderPermission(activity);
        }
    }

    public static void askRecorderPermission(Activity activity) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(activity.getString(R.string.access_granted));
                checkRecorderPermission(activity);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                forceClose(activity);
            }
        };
        new TedPermission(activity)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(activity.getString(R.string.confirm_permission))
                .setRationaleConfirmText(activity.getString(R.string.allow_permission))
                .setDeniedMessage(activity.getString(R.string.if_reject_permission))
                .setDeniedCloseButtonText(activity.getString(R.string.close))
                .setGotoSettingButtonText(activity.getString(R.string.allow_permission))
                .setPermissions(RECORD_AUDIO_PERMISSIONS).check();
    }

    public static boolean checkCameraPermission(Context context) {
        for (String s : PHOTO_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(context, s) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    public static void checkCameraPermission(Activity activity) {
        for (String s : PHOTO_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(activity, s) != PackageManager.PERMISSION_GRANTED)
                askCameraPermission(activity);
        }
    }

    public static void askCameraPermission(Activity activity) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(activity.getString(R.string.access_granted));
                checkCameraPermission(activity);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                forceClose(activity);
            }
        };
        new TedPermission(activity)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(activity.getString(R.string.confirm_permission))
                .setRationaleConfirmText(activity.getString(R.string.allow_permission))
                .setDeniedMessage(activity.getString(R.string.if_reject_permission))
                .setDeniedCloseButtonText(activity.getString(R.string.close))
                .setGotoSettingButtonText(activity.getString(R.string.allow_permission))
                .setPermissions(PHOTO_PERMISSIONS).check();
    }

    public static boolean checkStoragePermission(Context context) {
        for (String s : STORAGE_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(context, s) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    public static void checkStoragePermission(Activity activity) {
        for (String s : STORAGE_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(activity, s) != PackageManager.PERMISSION_GRANTED)
                askStoragePermission(activity);
        }
    }

    public static void askStoragePermission(Activity activity) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(activity.getString(R.string.access_granted));
                checkStoragePermission(activity);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                forceClose(activity);
            }
        };
        new TedPermission(activity)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(activity.getString(R.string.confirm_permission))
                .setRationaleConfirmText(activity.getString(R.string.allow_permission))
                .setDeniedMessage(activity.getString(R.string.if_reject_permission))
                .setDeniedCloseButtonText(activity.getString(R.string.close))
                .setGotoSettingButtonText(activity.getString(R.string.allow_permission))
                .setPermissions(STORAGE_PERMISSIONS).check();
    }

    public static boolean checkLocationPermission(Context context) {
        for (String s : LOCATION_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(context, s) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    public static void checkLocationPermission(Activity activity) {
        for (String s : LOCATION_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(activity, s) != PackageManager.PERMISSION_GRANTED)
                askLocationPermission(activity);
        }
    }

    public static void askLocationPermission(Activity activity) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(activity.getString(R.string.access_granted));
                checkLocationPermission(activity);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                PermissionManager.forceClose(activity);
            }
        };
        new TedPermission(activity)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(activity.getString(R.string.confirm_permission))
                .setRationaleConfirmText(activity.getString(R.string.allow_permission))
                .setDeniedMessage(activity.getString(R.string.if_reject_permission))
                .setDeniedCloseButtonText(activity.getString(R.string.close))
                .setGotoSettingButtonText(activity.getString(R.string.allow_permission))
                .setPermissions(LOCATION_PERMISSIONS).check();
    }

    public static boolean gpsEnabled(Activity activity) {
        LocationManager locationManager = (LocationManager)
                activity.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled =
                LocationManagerCompat.isLocationEnabled(Objects.requireNonNull(locationManager));
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AlertDialogCustom));
        if (!enabled) {
            alertDialog.setCancelable(false);
            alertDialog.setTitle(activity.getString(R.string.gps_setting));
            alertDialog.setMessage(R.string.active_gps);
            alertDialog.setPositiveButton(R.string.setting, (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivityForResult(intent, GPS_CODE);
            });
            alertDialog.setNegativeButton(R.string.close, (dialog, which) -> {
                dialog.cancel();
                forceClose(activity);
            });
            alertDialog.show();
        }
        return enabled;
    }

    public static boolean gpsEnabledNew(Activity activity) {
        LocationManager locationManager = (LocationManager)
                activity.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = LocationManagerCompat.isLocationEnabled(locationManager);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AlertDialogCustom));
        if (!enabled) {
            alertDialog.setCancelable(false);
            alertDialog.setTitle(activity.getString(R.string.gps_setting));
            alertDialog.setMessage(R.string.active_gps);
            alertDialog.setPositiveButton(R.string.setting, (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);
            });
            alertDialog.setNegativeButton(R.string.close, (dialog, which) -> {
                new CustomToast().warning("به علت عدم دسترسی به مکان یابی، امکان ثبت وجود ندارد.");
                dialog.cancel();
            });
            alertDialog.show();
        }
        return enabled;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static void enableNetwork(Activity activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AlertDialogCustom));
        alertDialog.setCancelable(false);
        alertDialog.setTitle(activity.getString(R.string.network_setting));
        alertDialog.setMessage(R.string.active_network);
        alertDialog.setPositiveButton(R.string.setting, (dialog, which) -> setMobileDataEnabled(activity));
        alertDialog.setNegativeButton(R.string.close, (dialog, which) -> {
            dialog.cancel();
            forceClose(activity);
        });
        alertDialog.show();
    }

    public static void setMobileDataEnabled(Activity activity) {
        activity.startActivityForResult(
                new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS), REQUEST_NETWORK_CODE);
    }

    public static void setMobileWifiEnabled(Activity activity) {
        activity.startActivityForResult(
                new Intent(Settings.ACTION_WIFI_SETTINGS), REQUEST_WIFI_CODE);
    }

    public static void forceClose(Activity activity) {
        new CustomToast().error(activity.getString(R.string.permission_not_completed));
        POSITION = -1;
        activity.finish();
    }

    public static boolean hasCarrierPrivileges(Activity activity) {
        TelephonyManager tm = (TelephonyManager) new ContextWrapper(activity).getSystemService(TELEPHONY_SERVICE);
        boolean isCarrier = tm.hasCarrierPrivileges();
        if (!isCarrier) {
            if (ActivityCompat.checkSelfPermission(activity,
                    PHONE_PERMISSIONS) != PackageManager.PERMISSION_GRANTED) {
                if (!activity.shouldShowRequestPermissionRationale(PHONE_PERMISSIONS)) {
                    ActivityCompat.requestPermissions(activity, new String[]{
                            PHONE_PERMISSIONS}, CARRIER_PRIVILEGE_STATUS);
                }
            }
        }
        return isCarrier;
    }
}
