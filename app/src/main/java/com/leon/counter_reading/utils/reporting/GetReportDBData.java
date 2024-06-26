package com.leon.counter_reading.utils.reporting;

import static com.leon.counter_reading.enums.HighLowStateEnum.HIGH;
import static com.leon.counter_reading.enums.HighLowStateEnum.LOW;
import static com.leon.counter_reading.enums.HighLowStateEnum.NORMAL;
import static com.leon.counter_reading.enums.HighLowStateEnum.ZERO;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.activities.ReportActivity;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.MyDatabase;

import java.util.ArrayList;

public class GetReportDBData extends AsyncTask<Activity, Integer, Integer> {
    private final CustomProgressModel progress;
    private final MyDatabase myDatabase;
    private final ArrayList<CounterStateDto> counterStateDtos = new ArrayList<>();
    private final ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
    private int zero;
    private int normal;
    private int high;
    private int low;
    private int unread;
    private int beforeRead;
    private int continueRead;
    private int total;
    private int isMane;

    public GetReportDBData(Activity activity) {
        super();
        progress = getApplicationComponent().CustomProgressModel();
        progress.show(activity, false);
        myDatabase = getApplicationComponent().MyDatabase();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        progress.getDialog().dismiss();
    }

    @Override
    protected Integer doInBackground(Activity... activities) {
        trackingDtos.addAll(myDatabase.trackingDao().getTrackingDtosIsActiveNotArchive(true, false));
        //TODO
        for (int j = 0, trackingDtosSize = trackingDtos.size(); j < trackingDtosSize; j++) {
            TrackingDto trackingDto = trackingDtos.get(j);
            //TODO

            isMane = myDatabase.onOffLoadDao().getOnOffLoadIsManeCount(myDatabase.counterStateDao().
                    getCounterStateDtosIsMane(true, trackingDto.zoneId), trackingDto.id);

            zero += myDatabase.onOffLoadDao().getOnOffLoadReadCountByStatus(trackingDto.id, ZERO.getValue());
            high += myDatabase.onOffLoadDao().getOnOffLoadReadCountByStatus(trackingDto.id, HIGH.getValue());
            low += myDatabase.onOffLoadDao().getOnOffLoadReadCountByStatus(trackingDto.id, LOW.getValue());
            normal += myDatabase.onOffLoadDao().getOnOffLoadReadCountByStatus(trackingDto.id, NORMAL.getValue());
            unread += myDatabase.onOffLoadDao().getOnOffLoadReadCount(0, trackingDto.id);

            //TODO
            continueRead += myDatabase.onOffLoadDao().getOnOffLoadUnreadCount(0, trackingDto.id);
            beforeRead += myDatabase.onOffLoadDao().getOnOffLoadReadCount(trackingDto.id);
            //TODO
            total += myDatabase.onOffLoadDao().getOnOffLoad(trackingDto.id);
        }
        if (!trackingDtos.isEmpty())
            counterStateDtos.addAll(myDatabase.counterStateDao().getCounterStateDtos(trackingDtos.get(0).zoneId));
        try {
            activities[0].runOnUiThread(() ->
                    ((ReportActivity) (activities[0])).setupViewPager(counterStateDtos, zero, normal,
                            high, low, total, isMane, unread, beforeRead, continueRead));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}