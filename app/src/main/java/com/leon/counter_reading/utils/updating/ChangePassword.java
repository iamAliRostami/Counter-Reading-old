package com.leon.counter_reading.utils.updating;

import static com.leon.counter_reading.enums.DialogType.Yellow;
import static com.leon.counter_reading.enums.ProgressType.SHOW;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.StartActivity;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.tables.PasswordInfo;
import com.leon.counter_reading.tables.SimpleResponse;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomToast;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChangePassword {
    public ChangePassword(Activity activity, String oldPassword, String newPassword, String newPasswordConfirm) {
        final Retrofit retrofit = MyApplication.getApplicationComponent().Retrofit();
        final IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        final Call<SimpleResponse> call = iAbfaService.changePassword(new PasswordInfo(oldPassword,
                newPassword, newPasswordConfirm));
        HttpClientWrapper.callHttpAsync(call, SHOW.getValue(), activity, new Change(activity),
                new ChangeIncomplete(activity), new ChangeError(activity));
    }
}

class Change implements ICallback<SimpleResponse> {
    final Activity activity;

    public Change(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void execute(Response<SimpleResponse> response) {
        if (response.body() != null)
            new CustomToast().success(response.body().message, Toast.LENGTH_LONG);
        final Intent intent = new Intent(activity, StartActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }
}

class ChangeIncomplete implements ICallbackIncomplete<SimpleResponse> {
    final Context context;

    public ChangeIncomplete(Context context) {
        this.context = context;
    }

    @Override
    public void executeIncomplete(Response<SimpleResponse> response) {
        final CustomErrorHandling errorHandling = new CustomErrorHandling(context);
        String error = errorHandling.getErrorMessageDefault(response);
        if (response.code() == 400 && response.errorBody() != null) {
            try {
                JSONObject jObjError = new JSONObject(response.errorBody().string());
                error = jObjError.getString("message");
                new CustomToast().error(error, Toast.LENGTH_LONG);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            new CustomDialogModel(Yellow, context, error, R.string.dear_user, R.string.change_password,
                    R.string.accepted);
    }
}

class ChangeError implements ICallbackError {
    final Context context;

    public ChangeError(Context context) {
        this.context = context;
    }

    @Override
    public void executeError(Throwable t) {
        CustomErrorHandling errorHandling = new CustomErrorHandling(context);
        String error = errorHandling.getErrorMessageTotal(t);
        new CustomDialogModel(Yellow, context, error, R.string.dear_user, R.string.change_password,
                R.string.accepted);
    }
}