package com.leon.counter_reading.utils.downloading;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.tables.ReadingData;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.utils.CustomToast;

import java.io.File;

public class DownloadOffline extends AsyncTask<Activity, Void, Void> {
    private final CustomProgressModel customProgressModel;
    private final String json;

    public DownloadOffline(Activity activity, File file) {
        super();
        customProgressModel = MyApplication.getApplicationComponent().CustomProgressModel();
        customProgressModel.show(activity, false);
        this.json = CustomFile.readData(file);
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        Gson gson = new Gson();
        ReadingData readingData, readingDataTemp;
        try {
            readingData = gson.fromJson(json, ReadingData.class);
            readingDataTemp = gson.fromJson(json, ReadingData.class);
        } catch (Exception e) {
            activities[0].runOnUiThread(() -> new CustomToast().error("فایل انتخاب شده نادرست است."));
            return null;
        }
        if (readingData == null) {
            new CustomToast().warning("موردی برای بارگیری وجود ندارد.");
            return null;
        }
        new SaveDownloadData().savedData(activities[0], readingData, readingDataTemp);

        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        customProgressModel.getDialog().dismiss();
        super.onPostExecute(unused);

    }
}

