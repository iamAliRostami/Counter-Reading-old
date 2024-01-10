package com.leon.counter_reading.utils.login;

import static com.leon.counter_reading.helpers.MyApplication.getContext;

import android.widget.Toast;

import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomToast;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class ShowCaptchaIncomplete implements ICallbackIncomplete<ResponseBody> {
    @Override
    public void executeIncomplete(Response<ResponseBody> response) {
        final CustomErrorHandling errorHandling = new CustomErrorHandling(getContext());
        final String error = errorHandling.getErrorMessageDefault(response);
        new CustomToast().warning(error, Toast.LENGTH_LONG);
    }
}
