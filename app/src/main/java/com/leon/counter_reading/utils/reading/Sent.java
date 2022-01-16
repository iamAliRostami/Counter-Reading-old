package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.helpers.Constants.readingData;
import static com.leon.counter_reading.helpers.Constants.readingDataTemp;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.enums.OffloadStateEnum;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.utils.CustomToast;

public class Sent extends AsyncTask<Activity, Integer, Integer> {
    private final OnOffLoadDto.OffLoadResponses offLoadResponses;

    public Sent(OnOffLoadDto.OffLoadResponses offLoadResponses) {
        super();
        this.offLoadResponses = offLoadResponses;
    }

    @Override
    protected Integer doInBackground(Activity... activities) {
        try {
            //TODO
            getApplicationComponent().MyDatabase().offLoadReportDao().updateOffLoadReportByIsSent(true);
            int state = offLoadResponses.isValid ? OffloadStateEnum.SENT.getValue() :
                    OffloadStateEnum.SENT_WITH_ERROR.getValue();
            getApplicationComponent().MyDatabase().onOffLoadDao()
                    .updateOnOffLoad(state, offLoadResponses.targetObject);
            String[] targetObject = offLoadResponses.targetObject;

            for (String s : targetObject) {
                boolean found = false;
                int i = 0;
                while (!found && i < readingData.onOffLoadDtos.size()) {
                    if (s.equals(readingData.onOffLoadDtos.get(i).id)) {
                        readingData.onOffLoadDtos.get(i).offLoadStateId = state;
                        found = true;
                    }
                    i++;
                }
                found = false;
                i = 0;
                while (!found && i < readingDataTemp.onOffLoadDtos.size()) {
                    if (s.equals(readingDataTemp.onOffLoadDtos.get(i).id)) {
                        readingDataTemp.onOffLoadDtos.get(i).offLoadStateId = state;
                        found = true;
                    }
                    i++;
                }
            }
        } catch (Exception e) {
            new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
        }
        return null;
    }
}