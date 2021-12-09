package com.leon.counter_reading.utils.login;

import static com.leon.counter_reading.di.view_model.HttpClientWrapper.callHttpAsync;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;

import com.leon.counter_reading.activities.LoginActivity;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.tables.LoginFeedBack;
import com.leon.counter_reading.tables.LoginInfo;
import com.leon.counter_reading.utils.CustomToast;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AttemptRegister extends AsyncTask<Activity, Activity, Void> {
    private final String username;
    private final String password;
    private final String serial;
    private final View view;

    public AttemptRegister(String username, String password, String serial, View view) {
        super();
        this.view = view;
        this.username = username;
        this.password = password;
        this.serial = serial;
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        Retrofit retrofit = getApplicationComponent().NetworkHelperModel().getInstance();
        final IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        final Call<LoginFeedBack> call = iAbfaService.register(new LoginInfo(username, password, serial));
        activities[0].runOnUiThread(() ->
                callHttpAsync(call, ProgressType.SHOW.getValue(), activities[0],
                        new RegisterCompleted(), new Incomplete(activities[0]),
                        new Error(activities[0])));
        return null;
    }

    @Override
    protected void onPreExecute() {
        if (view != null)
            view.setEnabled(false);
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        if (view != null)
            view.setEnabled(true);
    }
}

class RegisterCompleted implements ICallback<LoginFeedBack> {

    @Override
    public void execute(Response<LoginFeedBack> response) {
        if (response.body() != null) {
            new CustomToast().success(response.body().message);
        }
    }
}

