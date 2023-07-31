package com.leon.counter_reading.utils.uploading;

import static com.leon.counter_reading.enums.OffloadStateEnum.INSERTED;
import static com.leon.counter_reading.enums.OffloadStateEnum.SENT;
import static com.leon.counter_reading.enums.OffloadStateEnum.SENT_WITH_ERROR;
import static com.leon.counter_reading.enums.ProgressType.SHOW;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getContext;
import static com.leon.counter_reading.utils.CalendarTool.getDate;
import static com.leon.counter_reading.utils.Converters.bitmapToFile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.tables.ForbiddenDto;
import com.leon.counter_reading.tables.ForbiddenDtoMultiple;
import com.leon.counter_reading.tables.ForbiddenDtoRequestMultiple;
import com.leon.counter_reading.tables.ForbiddenDtoResponses;
import com.leon.counter_reading.tables.OffLoadReport;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.utils.CustomToast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PrepareOffLoad extends AsyncTask<Activity, Activity, Activity> {
    private final ArrayList<OnOffLoadDto> onOffLoadDtos = new ArrayList<>();
    private final ArrayList<OffLoadReport> offLoadReports = new ArrayList<>();
    private final ArrayList<ForbiddenDto> forbiddenDtos = new ArrayList<>();
    private final CustomProgressModel progress;
    private final int trackNumber;
    private final String id;

    public PrepareOffLoad(Activity activity, int trackNumber, String id) {
        super();
        progress = getApplicationComponent().CustomProgressModel();
        progress.show(activity, false);
        this.trackNumber = trackNumber;
        this.id = id;
    }

    @Override
    protected Activity doInBackground(Activity... activities) {
        forbiddenDtos.clear();
        forbiddenDtos.addAll(getApplicationComponent().MyDatabase().forbiddenDao()
                .getAllForbiddenDto(false));
        onOffLoadDtos.clear();
        onOffLoadDtos.addAll(getApplicationComponent().MyDatabase().onOffLoadDao()
                .getOnOffLoadReadByTrackingAndOffLoad(id, INSERTED.getValue()));
        offLoadReports.clear();
        offLoadReports.addAll(getApplicationComponent().MyDatabase().offLoadReportDao()
                .getAllOffLoadReport(false));
        return activities[0];
    }

    @Override
    protected void onPostExecute(Activity activity) {
        super.onPostExecute(activity);
        progress.getDialog().dismiss();
        uploadOffLoad(activity);
        if (forbiddenDtos.size() > 0) uploadForbid(activity);
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
                forbiddenDtoMultiple.File = bitmapToFile(CustomFile.loadImage(activity,
                        forbiddenDto.address), activity);
            forbiddenDtoRequestMultiple.forbiddenDtos.add(forbiddenDtoMultiple);
        }
        final Call<ForbiddenDtoResponses> call = iAbfaService.multipleForbidden(forbiddenDtoRequestMultiple);
        HttpClientWrapper.callHttpAsync(call, SHOW.getValue(), activity, new Forbidden(),
                new ForbiddenIncomplete(), new ForbiddenError());
    }

    @SuppressLint("SimpleDateFormat")
    private void uploadOffLoad(Activity activity) {
        if (onOffLoadDtos.size() == 0) {
            thankYou(activity);
            onOffLoadDtos.clear();
            onOffLoadDtos.add(getApplicationComponent().MyDatabase().onOffLoadDao()
                    .getOnOffLoadReadByTrackingAndOffLoad(id));
        }
        if (onOffLoadDtos.size() == 0 || onOffLoadDtos.get(0) == null) {
            getApplicationComponent().MyDatabase().trackingDao().updateTrackingDtoByArchive(id,
                    true, false, getDate(activity));
            return;
        }
        final Retrofit retrofit = getApplicationComponent().Retrofit();
        final IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        final OnOffLoadDto.OffLoadData offLoadData = new OnOffLoadDto.OffLoadData();
        offLoadData.isFinal = true;
        offLoadData.finalTrackNumber = trackNumber;
        for (int i = 0; i < onOffLoadDtos.size(); i++)
            offLoadData.offLoads.add(new OnOffLoadDto.OffLoad(onOffLoadDtos.get(i)));
        offLoadData.offLoadReports.addAll(offLoadReports);
        final Call<OnOffLoadDto.OffLoadResponses> call = iAbfaService.OffLoadData(offLoadData);
        HttpClientWrapper.callHttpAsync(call, SHOW.getValue(), activity, new OffLoadData(id, activity),
                new OffLoadDataIncomplete(), new OffLoadError());
    }

    private void thankYou(Activity activity) {
        activity.runOnUiThread(() ->
                new CustomToast().info(activity.getString(R.string.thank_you), Toast.LENGTH_LONG));
    }
}

class Forbidden implements ICallback<ForbiddenDtoResponses> {
    @Override
    public void execute(Response<ForbiddenDtoResponses> response) {
        if (response.isSuccessful()) {
            getApplicationComponent().MyDatabase().forbiddenDao().updateAllForbiddenDtoBySent(true);
            if (response.body() != null) {
                new CustomToast().success(getContext().
                                getString(R.string.report_forbid) + "\n" + response.body().message,
                        Toast.LENGTH_LONG);
            }
        }
    }
}

class ForbiddenIncomplete implements ICallbackIncomplete<ForbiddenDtoResponses> {
    @Override
    public void executeIncomplete(Response<ForbiddenDtoResponses> response) {
    }
}

class ForbiddenError implements ICallbackError {
    @Override
    public void executeError(Throwable t) {
    }
}

class OffLoadData implements ICallback<OnOffLoadDto.OffLoadResponses> {
    private final String id;
    private final Activity activity;

    public OffLoadData(String id, Activity activity) {
        this.id = id;
        this.activity = activity;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void execute(Response<OnOffLoadDto.OffLoadResponses> response) {
        if (response.body() != null && response.body().status == 200) {
            final int state = response.body().isValid ? SENT.getValue() : SENT_WITH_ERROR.getValue();
            getApplicationComponent().MyDatabase().onOffLoadDao().updateOnOffLoad(state,
                    response.body().targetObject);
            getApplicationComponent().MyDatabase().trackingDao().updateTrackingDtoByArchive(id,
                    true, false, getDate(activity));
            getApplicationComponent().MyDatabase().offLoadReportDao().updateOffLoadReportByIsSent(true);
            new CustomToast().success(response.body().message, Toast.LENGTH_LONG);
        }
    }
}

class OffLoadDataIncomplete implements ICallbackIncomplete<OnOffLoadDto.OffLoadResponses> {
    @Override
    public void executeIncomplete(Response<OnOffLoadDto.OffLoadResponses> response) {
        final CustomErrorHandling errorHandling = new CustomErrorHandling(getContext());
        final String error = errorHandling.getErrorMessageDefault(response);
        new CustomToast().warning(error, Toast.LENGTH_LONG);
    }
}

class OffLoadError implements ICallbackError {
    @Override
    public void executeError(Throwable t) {
        final CustomErrorHandling errorHandling = new CustomErrorHandling(getContext());
        final String error = errorHandling.getErrorMessageTotal(t);
        new CustomToast().error(error, Toast.LENGTH_LONG);
    }
}
