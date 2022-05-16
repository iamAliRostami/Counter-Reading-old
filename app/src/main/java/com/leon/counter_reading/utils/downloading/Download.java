package com.leon.counter_reading.utils.downloading;

import static com.leon.counter_reading.enums.ProgressType.SHOW;
import static com.leon.counter_reading.helpers.MyApplication.getContext;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.tables.ReadingData;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomToast;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Download extends AsyncTask<Activity, Void, Void> {

    public Download() {
        super();
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        final Retrofit retrofit = MyApplication.getApplicationComponent().Retrofit();
        final IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        final Call<ReadingData> call = iAbfaService.loadData(BuildConfig.VERSION_CODE);
        activities[0].runOnUiThread(() ->
                HttpClientWrapper.callHttpAsync(call, SHOW.getValue(), activities[0],
                        new DownloadCompleted(activities[0]), new DownloadIncomplete(), new DownloadError()));
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
//        throw new RuntimeException("Test Force Crash"); // Force a crash

    }
}

class DownloadCompleted implements ICallback<ReadingData> {
    final Activity activity;

    public DownloadCompleted(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void execute(Response<ReadingData> response) {
        if (response != null && response.body() != null) {
            final ReadingData readingData = response.body();
            final ReadingData readingDataTemp = response.body();
            new SaveDownloadData().savedData(activity, readingData, readingDataTemp);
        }
    }
}

class DownloadIncomplete implements ICallbackIncomplete<ReadingData> {
    @Override
    public void executeIncomplete(Response<ReadingData> response) {
        final CustomErrorHandling errorHandling = new CustomErrorHandling(getContext());
        String error = errorHandling.getErrorMessageDefault(response);
        if (response.code() == 400) {
            CustomErrorHandling.APIError apiError = errorHandling.parseError(response);
            error = apiError.message();
        }
        new CustomToast().warning(error, Toast.LENGTH_LONG);
    }
}

class DownloadError implements ICallbackError {
    @Override
    public void executeError(Throwable t) {
        final CustomErrorHandling errorHandling = new CustomErrorHandling(getContext());
        new CustomToast().error(errorHandling.getErrorMessageTotal(t), Toast.LENGTH_LONG);
    }
}