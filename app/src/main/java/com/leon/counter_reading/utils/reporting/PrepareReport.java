package com.leon.counter_reading.utils.reporting;

import static com.leon.counter_reading.enums.ProgressType.SHOW;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getContext;
import static com.leon.counter_reading.utils.Converters.bitmapToFile;
import static com.leon.counter_reading.utils.CustomFile.loadImage;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.fragments.report.ReportForbidsFragment;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.tables.ForbiddenDto;
import com.leon.counter_reading.tables.ForbiddenDtoMultiple;
import com.leon.counter_reading.tables.ForbiddenDtoRequestMultiple;
import com.leon.counter_reading.tables.ForbiddenDtoResponses;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomToast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PrepareReport extends AsyncTask<Activity, Activity, Activity> {
    private final ArrayList<ForbiddenDto> forbiddenDtos = new ArrayList<>();
    private final CustomProgressModel progress;
    private final ReportForbidsFragment fragment;

    public PrepareReport(Activity activity, ReportForbidsFragment fragment) {
        super();
        progress = getApplicationComponent().CustomProgressModel();
        progress.show(activity, false);
        this.fragment = fragment;
    }

    @Override
    protected Activity doInBackground(Activity... activities) {
        forbiddenDtos.clear();
        forbiddenDtos.addAll(getApplicationComponent().MyDatabase().forbiddenDao()
                .getAllForbiddenDto(false));
        return activities[0];
    }

    @Override
    protected void onPostExecute(Activity activity) {
        super.onPostExecute(activity);
        progress.getDialog().dismiss();
        if (forbiddenDtos.size() > 0) uploadForbid(activity);
        else new CustomToast().error(activity.getString(R.string.data_not_found));
        fragment.setButtonState();
    }

    private void uploadForbid(Activity activity) {
        final ForbiddenDtoRequestMultiple forbiddenDtoRequestMultiple = new ForbiddenDtoRequestMultiple();
        final Retrofit retrofit = getApplicationComponent().Retrofit();
        final IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        for (ForbiddenDto forbiddenDto : forbiddenDtos) {
            final ForbiddenDtoMultiple forbiddenDtoMultiple =
                    new ForbiddenDtoMultiple(forbiddenDto.zoneId, forbiddenDto.description,
                            forbiddenDto.preEshterak, forbiddenDto.nextEshterak,
                            forbiddenDto.postalCode, forbiddenDto.tedadVahed, forbiddenDto.x,
                            forbiddenDto.y, forbiddenDto.gisAccuracy);
            if (forbiddenDto.address != null)
                forbiddenDtoMultiple.File = bitmapToFile(loadImage(activity, forbiddenDto.address), activity);
            forbiddenDtoRequestMultiple.forbiddenDtos.add(forbiddenDtoMultiple);
        }
        final Call<ForbiddenDtoResponses> call = iAbfaService.multipleForbidden(forbiddenDtoRequestMultiple);
        HttpClientWrapper.callHttpAsync(call, SHOW.getValue(), activity, new Forbidden(fragment),
                new ForbiddenIncomplete(activity), new ForbiddenError(activity));
    }

}

class Forbidden implements ICallback<ForbiddenDtoResponses> {
    private final ReportForbidsFragment fragment;

    Forbidden(ReportForbidsFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void execute(Response<ForbiddenDtoResponses> response) {
        if (response.isSuccessful()) {
            getApplicationComponent().MyDatabase().forbiddenDao().updateAllForbiddenDtoBySent(true);
            fragment.setReportForbid();
            if (response.body() != null) {
                new CustomToast().success(getContext().
                                getString(R.string.report_forbid) + "\n" + response.body().message,
                        Toast.LENGTH_LONG);
            }
        }
    }
}

class ForbiddenIncomplete implements ICallbackIncomplete<ForbiddenDtoResponses> {
    private final Context context;

    ForbiddenIncomplete(Context context) {
        this.context = context;
    }

    @Override
    public void executeIncomplete(Response<ForbiddenDtoResponses> response) {
        final CustomErrorHandling errorHandling = new CustomErrorHandling(context);
        final String error = errorHandling.getErrorMessageDefault(response);
        new CustomToast().warning(error, Toast.LENGTH_LONG);
    }
}

class ForbiddenError implements ICallbackError {
    private final Context context;

    ForbiddenError(Context context) {
        this.context = context;
    }

    @Override
    public void executeError(Throwable t) {
        final CustomErrorHandling errorHandling = new CustomErrorHandling(context);
        new CustomToast().error(errorHandling.getErrorMessageTotal(t), Toast.LENGTH_LONG);
    }
}