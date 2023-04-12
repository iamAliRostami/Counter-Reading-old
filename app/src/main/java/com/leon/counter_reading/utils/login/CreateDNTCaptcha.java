package com.leon.counter_reading.utils.login;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.view_models.LoginViewModel;

import retrofit2.Response;

public class CreateDNTCaptcha extends AsyncTask<Activity, Activity, Void> {

    @Override
    protected Void doInBackground(Activity... activities) {
        return null;
    }
}

class CreateDNTCaptchaCompleted implements ICallback<LoginViewModel> {

    @Override
    public void execute(Response<LoginViewModel> response) {

    }
}
