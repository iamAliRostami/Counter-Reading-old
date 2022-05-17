package com.leon.counter_reading.utils.uploading;

import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.activities.UploadActivity;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.tables.TrackingDto;

import java.util.ArrayList;

public class GetUploadDBData extends AsyncTask<Activity, Integer, Integer> {
    private final CustomProgressModel progress;

    public GetUploadDBData(Activity activity) {
        super();
        progress = getApplicationComponent().CustomProgressModel();
        progress.show(activity, false);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        progress.getDialog().dismiss();
        super.onPostExecute(integer);
    }

    @Override
    protected Integer doInBackground(Activity... activities) {
        final ArrayList<TrackingDto> trackingDtos = new ArrayList<>(getApplicationComponent().MyDatabase().
                trackingDao().getTrackingDtoNotArchive(false));
        ((UploadActivity) (activities[0])).setupUI(trackingDtos);
        return null;
    }
}
