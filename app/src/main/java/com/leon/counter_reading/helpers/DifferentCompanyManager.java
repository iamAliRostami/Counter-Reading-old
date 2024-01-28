package com.leon.counter_reading.helpers;


import static com.leon.counter_reading.enums.CompanyNames.ESF;
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
        return switch (getActiveCompanyName()) {
            case TEH_TOTAL -> "http://85.133.245.143/";
            case ESF -> "https://37.191.92.157/";
//                return "http://172.18.12.36";
            case ZONE1 -> "http://217.146.220.33:50012/";
            case ZONE2 -> "http://212.16.75.194:8080/";
            case ZONE3 -> "http://46.209.219.36:90/";
            case ZONE4 -> "http://81.12.106.167:8081/";
            case ZONE5 ->
//                return "http://80.69.252.151/";
                    "http://178.252.151.147/";
            case ZONE6 -> "http://85.133.190.221:4121/";
            case TSW -> "http://81.90.148.25:880/";
            case TE -> "http://185.120.137.254";
            case TSE -> "http://172.28.5.40/";
            case TW -> "http://217.66.195.75/";
            case KSH -> "http://46.225.241.211:25123/";
            default -> "http://192.168.100.8:7529";
        };
    }

    public static String getLocalBaseUrl() {
        return switch (getActiveCompanyName()) {
            case TEH_TOTAL -> "https://85.133.245.143/";
            case ESF -> "http://172.18.12.14:100";
            case ESF_MAP -> "http://172.18.12.242/osm_tiles/";
            case ZONE1 -> "http://172.21.0.16/";
            case ZONE2 -> "http://172.22.4.71/";
            case ZONE3 -> "http://172.23.0.113/";
            case ZONE4 -> "http://172.24.13.23/";
            case ZONE5 -> "http://172.25.0.72/";
            case ZONE6 -> "http://172.26.0.32/";
            case TSW -> "http://172.30.1.22/";
            case TE -> "http://172.31.0.25/";
            case TSE -> "http://172.28.5.40/";
            case TW -> "http://172.28.5.41/";
            case KSH -> "http://46.209.219.36:90";
            default -> "https://192.168.100.8:44321";
        };
    }

    public static String getAhad() {
        if (getActiveCompanyName() == ESF) {
            return "واحد";
        }
        return "آحاد";
    }

    public static String getAhad1() {
        if (getActiveCompanyName() == ESF) {
            return "واحد مسکونی";
        }/* else if (getActiveCompanyName() == TSE) {
            return "آحاد مسکونی";
        }*/
        return "آحاد اصلی";
    }

    public static String getAhad2() {
        if (getActiveCompanyName() == ESF) {
            return "واحد تجاری";
        }/*else if (getActiveCompanyName() == TSE) {
            return "آحاد غیرمسکونی";
        }*/
        return "آحاد فرعی";
    }

    public static String getAhadTotal() {
        if (getActiveCompanyName() == ESF) {
            return "واحد کل";
        }
        return "آحاد آب بها";
    }

    public static int getImageNumber() {
        return switch (getActiveCompanyName()) {
            case ESF, ZONE1, ZONE3, ZONE4, ZONE5, ZONE6, TSE, TW, KSH, TEH_TOTAL -> 4;
            default -> 6;
        };
    }

    public static int getShowError() {
        return switch (getActiveCompanyName()) {
            case ESF, ZONE1, ZONE3, ZONE4, ZONE5, ZONE6, TSE, TW, KSH, TEH_TOTAL -> 3;
            default -> 5;
        };
    }

    public static int getLockNumber() {
        return switch (getActiveCompanyName()) {
            case ESF, ZONE1, ZONE3, ZONE4, ZONE5, ZONE6, TSE, TW, KSH, TEH_TOTAL -> 6;
            default -> 5;
        };
    }

    public static int getEshterakMinLength() {
        return switch (getActiveCompanyName()) {
            case ESF, KSH, ZONE1, ZONE3, ZONE4, ZONE5, ZONE6, TW, TSE, TEH_TOTAL -> 5;
            default -> 10;
        };
    }

    public static int getEshterakMaxLength() {
        return switch (getActiveCompanyName()) {
            case ESF, KSH, ZONE1, ZONE3, ZONE4, ZONE5, ZONE6, TSE, TW, TEH_TOTAL -> 15;
            default -> 10;
        };
    }

    public static String getSecondSearchItem() {
        if (getActiveCompanyName() == ESF) {
            return "ردیف";
        }
        return "شماره پرونده";
    }

    public static String getCompanyName() {
        return switch (getActiveCompanyName()) {
            case ZONE1 -> "آبقا منطقه یک";
            case ZONE2 -> "آبفا منطقه دو";
            case ZONE3 -> "آبفا منطقه سه";
            case ZONE4 -> "آبفا منطقه چهار";
            case ZONE5 -> "آبفا منطقه پنج";
            case ZONE6 -> "آبقا منطقه شش";
            case TE -> "آبفا شرق";
            case TSW -> "آبفا جنوب غربی";
            case TSE -> "آبفا جنوب شرقی";
            case TW -> "آبفا غرب";
            case TEH_TOTAL -> "آبفا استان تهران";
            case KSH -> "آبفا استان کرمانشاه";
            default -> "آبفا استان اصفهان";
        };
    }

    @SuppressLint("SimpleDateFormat")
    public static String getExpireDate(Activity activity) {
        Location location = getLocationTracker(activity).getCurrentLocation();
        Date date = new Date(location != null ? location.getTime() :
                Calendar.getInstance().getTimeInMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch (getActiveCompanyName()) {
            case TEH_TOTAL, ZONE1, ZONE2, ZONE3, ZONE4, ZONE5, ZONE6, TSW, TE, TSE, TW, ESF ->
                    calendar.add(Calendar.DAY_OF_YEAR, -20);
        }
        date = new Date(calendar.getTimeInMillis());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        return replaceNonstandardDigits(dateFormatter.format(date));
    }

    public static boolean gallerySelector() {
        return switch (getActiveCompanyName()) {
            case ZONE1, ZONE2, ZONE4, ZONE5, ZONE6, TSW, TE, TW, ESF -> true;
            default -> false;
        };
    }
}