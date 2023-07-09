package com.leon.counter_reading.utils.login;

import static com.leon.counter_reading.enums.ProgressType.NOT_SHOW;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.fragments.LoginFragment;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.view_models.LoginViewModel;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreateDNTCaptcha extends AsyncTask<Activity, Activity, Void> {
    private final LoginFragment fragment;
    public CreateDNTCaptcha(LoginFragment fragment) {
        super();
        this.fragment = fragment;
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        final Retrofit retrofit = getApplicationComponent().NetworkHelperModel().getInstance();
        final IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        final Call<LoginViewModel> call = iAbfaService.createDNTCaptchaParams();
        activities[0].runOnUiThread(() ->
                HttpClientWrapper.callHttpAsync(call, NOT_SHOW.getValue(), activities[0],
                        new CreateDNTCaptchaCompleted(fragment),new CreateCaptchaIncomplete(), new Error(activities[0])));
        return null;
    }
}

class CreateDNTCaptchaCompleted implements ICallback<LoginViewModel> {

    private final LoginFragment fragment;

    CreateDNTCaptchaCompleted(LoginFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void execute(Response<LoginViewModel> response) {
        if (response.body() != null) {
            try {
                fragment.showDNTCaptchaImage(response.body());
            }catch (Exception e){
                new CustomToast().warning(e.getMessage(), Toast.LENGTH_LONG);
            }
        }
    }
}
