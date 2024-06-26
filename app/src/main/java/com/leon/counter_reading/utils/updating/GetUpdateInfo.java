package com.leon.counter_reading.utils.updating;

import static com.leon.counter_reading.enums.DialogType.Yellow;
import static com.leon.counter_reading.enums.ProgressType.SHOW;

import android.app.Activity;
import android.content.Context;

import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.fragments.setting.SettingUpdateFragment;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.tables.LastInfo;
import com.leon.counter_reading.utils.CustomErrorHandling;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GetUpdateInfo {

    public GetUpdateInfo(Activity activity, SettingUpdateFragment settingUpdateFragment) {
        Retrofit retrofit = MyApplication.getApplicationComponent().Retrofit();
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<LastInfo> call = iAbfaService.getLastInfo();
        HttpClientWrapper.callHttpAsync(call, SHOW.getValue(), activity,
                new UpdateInfo(settingUpdateFragment), new UpdateInfoIncomplete(activity), new UpdateError(activity));
    }
}

record UpdateInfo(SettingUpdateFragment settingUpdateFragment) implements ICallback<LastInfo> {

    @Override
    public void execute(Response<LastInfo> response) {
        if (response.body() != null) {
            settingUpdateFragment.updateInfoUi(response.body());
        }
    }
}

record UpdateInfoIncomplete(Context context) implements ICallbackIncomplete<LastInfo> {

    @Override
    public void executeIncomplete(Response<LastInfo> response) {
        CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(context);
        String error = customErrorHandlingNew.getErrorMessageDefault(response);
        new CustomDialogModel(Yellow, context, error, R.string.dear_user, R.string.update,
                R.string.accepted);
    }
}