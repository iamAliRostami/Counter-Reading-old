package com.leon.counter_reading.helpers;


import static com.leon.counter_reading.enums.SharedReferenceKeys.PROXY;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getLocationTracker;
import static com.leon.counter_reading.utils.Converters.replaceNonstandardDigits;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.enums.CompanyNames;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DifferentCompanyManager {

    public static CompanyNames getActiveCompanyName() {
        return BuildConfig.COMPANY_NAME;
    }

    public static String getBaseUrl() {
        if (getApplicationComponent().SharedPreferenceModel().checkIsNotEmpty(PROXY.getValue())) {
            final String proxy = getApplicationComponent().SharedPreferenceModel().getStringData(PROXY.getValue());
            if (proxy.startsWith("http://") && proxy.length() > 7)
                return proxy;
            if (proxy.startsWith("https://") && proxy.length() > 8)
                return proxy;
        }
        switch (getActiveCompanyName()) {
            case ESF:
//                return "https://37.191.92.157/";//Internet
                return "http://172.18.12.36/";
            case ZONE1:
                return "http://217.146.220.33:50011/";
            case ZONE2:
                return "http://212.16.75.194:8080/";
            case ZONE3:
                return "http://46.209.219.36:90/";
            case ZONE4:
                return "http://81.12.106.167:8081/";
            case ZONE5:
                return "http://80.69.252.151/";
            case ZONE6:
                return "http://85.133.190.221:4121/";
            case TSW:
                return "http://81.90.148.25/";
            case TE:
                return "http://185.120.137.254";
            case TSE:
                return "http://172.28.5.40/";
//            return "http://46.209.181.2:9098/";
//                return "http://5.160.85.228:9098/";
            case TW:
                return "http://217.66.195.75/";
            case KSH:
                return "http://46.225.241.211:25123/";
            case DEBUG:
                return "http://192.168.43.185:45458/";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static String getLocalBaseUrl() {
        switch (getActiveCompanyName()) {
            case ESF:
                return "http://172.18.12.14:100";
            case ESF_MAP:
                return "http://172.18.12.242/osm_tiles/";
            case ZONE1:
                return "http://172.21.0.16/";
            case ZONE2:
                return "http://172.22.4.71/";
            case ZONE3:
                return "http://172.23.0.113/";
            case ZONE4:
                return "http://172.24.13.23/";
            case ZONE5:
                return "http://172.25.0.72/";
            case ZONE6:
                return "http://172.26.0.32/";
            case TSW:
                return "http://172.30.1.22/";
            case TE:
                return "http://172.31.0.25/";
            case TSE:
                return "http://172.28.5.40/";
            case TW:
                return "http://172.28.5.41/";
            case KSH:
                return "http://46.209.219.36:90";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static String getAhad() {
        switch (getActiveCompanyName()) {
            case ESF:
                return "واحد";
            case ZONE3:
            case ZONE4:
            case ZONE6:
            case TSE:
            case TW:
            case KSH:
                return "آحاد";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static String getAhad1() {
        switch (getActiveCompanyName()) {
            case ESF:
                return "واحد مسکونی";
            case KSH:
            case ZONE3:
            case ZONE4:
            case ZONE6:
            case TW:
            case TSE:
                return "آحاد اصلی";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static String getAhad2() {
        switch (getActiveCompanyName()) {
            case ESF:
                return "واحد تجاری";
            case KSH:
            case ZONE3:
            case ZONE4:
            case ZONE6:
            case TW:
            case TSE:
                return "آحاد فرعی";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static String getAhadTotal() {
        switch (getActiveCompanyName()) {
            case ESF:
                return "واحد کل";
            case ZONE3:
            case ZONE4:
            case ZONE6:
            case KSH:
            case TW:
            case TSE:
                return "آحاد مصرف";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static int getImageNumber() {
        switch (getActiveCompanyName()) {
            case ESF:
            case ZONE3:
            case ZONE4:
            case ZONE6:
            case TSE:
            case TW:
            case KSH:
                return 4;
            default:
                return 6;
        }
    }

    public static int getShowError() {
        switch (getActiveCompanyName()) {
            case ESF:
            case ZONE3:
            case ZONE4:
            case ZONE6:
            case TSE:
            case TW:
            case KSH:
                return 3;
            default:
                return 5;
        }
    }

    public static int getLockNumber() {
        switch (getActiveCompanyName()) {
            case ESF:
            case ZONE3:
            case ZONE4:
            case ZONE6:
            case TSE:
            case TW:
            case KSH:
                return 6;
            default:
                return 5;
        }
    }

    public static int getEshterakMinLength() {
        switch (getActiveCompanyName()) {
            case ESF:
            case KSH:
            case ZONE3:
            case ZONE4:
            case ZONE6:
            case TW:
            case TSE:
                return 5;
            default:
                return 10;
        }
    }

    public static int getEshterakMaxLength() {
        switch (getActiveCompanyName()) {
            case ESF:
            case KSH:
            case ZONE3:
            case ZONE4:
            case ZONE6:
                return 15;
            case TSE:
            case TW:
                return 12;
            default:
                return 10;
        }
    }

    public static String getSecondSearchItem() {
        switch (getActiveCompanyName()) {
            case ESF:
                return "ردیف";
            case ZONE3:
            case ZONE4:
            case ZONE6:
            case TSE:
            case TW:
            case KSH:
                return "شماره پرونده";
            default:
                return "دیگر";
        }
    }

    public static String getCompanyName() {
        switch (getActiveCompanyName()) {
            case ZONE1:
                return "آبقا منطقه یک";
            case ZONE2:
                return "آبفا منطقه دو";
            case ZONE3:
                return "آبفا منطقه سه";
            case ZONE4:
                return "آبفا منطقه چهار";
            case ZONE5:
                return "آبفا منطقه پنج";
            case ZONE6:
                return "آبقا منطقه شش";
            case TE:
                return "آبفا شرق";
            case TSW:
                return "آبفا جنوب غربی";
            case TSE:
                return "آبفا جنوب شرقی";
            case TW:
                return "آبفا غرب";
            case ESF:
                return "آبفا استان اصفهان";
            case KSH:
                return "آبفا استان کرمانشاه";
            default:
                throw new UnsupportedOperationException();
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String getExpireDate(Activity activity) {
        final Location location = getLocationTracker(activity).getCurrentLocation();
        Date date = new Date(location != null ? location.getTime() :
                Calendar.getInstance().getTimeInMillis());
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch (getActiveCompanyName()) {
            case ZONE1:
            case ZONE2:
            case ZONE3:
            case ZONE4:
            case ZONE5:
            case ZONE6:
            case TSW:
            case TE:
            case TSE:
            case TW:
            case ESF:
                calendar.add(Calendar.DAY_OF_YEAR, -20);
        }
        date = new Date(calendar.getTimeInMillis());
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        return replaceNonstandardDigits(dateFormatter.format(date));
    }

    public static boolean gallerySelector() {
        switch (getActiveCompanyName()) {
            case ZONE1:
            case ZONE2:
//            case ZONE3:
            case ZONE4:
            case ZONE5:
            case ZONE6:
            case TSW:
            case TE:
            case TW:
            case ESF:
//            case TSE:
                return true;
        }
        return false;
    }
}