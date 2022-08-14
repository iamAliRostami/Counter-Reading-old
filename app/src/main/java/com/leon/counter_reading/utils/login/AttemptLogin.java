package com.leon.counter_reading.utils.login;

import static com.leon.counter_reading.enums.ProgressType.SHOW;
import static com.leon.counter_reading.enums.SharedReferenceKeys.DISPLAY_NAME;
import static com.leon.counter_reading.enums.SharedReferenceKeys.PASSWORD;
import static com.leon.counter_reading.enums.SharedReferenceKeys.PASSWORD_TEMP;
import static com.leon.counter_reading.enums.SharedReferenceKeys.REFRESH_TOKEN;
import static com.leon.counter_reading.enums.SharedReferenceKeys.TOKEN;
import static com.leon.counter_reading.enums.SharedReferenceKeys.USERNAME;
import static com.leon.counter_reading.enums.SharedReferenceKeys.USERNAME_TEMP;
import static com.leon.counter_reading.enums.SharedReferenceKeys.USER_CODE;
import static com.leon.counter_reading.enums.SharedReferenceKeys.XSRF;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.HomeActivity;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.utils.Crypto;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.view_models.LoginViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AttemptLogin extends AsyncTask<Activity, Activity, Void> {
    private final LoginViewModel login;

    public AttemptLogin(LoginViewModel login) {
        super();
        this.login = login;
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        final Retrofit retrofit = getApplicationComponent().NetworkHelperModel().getInstance();
        final IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        final Call<LoginViewModel> call = iAbfaService.login(login);
        activities[0].runOnUiThread(() ->
                HttpClientWrapper.callHttpAsync(call, SHOW.getValue(), activities[0],
                        new LoginCompleted(activities[0], login),
                        new Incomplete(activities[0]), new Error(activities[0])));
        return null;
    }
}

class LoginCompleted implements ICallback<LoginViewModel> {
    private final Activity activity;
    private final LoginViewModel login;

    public LoginCompleted(Activity activity, LoginViewModel login) {
        this.activity = activity;
        this.login = login;
    }

    @Override
    public void execute(Response<LoginViewModel> response) {
        if (login.setLoginFeedback(response.body()) || login.getAccessToken() == null ||
                login.getRefreshToken() == null ||
                login.getAccessToken().length() < 1 ||
                login.getRefreshToken().length() < 1) {
            new CustomToast().warning(activity.getString(R.string.error_is_not_match), Toast.LENGTH_LONG);
        } else {
            final List<String> cookieList = response.headers().values("Set-Cookie");
            login.setXSRFToken((cookieList.get(1).split(";"))[0]);
            final JWT jwt = new JWT(login.getAccessToken());
            login.setDisplayName(jwt.getClaim("DisplayName").asString());
            login.setUserCode(jwt.getClaim("UserCode").asString());
            savePreference();
            final Intent intent = new Intent(activity, HomeActivity.class);
            activity.startActivity(intent);
            activity.finish();
        }
    }

    private void savePreference() {
        final ISharedPreferenceManager sharedPreferenceManager = getApplicationComponent().SharedPreferenceModel();
        sharedPreferenceManager.putData(DISPLAY_NAME.getValue(), login.getDisplayName());
        sharedPreferenceManager.putData(USER_CODE.getValue(), login.getUserCode());
        sharedPreferenceManager.putData(TOKEN.getValue(), login.getAccessToken());
        sharedPreferenceManager.putData(REFRESH_TOKEN.getValue(), login.getRefreshToken());
        sharedPreferenceManager.putData(XSRF.getValue(), login.getXSRFToken());
        sharedPreferenceManager.putData(USERNAME_TEMP.getValue(), login.getUsername());
        sharedPreferenceManager.putData(PASSWORD_TEMP.getValue(), Crypto.encrypt(login.getPassword()));
        if (this.login.isSaved()) {
            sharedPreferenceManager.putData(USERNAME.getValue(), login.getUsername());
            sharedPreferenceManager.putData(PASSWORD.getValue(), Crypto.encrypt(login.getPassword()));
        }
    }
}

