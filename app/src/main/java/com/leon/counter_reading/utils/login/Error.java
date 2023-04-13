package com.leon.counter_reading.utils.login;

import android.app.Activity;
import android.widget.Toast;

import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomToast;

class Error implements ICallbackError {
    private final Activity activity;

    public Error(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void executeError(Throwable t) {
        final CustomErrorHandling errorHandling = new CustomErrorHandling(activity);
        final String error = errorHandling.getErrorMessageTotal(t);
        new CustomToast().error(error, Toast.LENGTH_LONG);
    }
}
