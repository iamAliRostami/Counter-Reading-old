package com.leon.counter_reading.utils.login;

import static com.leon.counter_reading.enums.ProgressType.SHOW;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.HomeActivity;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.LoginFeedBack;
import com.leon.counter_reading.tables.LoginInfo;
import com.leon.counter_reading.utils.Crypto;
import com.leon.counter_reading.utils.CustomToast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AttemptLogin extends AsyncTask<Activity, Activity, Void> {
    private final String username;
    private final String password;
    private final String serial;
    private final boolean isChecked;
    private final View view;

    public AttemptLogin(String username, String password, String serial, boolean isChecked, View view) {
        super();
        this.view = view;
        this.username = username;
        this.password = password;
        this.serial = serial;
        this.isChecked = isChecked;
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        final Retrofit retrofit = getApplicationComponent().NetworkHelperModel().getInstance();
        final IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        final Call<LoginFeedBack> call = iAbfaService.login(new LoginInfo(username, password, serial,
                BuildConfig.VERSION_NAME));
        activities[0].runOnUiThread(() ->
                HttpClientWrapper.callHttpAsync(call, SHOW.getValue(), activities[0],
                        new LoginCompleted(activities[0], isChecked, username, password),
                        new Incomplete(activities[0]), new Error(activities[0])));
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

class LoginCompleted implements ICallback<LoginFeedBack> {
    private final Activity activity;
    private final boolean isChecked;
    private final String username;
    private final String password;

    public LoginCompleted(Activity activity, boolean isChecked, String username, String password) {
        this.activity = activity;
        this.isChecked = isChecked;
        this.username = username;
        this.password = password;
    }

    @Override
    public void execute(Response<LoginFeedBack> response) {
        LoginFeedBack loginFeedBack = response.body();
        if (loginFeedBack == null || loginFeedBack.access_token == null ||
                loginFeedBack.refresh_token == null ||
                loginFeedBack.access_token.length() < 1 ||
                loginFeedBack.refresh_token.length() < 1) {
            new CustomToast().warning(activity.getString(R.string.error_is_not_match), Toast.LENGTH_LONG);
        } else {
            List<String> cookieList = response.headers().values("Set-Cookie");
            loginFeedBack.XSRFToken = (cookieList.get(1).split(";"))[0];
            JWT jwt = new JWT(loginFeedBack.access_token);
            loginFeedBack.displayName = jwt.getClaim("DisplayName").asString();
            loginFeedBack.userCode = jwt.getClaim("UserCode").asString();
            savePreference(loginFeedBack, isChecked);
            Intent intent = new Intent(activity, HomeActivity.class);
            activity.startActivity(intent);
            activity.finish();
        }
    }

    private void savePreference(LoginFeedBack loginFeedBack, boolean isChecked) {
        final ISharedPreferenceManager sharedPreferenceManager = getApplicationComponent().SharedPreferenceModel();
        sharedPreferenceManager.putData(SharedReferenceKeys.DISPLAY_NAME.getValue(), loginFeedBack.displayName);
        sharedPreferenceManager.putData(SharedReferenceKeys.USER_CODE.getValue(), loginFeedBack.userCode);
        sharedPreferenceManager.putData(SharedReferenceKeys.TOKEN.getValue(), loginFeedBack.access_token);
        sharedPreferenceManager.putData(SharedReferenceKeys.REFRESH_TOKEN.getValue(), loginFeedBack.refresh_token);
        sharedPreferenceManager.putData(SharedReferenceKeys.XSRF.getValue(), loginFeedBack.XSRFToken);
        sharedPreferenceManager.putData(SharedReferenceKeys.USERNAME_TEMP.getValue(), username);
        sharedPreferenceManager.putData(SharedReferenceKeys.PASSWORD_TEMP.getValue(), Crypto.encrypt(password));
        if (isChecked) {
            sharedPreferenceManager.putData(SharedReferenceKeys.USERNAME.getValue(), username);
            sharedPreferenceManager.putData(SharedReferenceKeys.PASSWORD.getValue(), Crypto.encrypt(password));
        }
    }
}

