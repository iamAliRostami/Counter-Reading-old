package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.activities.ReadingActivity.offlineAttempts;
import static com.leon.counter_reading.enums.DialogType.Red;
import static com.leon.counter_reading.enums.OffloadStateEnum.INSERTED;
import static com.leon.counter_reading.helpers.Constants.OFFLINE_ATTEMPT;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getContext;
import static com.leon.counter_reading.helpers.MyApplication.setErrorCounter;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.DifferentCompanyManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PrepareToSend extends AsyncTask<Activity, Integer, Integer> {
    private final OnOffLoadDto.OffLoadData offLoadData;
    private final String token;

    public PrepareToSend(String token) {
        super();
        this.token = token;
        offLoadData = new OnOffLoadDto.OffLoadData();
    }

    @Override
    protected Integer doInBackground(Activity... activities) {
        offLoadData.isFinal = false;
        offLoadData.offLoads = new ArrayList<>(getApplicationComponent().MyDatabase().
                onOffLoadDao().getAllOnOffLoadInsert(INSERTED.getValue(), true));
        offLoadData.offLoadReports.addAll(getApplicationComponent().MyDatabase().offLoadReportDao().
                getAllOffLoadReportByActive(true, false));
        Retrofit retrofit = getApplicationComponent().NetworkHelperModel().getInstance(2, token);
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<OnOffLoadDto.OffLoadResponses> call = iAbfaService.OffLoadData(offLoadData);
        try {
            if (HttpClientWrapper.call != null)
                HttpClientWrapper.call.cancel();
        } catch (Exception e) {
            activities[0].runOnUiThread(() -> new CustomDialogModel(Red,
                    activities[0], e.getMessage(),
                    activities[0].getString(R.string.dear_user),
                    activities[0].getString(R.string.take_screen_shot),
                    activities[0].getString(R.string.accepted)));
        }
        activities[0].runOnUiThread(() ->
                HttpClientWrapper.callHttpAsync(call, ProgressType.NOT_SHOW.getValue(), activities[0],
                        new offLoadData(activities[0]),
                        new offLoadDataIncomplete(activities[0]), new offLoadError(activities[0])));
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
                setErrorCounter(MyApplication.getErrorCounter() + 1);
                CustomErrorHandling errorHandling = new CustomErrorHandling(activity);
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
        if (MyApplication.getErrorCounter() <
                DifferentCompanyManager.getShowError(DifferentCompanyManager.getActiveCompanyName())) {
            try {
                CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(getContext());
                String error = customErrorHandlingNew.getErrorMessageTotal(t);
                new CustomToast().error(error);
            } catch (Exception e) {
                activity.runOnUiThread(() -> new CustomDialogModel(Red,
                        activity, e.getMessage(),
                        activity.getString(R.string.dear_user),
                        activity.getString(R.string.take_screen_shot),
                        activity.getString(R.string.accepted)));
            }
        }
        setErrorCounter(MyApplication.getErrorCounter() + 1);
        offlineAttempts++;
        if (offlineAttempts == OFFLINE_ATTEMPT) {
            activity.runOnUiThread(() -> new CustomToast().warning("???????? ???????????? ???????? ??????????."));
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
                CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(getContext());
                String error = customErrorHandlingNew.getErrorMessageDefault(response);
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