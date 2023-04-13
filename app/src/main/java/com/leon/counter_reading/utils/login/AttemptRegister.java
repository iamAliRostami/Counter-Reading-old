package com.leon.counter_reading.utils.login;

import static com.leon.counter_reading.di.view_model.HttpClientWrapper.callHttpAsync;
import static com.leon.counter_reading.enums.ProgressType.SHOW;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.fragments.LoginFragment;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.view_models.LoginViewModel;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AttemptRegister extends AsyncTask<Activity, Activity, Void> {
    private final LoginFragment fragment;


    public AttemptRegister(LoginFragment fragment) {
        super();
        this.fragment = fragment;
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        Retrofit retrofit = getApplicationComponent().NetworkHelperModel().getInstance();
        final IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        final Call<LoginViewModel> call = iAbfaService.register(fragment.getLogin());
        activities[0].runOnUiThread(() ->
                callHttpAsync(call, SHOW.getValue(), activities[0], new RegisterCompleted(),
                        new Incomplete(fragment), new Error(activities[0])));
        return null;
    }
}

class RegisterCompleted implements ICallback<LoginViewModel> {

    @Override
    public void execute(Response<LoginViewModel> response) {
        if (response.body() != null) {
            new CustomToast().success(response.body().getMessage());
        }
    }
}

