package com.leon.counter_reading.helpers;


import static com.leon.counter_reading.enums.CompanyNames.ESF;
import static com.leon.counter_reading.enums.CompanyNames.TE;
import static com.leon.counter_reading.enums.CompanyNames.TW;
import static com.leon.counter_reading.enums.CompanyNames.TSE;
import static com.leon.counter_reading.enums.CompanyNames.TSW;
import static com.leon.counter_reading.enums.CompanyNames.ZONE1;
import static com.leon.counter_reading.enums.CompanyNames.ZONE2;
import static com.leon.counter_reading.enums.CompanyNames.ZONE3;
import static com.leon.counter_reading.enums.CompanyNames.ZONE4;
import static com.leon.counter_reading.enums.CompanyNames.ZONE5;
import static com.leon.counter_reading.enums.CompanyNames.ZONE6;
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

    public static String getBaseUrl(CompanyNames companyNames) {
        if (getApplicationComponent().SharedPreferenceModel().checkIsNotEmpty(PROXY.getValue())) {
            final String proxy = getApplicationComponent().SharedPreferenceModel().getStringData(PROXY.getValue());
            if (proxy.startsWith("http://") && proxy.length() > 7)
                return proxy;
            if (proxy.startsWith("https://") && proxy.length() > 8)
                return proxy;
        }
        switch (companyNames) {
            case ESF:
                return "https://37.191.92.157/";//Internet
//            return "http://172.18.12.36/";
            case ZONE1:
                return "http://217.146.220.33:50011/";
            case ZONE2:
                return "http://212.16.75.194:8080/";
            case ZONE3:
                return "http://212.16.69.36:90/";
            case ZONE4:
                return "http://81.12.106.167:8081/";
            case ZONE5:
                return "http://80.69.252.151/";
            case ZONE6:
                return "http://85.133.190.220:4121/";
            case TSW:
                return "http://81.90.148.25/";
            case TE:
                return "http://185.120.137.254";
            case TSE:
                return "http://46.209.181.2:9098/";
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

    public static String getLocalBaseUrl(CompanyNames companyNames) {
        switch (companyNames) {
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

    public static String getAhad(CompanyNames companyNames) {
        switch (companyNames) {
            case ESF:
                return "واحد";
            case ZONE4:
            case TSE:
            case TW:
            case KSH:
                return "آحاد";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static String getAhad1(CompanyNames companyNames) {
        switch (companyNames) {
            case ESF:
                return "واحد مسکونی";
            case KSH:
            case ZONE4:
            case TW:
            case TSE:
                return "آحاد اصلی";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static String getAhad2(CompanyNames companyNames) {
        switch (companyNames) {
            case ESF:
                return "واحد تجاری";
            case KSH:
            case ZONE4:
            case TW:
            case TSE:
                return "آحاد فرعی";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static String getAhadTotal(CompanyNames companyNames) {
        switch (companyNames) {
            case ESF:
                return "واحد کل";
            case ZONE4:
            case KSH:
            case TW:
            case TSE:
                return "آحاد مصرف";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static int getImageNumber(CompanyNames companyNames) {
        switch (companyNames) {
            case ESF:
            case ZONE4:
            case TSE:
            case TW:
            case KSH:
                return 4;
            default:
                return 6;
        }
    }

    public static int getShowError(CompanyNames companyNames) {
        switch (companyNames) {
            case ESF:
            case ZONE4:
            case TSE:
            case TW:
            case KSH:
                return 3;
            default:
                return 5;
        }
    }

    public static int getLockNumber(CompanyNames companyNames) {
        switch (companyNames) {
            case ESF:
            case ZONE4:
            case TSE:
            case TW:
            case KSH:
                return 6;
            default:
                return 5;
        }
    }

    public static int getEshterakMinLength(CompanyNames companyNames) {
        switch (companyNames) {
            case ESF:
            case KSH:
            case ZONE4:
            case TW:
            case TSE:
                return 5;
            default:
                return 10;
        }
    }

    public static int getEshterakMaxLength(CompanyNames companyNames) {
        switch (companyNames) {
            case ESF:
            case KSH:
            case ZONE4:
                return 15;
            case TSE:
            case TW:
                return 12;
            default:
                return 10;
        }
    }

    public static String getSecondSearchItem(CompanyNames companyNames) {
        switch (companyNames) {
            case ESF:
                return "ردیف";
            case ZONE4:
            case TSE:
            case TW:
            case KSH:
                return "شماره پرونده";
            default:
                return "دیگر";
        }
    }

    public static String getCompanyName(CompanyNames companyName) {
        switch (companyName) {
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
    public static String getExpireDate(CompanyNames companyNames, Activity activity) {
        final Location location = getLocationTracker(activity).getCurrentLocation();
        Date date = new Date(location != null ? location.getTime() :
                Calendar.getInstance().getTimeInMillis());
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch (companyNames) {
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
}