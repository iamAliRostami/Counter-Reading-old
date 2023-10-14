package com.leon.counter_reading.utils.login;

import android.widget.Toast;

import com.leon.counter_reading.R;
import com.leon.counter_reading.fragments.LoginFragment;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.view_models.LoginViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Response;

public class Incomplete implements ICallbackIncomplete<LoginViewModel> {
    private final LoginFragment fragment;

    public Incomplete(LoginFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void executeIncomplete(Response<LoginViewModel> response) {
        String error;
        try {
            CustomErrorHandling errorHandling = new CustomErrorHandling(fragment.requireContext());
            error = errorHandling.getErrorMessageDefault(response);
            if (response.code() == 401 || response.code() == 400) {
                error = fragment.getString(R.string.error_is_not_match);
            } else if (response.code() == 428 || response.code() == 451) {
                error = errorHandling.getErrorMessageDefault(response);
            }
            if (response.code() == 401 || response.code() == 400 || response.code() == 428) {
                if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        error = jObjError.getString("message");
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (response.code() == 400) {
                fragment.resetAttempt();
            }
        } catch (Exception e) {
            error = e.getMessage();
        }
        new CustomToast().warning(error, Toast.LENGTH_LONG);
    }
}
