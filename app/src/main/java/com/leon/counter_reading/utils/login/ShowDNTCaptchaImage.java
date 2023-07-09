package com.leon.counter_reading.utils.login;

import static com.leon.counter_reading.enums.ProgressType.NOT_SHOW;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getContext;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.fragments.LoginFragment;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.view_models.LoginViewModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ShowDNTCaptchaImage extends AsyncTask<Activity, Activity, Void> {
    private final LoginFragment fragment;
    private final LoginViewModel login;

    public ShowDNTCaptchaImage(LoginFragment fragment, LoginViewModel login) {
        super();
        this.fragment = fragment;
        this.login = login;
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        final Retrofit retrofit = getApplicationComponent().NetworkHelperModel().getInstance();
        final IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        final Call<ResponseBody> call = iAbfaService.showDNTCaptchaImage(login.getData(),"bmp");
        activities[0].runOnUiThread(() ->
                HttpClientWrapper.callHttpAsync(call, NOT_SHOW.getValue(), activities[0],
                        new ShowDNTCaptchaImageCompleted(fragment),
                        new ShowCaptchaIncomplete(), new Error(activities[0])));
        return null;
    }
}

class ShowDNTCaptchaImageCompleted implements ICallback<ResponseBody> {

    private final LoginFragment fragment;

    ShowDNTCaptchaImageCompleted(LoginFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void execute(Response<ResponseBody> response) {
        if (response.body() != null) {
            fragment.setCaptchaResult(BitmapFactory.decodeStream(response.body().byteStream()));
        }
    }
}
