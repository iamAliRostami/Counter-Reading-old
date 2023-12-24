package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.enums.DialogType.Red;
import static com.leon.counter_reading.enums.FragmentTags.RESET;
import static com.leon.counter_reading.enums.OffloadStateEnum.INSERTED;
import static com.leon.counter_reading.enums.ProgressType.NOT_SHOW;
import static com.leon.counter_reading.fragments.dialog.ShowFragmentDialog.ShowDialogOnce;
import static com.leon.counter_reading.helpers.Constants.MAX_OFFLINE_ATTEMPT;
import static com.leon.counter_reading.helpers.Constants.currentOfflineAttempts;
import static com.leon.counter_reading.helpers.Constants.publicErrorCounter;
import static com.leon.counter_reading.helpers.Constants.unauthorisedAttempts;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getShowError;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getContext;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.fragments.dialog.ResetApplicationFragment;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomToast;

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
        offLoadData.offLoads.addAll(getApplicationComponent().MyDatabase().
                onOffLoadDao().getAllOnOffLoadInsert(INSERTED.getValue(), true));
        offLoadData.offLoadReports.addAll(getApplicationComponent().MyDatabase().offLoadReportDao().
                getAllOffLoadReportByActive(true, false));
        final Retrofit retrofit = getApplicationComponent().NetworkHelperModel().getInstance(2, token);
        final IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        final Call<OnOffLoadDto.OffLoadResponses> call = iAbfaService.OffLoadData(offLoadData);
        try {
            if (HttpClientWrapper.call != null)
                HttpClientWrapper.call.cancel();
        } catch (Exception e) {
            activities[0].runOnUiThread(() -> new CustomDialogModel(Red, activities[0], e.getMessage(),
                    activities[0].getString(R.string.dear_user),
                    activities[0].getString(R.string.take_screen_shot),
                    activities[0].getString(R.string.accepted)));
        }
        activities[0].runOnUiThread(() ->
                HttpClientWrapper.callHttpAsync(call, NOT_SHOW.getValue(), activities[0],
                        new offLoadData(activities[0]),
                        new offLoadDataIncomplete(activities[0]), new offLoadError(activities[0])));
        return null;
    }

}

record offLoadData(Activity activity) implements ICallback<OnOffLoadDto.OffLoadResponses> {

    @Override
    public void execute(Response<OnOffLoadDto.OffLoadResponses> response) {
        if (response.body() != null && response.body().status == 200) {
            new Sent(response.body()).execute(activity);
        } else if (response.body() != null) {
            try {
                publicErrorCounter = publicErrorCounter + 1;
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


record offLoadDataIncomplete(Activity activity)
        implements ICallbackIncomplete<OnOffLoadDto.OffLoadResponses> {

    @Override
    public void executeIncomplete(Response<OnOffLoadDto.OffLoadResponses> response) {
        if (response != null) {
            try {
                CustomErrorHandling errorHandling = new CustomErrorHandling(getContext());
                String error = errorHandling.getErrorMessageDefault(response);
                if (response.code() == 401 || response.code() == 403) {
                    if (unauthorisedAttempts < getShowError())
                        unauthorisedAttempts = unauthorisedAttempts + 1;
                    else {
                        ShowDialogOnce(activity, RESET.getValue(), ResetApplicationFragment.newInstance());
                    }
                } else if (response.code() == 400) {
                    CustomErrorHandling.APIError apiError = errorHandling.parseError(response);
                    error = apiError.message();
                }
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

record offLoadError(Activity activity) implements ICallbackError {

    @Override
    public void executeError(Throwable t) {
        if (publicErrorCounter < getShowError()) {
            try {
                String error = new CustomErrorHandling(getContext()).getErrorMessageTotal(t);
                new CustomToast().error(error);
            } catch (Exception e) {
                activity.runOnUiThread(() -> new CustomDialogModel(Red,
                        activity, e.getMessage(),
                        activity.getString(R.string.dear_user),
                        activity.getString(R.string.take_screen_shot),
                        activity.getString(R.string.accepted)));
            }
        }
        publicErrorCounter = publicErrorCounter + 1;
        currentOfflineAttempts++;
        if (currentOfflineAttempts == MAX_OFFLINE_ATTEMPT) {
            activity.runOnUiThread(() -> new CustomToast().warning("حالت آفلاین فعال گردید"));
        }
    }
}