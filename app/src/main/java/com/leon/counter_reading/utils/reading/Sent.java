package com.leon.counter_reading.utils.reading;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.enums.OffloadStateEnum;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.utils.CustomToast;

import java.util.ArrayList;

public class Sent extends AsyncTask<Activity, Integer, Integer> {
    private final ArrayList<OnOffLoadDto> onOffLoadDtos;
    private final ArrayList<OnOffLoadDto> onOffLoadDtosTemp;
    private final OnOffLoadDto.OffLoadResponses offLoadResponses;

    public Sent(ArrayList<OnOffLoadDto> onOffLoadDtos, ArrayList<OnOffLoadDto> onOffLoadDtosTemp,
                OnOffLoadDto.OffLoadResponses offLoadResponses) {
        super();
        this.onOffLoadDtos = new ArrayList<>(onOffLoadDtos);
        this.onOffLoadDtosTemp = new ArrayList<>(onOffLoadDtosTemp);
        this.offLoadResponses =offLoadResponses;
    }

    @Override
    protected Integer doInBackground(Activity... activities) {
        try {
            //TODO
            MyApplication.getApplicationComponent().MyDatabase().offLoadReportDao().updateOffLoadReportByIsSent(true);
            int state = offLoadResponses.isValid ? OffloadStateEnum.SENT.getValue() :
                    OffloadStateEnum.SENT_WITH_ERROR.getValue();
            MyApplication.getApplicationComponent().MyDatabase().onOffLoadDao()
                    .updateOnOffLoad(state, offLoadResponses.targetObject);
            String[] targetObject = offLoadResponses.targetObject;

            for (String s : targetObject) {
                boolean found = false;
                int i = 0;
                while (!found && i < onOffLoadDtos.size()) {
                    if (s.equals(onOffLoadDtos.get(i).id)) {
                        onOffLoadDtos.get(i).offLoadStateId = state;
                        found = true;
                    }
                    i++;
                }
                found = false;
                i = 0;
                while (!found && i < onOffLoadDtosTemp.size()) {
                    if (s.equals(onOffLoadDtosTemp.get(i).id)) {
                        onOffLoadDtosTemp.get(i).offLoadStateId = state;
                        found = true;
                    }
                    i++;
                }
            }
            ((ReadingActivity) (activities[0])).setReadingDataOnOffload(onOffLoadDtos, onOffLoadDtosTemp);
        } catch (Exception e) {
            new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
        }
        return null;
    }
}