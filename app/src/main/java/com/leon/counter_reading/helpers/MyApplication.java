package com.leon.counter_reading.helpers;

import static android.os.Build.UNKNOWN;
import static com.leon.counter_reading.enums.SharedReferenceKeys.USERNAME;
import static com.leon.counter_reading.enums.SharedReferenceNames.ACCOUNT;
import static com.leon.counter_reading.helpers.Constants.FONT_NAME;
import static com.leon.counter_reading.helpers.Constants.TOAST_TEXT_SIZE;
import static com.leon.counter_reading.utils.PermissionManager.hasCarrierPrivileges;
import static com.leon.counter_reading.utils.locating.CheckSensor.checkSensor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Typeface;
import android.os.Build;
import android.provider.Settings;

import androidx.multidex.MultiDex;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.di.component.ActivityComponent;
import com.leon.counter_reading.di.component.ApplicationComponent;
import com.leon.counter_reading.di.component.DaggerActivityComponent;
import com.leon.counter_reading.di.component.DaggerApplicationComponent;
import com.leon.counter_reading.di.module.CustomDialogModule;
import com.leon.counter_reading.di.module.CustomProgressModule;
import com.leon.counter_reading.di.module.FlashModule;
import com.leon.counter_reading.di.module.LocationTrackingModule;
import com.leon.counter_reading.di.module.MyDatabaseModule;
import com.leon.counter_reading.di.module.NetworkModule;
import com.leon.counter_reading.di.module.SharedPreferenceModule;
import com.leon.counter_reading.infrastructure.ILocationTracking;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;
import com.yandex.metrica.profile.Attribute;
import com.yandex.metrica.profile.UserProfile;

import es.dmoral.toasty.Toasty;

public class MyApplication extends Application {
    private static Context appContext;
    private static int ERROR_COUNTER = 0;
    private static ApplicationComponent applicationComponent;
    private static ActivityComponent activityComponent;

    public static ActivityComponent getActivityComponent() {
        return activityComponent;
    }

    public static void setActivityComponent(Activity activity) {
        activityComponent = DaggerActivityComponent.builder()
                .customDialogModule(new CustomDialogModule(activity))
                .locationTrackingModule(new LocationTrackingModule(activity)).build();
    }

    public static ILocationTracking getLocationTracker(Activity activity) {
        return checkSensor(activity, false) ? activityComponent.LocationTrackingGoogle() :
                activityComponent.LocationTrackingGps();
    }

    public static ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    public static Context getContext() {
        return appContext;
    }

    public static int getErrorCounter() {
        return ERROR_COUNTER;
    }

    public static void setErrorCounter(int errorCounter) {
        ERROR_COUNTER = errorCounter;
    }

    public static void onActivitySetTheme(Activity activity, int theme, boolean actionBar) {
        if (!actionBar) {
            if (theme == 1) {
                activity.setTheme(R.style.AppTheme_NoActionBar);
            } else if (theme == 2) {
                activity.setTheme(R.style.GreenBlue_NoActionBar);
            } else if (theme == 3) {
                activity.setTheme(R.style.AppTheme_Indigo_NoActionBar);
            } else if (theme == 4) {
                activity.setTheme(R.style.AppTheme_DarkGrey_NoActionBar);
            }
        } else {
            if (theme == 1) {
                activity.setTheme(R.style.AppTheme);
            } else if (theme == 2) {
                activity.setTheme(R.style.GreenBlue);
            } else if (theme == 3) {
                activity.setTheme(R.style.AppTheme_Indigo);
            } else if (theme == 4) {
                activity.setTheme(R.style.AppTheme_DarkGrey);
            }
        }
    }

    public static String getDBName() {
        return "MyDatabase_19";
    }

    public static String getAndroidVersion() {
        final String release = Build.VERSION.RELEASE;
        final int sdkVersion = Build.VERSION.SDK_INT;
        return "Android SDK: " + sdkVersion + " (" + release + ")";
    }

    @SuppressLint("HardwareIds")
    public static String getSerial(Activity activity) {
        String serial = Build.SERIAL;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (hasCarrierPrivileges(activity))
                serial = Build.getSerial();
        }
        if (serial.equals(UNKNOWN))
            serial = Settings.Secure.getString(new ContextWrapper(activity).getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        return serial;
    }

    @Override
    public void onCreate() {
        appContext = getApplicationContext();
        Toasty.Config.getInstance().tintIcon(true)
                .setToastTypeface(Typeface.createFromAsset(appContext.getAssets(), FONT_NAME))
                .setTextSize(TOAST_TEXT_SIZE).allowQueue(true).apply();
        applicationComponent = DaggerApplicationComponent
                .builder().networkModule(new NetworkModule())
                .flashModule(new FlashModule(appContext))
                .customProgressModule(new CustomProgressModule())
                .myDatabaseModule(new MyDatabaseModule(appContext))
                .sharedPreferenceModule(new SharedPreferenceModule(appContext, ACCOUNT)).build();
        applicationComponent.inject(this);
        super.onCreate();
        if (!BuildConfig.BUILD_TYPE.equals("release")) {
            setupYandex();
        }
    }


    protected void setupYandex() {
        final UserProfile userProfile = UserProfile.newBuilder().apply(Attribute.name()
                .withValue(applicationComponent.SharedPreferenceModel()
                        .getStringData(USERNAME.getValue()))).build();
        final YandexMetricaConfig config = com.yandex.metrica.YandexMetricaConfig
                .newConfigBuilder("6d39e473-5c5c-4163-9c4c-21eb91758e8f").withLogs()
                .withAppVersion(BuildConfig.VERSION_NAME).build();
        YandexMetrica.activate(appContext, config);
        YandexMetrica.enableActivityAutoTracking(this);
        YandexMetrica.activate(getApplicationContext(), config);
        YandexMetrica.reportUserProfile(userProfile);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
