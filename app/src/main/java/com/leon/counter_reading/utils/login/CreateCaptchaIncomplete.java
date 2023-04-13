package com.leon.counter_reading.utils.login;

import static com.leon.counter_reading.helpers.MyApplication.getContext;

import android.widget.Toast;

import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.view_models.LoginViewModel;

import retrofit2.Response;

public class CreateCaptchaIncomplete implements ICallbackIncomplete<LoginViewModel> {
    @Override
    public void executeIncomplete(Response<LoginViewModel> response) {
        final CustomErrorHandling errorHandling = new CustomErrorHandling(getContext());
        final String error = errorHandling.getErrorMessageDefault(response);
        new CustomToast().warning(error, Toast.LENGTH_LONG);
    }
}
