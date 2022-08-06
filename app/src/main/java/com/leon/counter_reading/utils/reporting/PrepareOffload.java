package com.leon.counter_reading.utils.reporting;

import static com.leon.counter_reading.enums.DialogType.Red;
import static com.leon.counter_reading.enums.ProgressType.SHOW;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getContext;
import static com.leon.counter_reading.helpers.MyApplication.getErrorCounter;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getActiveCompanyName;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getShowError;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.fragments.report.ReportInspectionFragment;
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

public class PrepareOffload extends AsyncTask<Activity, Activity, Activity> {
    private final OnOffLoadDto.OffLoadData offLoadData;
    private final CustomProgressModel progress;
    private final ReportInspectionFragment fragment;

    public PrepareOffload(Activity activity, ReportInspectionFragment fragment) {
        super();
        progress = getApplicationComponent().CustomProgressModel();
        progress.show(activity, false);
        offLoadData = new OnOffLoadDto.OffLoadData();
        this.fragment = fragment;
    }

    @Override
    protected Activity doInBackground(Activity... activities) {
        offLoadData.isFinal = false;
        offLoadData.offLoadReports.addAll(getApplicationComponent().MyDatabase().offLoadReportDao().
                getAllOffLoadReportByActive(true, false));
        return activities[0];
    }

    @Override
    protected void onPostExecute(Activity activity) {
        super.onPostExecute(activity);
        progress.getDialog().dismiss();
        if (offLoadData.offLoadReports.size() > 0) uploadOffload(activity);
        else new CustomToast().error(activity.getString(R.string.data_not_found));
        fragment.setButtonState();
    }

    private void uploadOffload(Activity activity) {
        final Retrofit retrofit = getApplicationComponent().Retrofit();
        final IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        final Call<OnOffLoadDto.OffLoadResponses> call = iAbfaService.ReportOnly(offLoadData);
        HttpClientWrapper.callHttpAsync(call, SHOW.getValue(), activity,
                new offLoadData(activity, fragment), new offLoadDataIncomplete(activity), new offLoadError(activity));
    }
}

class offLoadData implements ICallback<OnOffLoadDto.OffLoadResponses> {
    private final Activity activity;
    private final ReportInspectionFragment fragment;

    public offLoadData(Activity activity, ReportInspectionFragment fragment) {
        this.activity = activity;
        this.fragment = fragment;
    }

    @Override
    public void execute(Response<OnOffLoadDto.OffLoadResponses> response) {
        if (response.body() != null && response.body().status == 200) {
            getApplicationComponent().MyDatabase().offLoadReportDao().updateOffLoadReportByIsSent(true);
            new CustomToast().success(response.body().message, Toast.LENGTH_LONG);
            fragment.setReportInspection();
        } else if (response.body() != null) {
            try {
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
                final CustomErrorHandling.APIError apiError = errorHandling.parseError(response);
                final String error = apiError.message();
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