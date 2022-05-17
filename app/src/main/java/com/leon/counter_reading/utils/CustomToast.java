package com.leon.counter_reading.utils;

import static com.leon.counter_reading.helpers.MyApplication.getContext;

import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class CustomToast {
    public void error(String s) {
        error(s, Toast.LENGTH_SHORT);
    }

    public void error(String s, int duration) {
        Toasty.error(getContext(), s, duration, true).show();
    }

    public void success(String s) {
        success(s, Toast.LENGTH_SHORT);
    }

    public void success(String s, int duration) {
        Toasty.success(getContext(), s, duration, true).show();
    }

    public void info(String s) {
        info(s, Toast.LENGTH_SHORT);
    }

    public void info(String s, int duration) {
        Toasty.info(getContext(), s, duration, true).show();
    }

    public void warning(String s) {
        warning(s, Toast.LENGTH_SHORT);
    }

    public void warning(String s, int duration) {
        Toasty.warning(getContext(), s, duration, true).show();
    }

    public void normal(String s, int drawable) {
        Toasty.normal(getContext(), s, drawable).show();
    }

    public void custom(String s, int drawable, int duration, int tintColor,
                       boolean withIcon, boolean shouldTint) {
        Toasty.custom(getContext(), s, drawable, tintColor, duration, withIcon,
                shouldTint).show();
    }
}
