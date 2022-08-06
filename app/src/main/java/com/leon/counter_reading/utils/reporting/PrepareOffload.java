package com.leon.counter_reading.utils.reporting;

import static com.leon.counter_reading.enums.DialogType.Red;
import static com.leon.counter_reading.enums.OffloadStateEnum.INSERTED;
import static com.leon.counter_reading.enums.ProgressType.SHOW;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getContext;
import static com.leon.counter_reading.helpers.MyApplication.getErrorCounter;
import static com.leon.counter_reading.helpers.MyApplication.setErrorCounter;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getActiveCompanyName;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getShowError;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.reading.Sent;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PrepareOffload extends AsyncTask<Activity, Integer, Integer> {
    private final OnOffLoadDto.OffLoadData offLoadData;
    private final CustomProgressModel progress;
    public PrepareOffload(Activity activity) {
        super();
        progress = getApplicationComponent().CustomProgressModel();
        progress.show(activity, false);
        offLoadData = new OnOffLoadDto.OffLoadData();
    }

    @Override
    protected Integer doInBackground(Activity... activities) {
        offLoadData.isFinal = false;
        offLoadData.offLoads.addAll(getApplicationComponent().MyDatabase().
                onOffLoadDao().getAllOnOffLoadInsert(INSERTED.getValue(), true));
        offLoadData.offLoadReports.addAll(getApplicationComponent().MyDatabase().offLoadReportDao().
                getAllOffLoadReportByActive(true, false));
        final Retrofit retrofit = getApplicationComponent().Retrofit();
        final IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        final Call<OnOffLoadDto.OffLoadResponses> call = iAbfaService.OffLoadData(offLoadData);
//        activities[0].runOnUiThread(() ->);
        HttpClientWrapper.callHttpAsync(call, SHOW.getValue(), activities[0], new offLoadData(activities[0]),
                new offLoadDataIncomplete(activities[0]), new offLoadError(activities[0]));
        return null;
    }

}

class offLoadData implements ICallback<OnOffLoadDto.OffLoadResponses> {
    private final Activity activity;

    public offLoadData(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void execute(Response<OnOffLoadDto.OffLoadResponses> response) {
        if (response.body() != null && response.body().status == 200) {
            new Sent(response.body()).execute(activity);
        } else if (response.body() != null) {
            try {
                setErrorCounter(getErrorCounter() + 1);
                final CustomErrorHandling errorHandling = new CustomErrorHandling(activity);
                final String error = errorHandling.getErrorMessage(response.body().status);
                new CustomToast().error(error);
            } catch (Exception e) {
                activity.runOnUiThread(() -> new CustomDialogModel(Red,
                        activity, e.getMessage(),
                        activity.getString(R.string.dear_user),
                        activity.getString(R.string.take_screen_shot),
                        activity.getString(R.string.accepted)));
            }
        }
    }
}

class offLoadError implements ICallbackError {
    final Activity activity;

    public offLoadError(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void executeError(Throwable t) {
        if (getErrorCounter() < getShowError(getActiveCompanyName())) {
            try {
                final CustomErrorHandling errorHandling = new CustomErrorHandling(getContext());
                final String error = errorHandling.getErrorMessageTotal(t);
                new CustomToast().error(error);
            } catch (Exception e) {
                activity.runOnUiThread(() -> new CustomDialogModel(Red,
                        activity, e.getMessage(),
                        activity.getString(R.string.dear_user),
                        activity.getString(R.string.take_screen_shot),
                        activity.getString(R.string.accepted)));
            }
        }
    }
}

class offLoadDataIncomplete implements ICallbackIncomplete<OnOffLoadDto.OffLoadResponses> {
    final Activity activity;

    public offLoadDataIncomplete(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void executeIncomplete(Response<OnOffLoadDto.OffLoadResponses> response) {
        if (response != null) {
            try {
                final CustomErrorHandling errorHandling = new CustomErrorHandling(getContext());
                final String error = errorHandling.getErrorMessageDefault(response);
                new CustomToast().error(error);
            } catch (Exception e) {
                activity.runOnUiThread(() -> new CustomDialogModel(Red, activity, e.getMessage(),
                        activity.getString(R.string.dear_user),
                        activity.getString(R.string.take_screen_shot),
                        activity.getString(R.string.accepted)));
            }
        }
    }
}