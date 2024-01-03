package com.leon.counter_reading.utils.updating;

import static com.leon.counter_reading.enums.DialogType.Yellow;
import static com.leon.counter_reading.enums.ProgressType.SHOW_CANCELABLE;

import android.app.Activity;
import android.content.Context;

import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.utils.CustomToast;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GetUpdateFile {
    public GetUpdateFile(Activity activity) {
        Retrofit retrofit = MyApplication.getApplicationComponent().Retrofit();
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<ResponseBody> call = iAbfaService.getLastApk();
        HttpClientWrapper.callHttpAsyncProgressDismiss(call, SHOW_CANCELABLE.getValue(),
                activity, new Update(activity), new UpdateIncomplete(activity), new UpdateError(activity));
    }
}

record Update(Activity activity) implements ICallback<ResponseBody> {

    @Override
    public void execute(Response<ResponseBody> response) {
        if (!CustomFile.writeResponseApkToDisk(response.body(), activity))
            activity.runOnUiThread(() ->
                    new CustomToast().warning(activity.getString(R.string.error_update)));
    }
}

record UpdateIncomplete(Context context) implements ICallbackIncomplete<ResponseBody> {

    @Override
    public void executeIncomplete(Response<ResponseBody> response) {
        CustomErrorHandling errorHandling = new CustomErrorHandling(context);
        String error = errorHandling.getErrorMessageDefault(response);
        new CustomDialogModel(Yellow, context, error, R.string.dear_user, R.string.update,
                R.string.accepted);
    }
}
