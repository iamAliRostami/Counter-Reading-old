package com.leon.counter_reading.utils.forbid;

import static com.leon.counter_reading.enums.ProgressType.SHOW;
import static com.leon.counter_reading.helpers.Constants.CURRENT_IMAGE_SIZE;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.utils.CustomFile.saveTempBitmap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.tables.ForbiddenDto;
import com.leon.counter_reading.tables.ForbiddenDtoResponses;
import com.leon.counter_reading.utils.CustomToast;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PrepareForbid extends AsyncTask<Activity, Activity, Activity> {
    private final CustomProgressModel customProgressModel;
    private final ForbiddenDto forbiddenDto;
    private final int zoneId;

    public PrepareForbid(Activity activity, ForbiddenDto forbiddenDto, int zoneId) {
        super();
        customProgressModel = getApplicationComponent().CustomProgressModel();
        customProgressModel.show(activity, false);
        this.forbiddenDto = forbiddenDto;
        this.zoneId = zoneId;
    }

    @Override
    protected Activity doInBackground(Activity... activities) {
        final Retrofit retrofit = getApplicationComponent().Retrofit();
        final IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        final Call<ForbiddenDtoResponses> call;
        if (zoneId != 0 && forbiddenDto.File.size() > 0) {
            call = iAbfaService.singleForbidden(forbiddenDto.File,
                    forbiddenDto.forbiddenDtoRequest.zoneId,
                    forbiddenDto.forbiddenDtoRequest.description,
                    forbiddenDto.forbiddenDtoRequest.preEshterak,
                    forbiddenDto.forbiddenDtoRequest.nextEshterak,
                    forbiddenDto.forbiddenDtoRequest.postalCode,
                    forbiddenDto.forbiddenDtoRequest.tedadVahed,
                    forbiddenDto.forbiddenDtoRequest.activate,
                    forbiddenDto.forbiddenDtoRequest.x,
                    forbiddenDto.forbiddenDtoRequest.y,
                    forbiddenDto.forbiddenDtoRequest.gisAccuracy);
        } else if (zoneId == 0 && forbiddenDto.File.size() > 0) {
            call = iAbfaService.singleForbidden(forbiddenDto.File,
                    forbiddenDto.forbiddenDtoRequest.description,
                    forbiddenDto.forbiddenDtoRequest.preEshterak,
                    forbiddenDto.forbiddenDtoRequest.nextEshterak,
                    forbiddenDto.forbiddenDtoRequest.postalCode,
                    forbiddenDto.forbiddenDtoRequest.tedadVahed,
                    forbiddenDto.forbiddenDtoRequest.activate,
                    forbiddenDto.forbiddenDtoRequest.x,
                    forbiddenDto.forbiddenDtoRequest.y,
                    forbiddenDto.forbiddenDtoRequest.gisAccuracy);
        } else if (zoneId != 0) {
            call = iAbfaService.singleForbidden(forbiddenDto.forbiddenDtoRequest.zoneId,
                    forbiddenDto.forbiddenDtoRequest.description,
                    forbiddenDto.forbiddenDtoRequest.preEshterak,
                    forbiddenDto.forbiddenDtoRequest.nextEshterak,
                    forbiddenDto.forbiddenDtoRequest.postalCode,
                    forbiddenDto.forbiddenDtoRequest.tedadVahed,
                    forbiddenDto.forbiddenDtoRequest.activate,
                    forbiddenDto.forbiddenDtoRequest.x,
                    forbiddenDto.forbiddenDtoRequest.y,
                    forbiddenDto.forbiddenDtoRequest.gisAccuracy);
        } else {
            call = iAbfaService.singleForbidden(forbiddenDto.forbiddenDtoRequest.description,
                    forbiddenDto.forbiddenDtoRequest.preEshterak,
                    forbiddenDto.forbiddenDtoRequest.nextEshterak,
                    forbiddenDto.forbiddenDtoRequest.postalCode,
                    forbiddenDto.forbiddenDtoRequest.tedadVahed,
                    forbiddenDto.forbiddenDtoRequest.activate,
                    forbiddenDto.forbiddenDtoRequest.x,
                    forbiddenDto.forbiddenDtoRequest.y,
                    forbiddenDto.forbiddenDtoRequest.gisAccuracy);
        }
        activities[0].runOnUiThread(() -> {
            customProgressModel.getDialog().dismiss();
            HttpClientWrapper.callHttpAsync(call, SHOW.getValue(), activities[0],
                    new Forbidden(forbiddenDto), new ForbiddenIncomplete(activities[0]),
                    new ForbiddenError(activities[0]));
        });
        return null;
    }

    @Override
    protected void onPostExecute(Activity activity) {
        super.onPostExecute(activity);
    }

    private void saveForbidden(Activity activity) {
        if (forbiddenDto.bitmaps.size() > 0)
            for (Bitmap bitmap : forbiddenDto.bitmaps) {
                final String address = saveTempBitmap(bitmap, activity);
                if (!address.equals(activity.getString(R.string.error_external_storage_is_not_writable))) {
                    forbiddenDto.address = address;
                    forbiddenDto.size = CURRENT_IMAGE_SIZE;
                    getApplicationComponent().MyDatabase().forbiddenDao().insertForbiddenDto(forbiddenDto);
                }
            }
        else
            getApplicationComponent().MyDatabase().forbiddenDao().insertForbiddenDto(forbiddenDto);
    }

    class ForbiddenIncomplete implements ICallbackIncomplete<ForbiddenDtoResponses> {
        private final Activity activity;

        public ForbiddenIncomplete(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void executeIncomplete(Response<ForbiddenDtoResponses> response) {
            saveForbidden(activity);
        }
    }

    class ForbiddenError implements ICallbackError {
        private final Activity activity;

        public ForbiddenError(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void executeError(Throwable t) {
            saveForbidden(activity);
        }
    }
}

class Forbidden implements ICallback<ForbiddenDtoResponses> {
    private final ForbiddenDto forbiddenDto;

    public Forbidden(ForbiddenDto forbiddenDto) {
        this.forbiddenDto = forbiddenDto;
    }

    @Override
    public void execute(Response<ForbiddenDtoResponses> response) {
        if (!response.isSuccessful())
            getApplicationComponent().MyDatabase().forbiddenDao().insertForbiddenDto(forbiddenDto);
        else {
            if (response.body() != null) {
                new CustomToast().success(response.body().message);
            }
        }
    }
}