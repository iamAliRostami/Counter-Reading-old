package com.leon.counter_reading.utils;

import static android.content.Context.TELEPHONY_SERVICE;
import static com.leon.counter_reading.helpers.Constants.CARRIER_PRIVILEGE_STATUS;
import static com.leon.counter_reading.helpers.Constants.GPS_CODE;
import static com.leon.counter_reading.helpers.Constants.PHOTO_PERMISSIONS;
import static com.leon.counter_reading.helpers.Constants.REQUEST_NETWORK_CODE;
import static com.leon.counter_reading.helpers.Constants.REQUEST_WIFI_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationManagerCompat;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;

import java.util.ArrayList;

public class PermissionManager {
    public static boolean checkRecorderPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void checkRecorderPermission(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            askRecorderPermission(activity);
        }
    }

    public static void askRecorderPermission(Activity activity) {
        final PermissionListener permissionlistener = new PermissionListener() {
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
                .setPermissions(Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE).check();
    }

    public static boolean checkCameraPermission(Context context) {
        //TODO
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager() && Settings.System.canWrite(context) &&
                    ActivityCompat.checkSelfPermission(context,
                            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        }
        return ActivityCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void checkCameraPermission(Activity activity) {
        //TODO
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!(Environment.isExternalStorageManager() && Settings.System.canWrite(activity) &&
                    ActivityCompat.checkSelfPermission(activity,
                            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
                askCameraPermission(activity);
            }
        }
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            askCameraPermission(activity);
        }
    }

    public static void askCameraPermission(Activity activity) {
        final PermissionListener permissionlistener = new PermissionListener() {
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
        //TODO
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
            activity.startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri));
        } else {
            new TedPermission(activity)
                    .setPermissionListener(permissionlistener)
                    .setRationaleMessage(activity.getString(R.string.confirm_permission))
                    .setRationaleConfirmText(activity.getString(R.string.allow_permission))
                    .setDeniedMessage(activity.getString(R.string.if_reject_permission))
                    .setDeniedCloseButtonText(activity.getString(R.string.close))
                    .setGotoSettingButtonText(activity.getString(R.string.allow_permission))
                    .setPermissions(PHOTO_PERMISSIONS).check();
        }
    }

    //TODO
    public static boolean checkStoragePermission(Context context) {
        return ActivityCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    //TODO
    public static void checkStoragePermission(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            askStoragePermission(activity);
        }
    }

    public static void askStoragePermission(Activity activity) {
        final PermissionListener permissionlistener = new PermissionListener() {
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
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE).check();
    }

    public static boolean checkLocationPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkLocationPermission(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return true;
        else askLocationPermission(activity);
        return false;
    }

    public static void askLocationPermission(Activity activity) {
        final PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(activity.getString(R.string.access_granted));
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
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION).check();
    }

    public static boolean enableGpsForResult(Activity activity) {
        final LocationManager locationManager = (LocationManager)
                activity.getSystemService(Context.LOCATION_SERVICE);
        final boolean enabled = LocationManagerCompat.isLocationEnabled(locationManager);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AlertDialogCustom));
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

    public static boolean enableGps(Activity activity) {
        final LocationManager locationManager = (LocationManager)
                activity.getSystemService(Context.LOCATION_SERVICE);
        final boolean enabled = LocationManagerCompat.isLocationEnabled(locationManager);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AlertDialogCustom));
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
        final ConnectivityManager connectivityManager = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return getSignalStatus(context) || (connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting());
    }

    @SuppressLint("MissingPermission")
    private static boolean getSignalStatus(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        boolean outOfService = true;
        try {
            for (int i = 0; i < tm.getAllCellInfo().size() && outOfService; i++) {
                final CellInfo info = tm.getAllCellInfo().get(i);
                if (info instanceof CellInfoGsm) {
                    final CellSignalStrengthGsm cell = ((CellInfoGsm) info).getCellSignalStrength();
                    outOfService = cell.getDbm() <= -110;
//                    Log.e("signal 1", String.valueOf(cell.getDbm()));
                } else if (info instanceof CellInfoCdma) {
                    final CellSignalStrengthCdma cell = ((CellInfoCdma) info).getCellSignalStrength();
                    outOfService = cell.getDbm() <= -110;
//                    Log.e("signal 2", String.valueOf(cell.getDbm()));
                } else if (info instanceof CellInfoLte) {
                    final CellSignalStrengthLte cell = ((CellInfoLte) info).getCellSignalStrength();
                    outOfService = cell.getDbm() <= -110;
//                    Log.e("signal 3", String.valueOf(cell.getDbm()));
                } else if (info instanceof CellInfoWcdma) {
                    final CellSignalStrengthWcdma cell = ((CellInfoWcdma) info).getCellSignalStrength();
                    outOfService = cell.getDbm() <= -110;
//                    Log.e("signal 4", String.valueOf(cell.getDbm()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        return true;
        return outOfService;
    }

    @SuppressLint("MissingPermission")
    public static int getSignal(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        boolean outOfService = true;
        int signal = 0;
        try {
            for (int i = 0; i < tm.getAllCellInfo().size() && outOfService; i++) {
                final CellInfo info = tm.getAllCellInfo().get(i);
                if (info instanceof CellInfoGsm) {
                    final CellSignalStrengthGsm cell = ((CellInfoGsm) info).getCellSignalStrength();
                    signal = cell.getDbm();
                    outOfService = signal <= -110;
                } else if (info instanceof CellInfoCdma) {
                    final CellSignalStrengthCdma cell = ((CellInfoCdma) info).getCellSignalStrength();
                    signal = cell.getDbm();
                    outOfService = signal <= -110;
                } else if (info instanceof CellInfoLte) {
                    final CellSignalStrengthLte cell = ((CellInfoLte) info).getCellSignalStrength();
                    signal = cell.getDbm();
                    outOfService = signal <= -110;
                } else if (info instanceof CellInfoWcdma) {
                    final CellSignalStrengthWcdma cell = ((CellInfoWcdma) info).getCellSignalStrength();
                    signal = cell.getDbm();
                    outOfService = signal <= -110;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signal;
    }

    public static void enableNetwork(Activity activity) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AlertDialogCustom));
        alertDialog.setCancelable(false);
        alertDialog.setTitle(activity.getString(R.string.network_setting));
        alertDialog.setMessage(R.string.active_network);
        alertDialog.setPositiveButton(R.string.setting, (dialog, which) -> enableMobileData(activity));
        alertDialog.setNegativeButton(R.string.close, (dialog, which) -> {
            dialog.cancel();
            forceClose(activity);
        });
        alertDialog.show();
    }

    public static void enableMobileData(Activity activity) {
        activity.startActivityForResult(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS),
                REQUEST_NETWORK_CODE);
    }

    public static void enableMobileWifi(Activity activity) {
        activity.startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), REQUEST_WIFI_CODE);
    }

    public static void forceClose(Activity activity) {
        new CustomToast().error(activity.getString(R.string.permission_not_completed));
        activity.finish();
    }

    public static boolean hasCarrierPrivileges(Activity activity) {
        final TelephonyManager tm = (TelephonyManager)
                new ContextWrapper(activity).getSystemService(TELEPHONY_SERVICE);
        final boolean isCarrier = tm.hasCarrierPrivileges();
        if (!isCarrier) {
            final int hasPermission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.READ_PRIVILEGED_PHONE_STATE");
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                if (!activity.shouldShowRequestPermissionRationale("android.permission.READ_PRIVILEGED_PHONE_STATE")) {
                    ActivityCompat.requestPermissions(activity, new String[]{
                            "android.permission.READ_PRIVILEGED_PHONE_STATE"}, CARRIER_PRIVILEGE_STATUS);
                }
            }
        }
        return isCarrier;
    }
}
