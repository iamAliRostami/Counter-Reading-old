package com.leon.counter_reading.utils.performance;

import static com.leon.counter_reading.di.view_model.HttpClientWrapper.callHttpAsync;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.fragments.report.ReportPerformanceFragment;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.view_models.PerformanceViewModel;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GetPerformance extends AsyncTask<Activity, Activity, Activity> {
    private final CustomProgressModel customProgressModel;
    private final ReportPerformanceFragment fragment;
    private final PerformanceViewModel performanceVM;

    public GetPerformance(Activity activity, ReportPerformanceFragment fragment, PerformanceViewModel performanceVM) {
        super();
        customProgressModel = getApplicationComponent().CustomProgressModel();
        customProgressModel.show(activity, false);
        this.performanceVM = performanceVM;
        this.fragment = fragment;
    }

    @Override
    protected Activity doInBackground(Activity... activities) {
        final Retrofit retrofit = getApplicationComponent().NetworkHelperModel()
                .getInstance(true, getApplicationComponent().SharedPreferenceModel()
                        .getStringData(SharedReferenceKeys.TOKEN.getValue()), 10, 5, 5);

        final IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        final Call<PerformanceViewModel> call = iAbfaService.myPerformance(performanceVM);
        activities[0].runOnUiThread(() -> {
            customProgressModel.getDialog().dismiss();
            callHttpAsync(call, ProgressType.SHOW.getValue(), activities[0],
                    new Performance(fragment), new PerformanceIncomplete(activities[0]),
                    new PerformanceError(activities[0]));
        });
        return activities[0];
    }

}

class PerformanceError implements ICallbackError {
    private final Activity activity;

    public PerformanceError(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void executeError(Throwable t) {
        final CustomErrorHandling errorHandling = new CustomErrorHandling(activity);
        new CustomToast().error(errorHandling.getErrorMessageTotal(t), Toast.LENGTH_LONG);
    }
}

class PerformanceIncomplete implements ICallbackIncomplete<PerformanceViewModel> {
    private final Activity activity;

    public PerformanceIncomplete(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void executeIncomplete(Response<PerformanceViewModel> response) {
        final CustomErrorHandling errorHandling = new CustomErrorHandling(activity);
        new CustomToast().warning(errorHandling.getErrorMessageDefault(response), Toast.LENGTH_LONG);
    }
}

class Performance implements ICallback<PerformanceViewModel> {
    private final ReportPerformanceFragment fragment;

    public Performance(ReportPerformanceFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void execute(Response<PerformanceViewModel> response) {
        fragment.setTextViewTextSetter(response.body());
    }
}
