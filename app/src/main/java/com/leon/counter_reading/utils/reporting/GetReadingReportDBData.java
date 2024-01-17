package com.leon.counter_reading.utils.reporting;

import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.fragments.dialog.ReadingReportFragment;
import com.leon.counter_reading.tables.CounterReportDto;
import com.leon.counter_reading.tables.OffLoadReport;

import java.util.ArrayList;

public class GetReadingReportDBData extends AsyncTask<Activity, Integer, Integer> {
    private final String uuid;
    private final int trackNumber;
    private final int zoneId;
    private final CustomProgressModel progress;

    public GetReadingReportDBData(Context context, int trackNumber, int zoneId, String uuid) {
        super();
        progress = getApplicationComponent().CustomProgressModel();
        progress.show(context, false);
        this.trackNumber = trackNumber;
        this.zoneId = zoneId;
        this.uuid = uuid;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        progress.getDialog().dismiss();
    }

    @Override
    protected Integer doInBackground(Activity... activities) {
        final ArrayList<CounterReportDto> counterReportDtos = new ArrayList<>(getApplicationComponent()
                .MyDatabase().counterReportDao().getAllCounterReportByZone(zoneId));
        final ArrayList<OffLoadReport> offLoadReports = new ArrayList<>(getApplicationComponent()
                .MyDatabase().offLoadReportDao().getAllOffLoadReportById(uuid, trackNumber));
        for (int i = 0; i < offLoadReports.size(); i++) {
            for (int j = 0; j < counterReportDtos.size(); j++) {
                if (offLoadReports.get(i).reportId == counterReportDtos.get(j).id) {
                    counterReportDtos.get(j).isSelected = true;
                }
            }
        }
        activities[0].runOnUiThread(() ->
                ReadingReportFragment.newInstance().setupRecyclerView(counterReportDtos, offLoadReports));

        return null;
    }
}