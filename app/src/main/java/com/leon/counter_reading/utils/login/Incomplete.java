package com.leon.counter_reading.utils.login;

import android.app.Activity;
import android.widget.Toast;

import com.leon.counter_reading.R;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.view_models.LoginViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Response;

class Incomplete implements ICallbackIncomplete<LoginViewModel> {
    private final Activity activity;

    public Incomplete(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void executeIncomplete(Response<LoginViewModel> response) {
        final CustomErrorHandling errorHandling = new CustomErrorHandling(activity);
        String error = errorHandling.getErrorMessageDefault(response);
        if (response.code() == 401 || response.code() == 400) {
            error = activity.getString(R.string.error_is_not_match);
            if (response.errorBody() != null) {
                try {
                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                    error = jObjError.getString("message");
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
        new CustomToast().warning(error, Toast.LENGTH_LONG);
    }
}
