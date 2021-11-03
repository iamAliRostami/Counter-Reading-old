package com.leon.counter_reading.utils.reading;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.tables.OnOffLoadDto;

import java.util.ArrayList;
import java.util.Collections;

public class ChangeSortType extends AsyncTask<Activity, Void, Void> {
    private final boolean sortType;
    private final CustomProgressModel customProgressModel;
    private final ArrayList<OnOffLoadDto> onOffLoadDtos;
    private final ArrayList<OnOffLoadDto> onOffLoadDtosTemp;

    public ChangeSortType(Activity activity, boolean sortType, ArrayList<OnOffLoadDto> onOffLoadDtos,
                          ArrayList<OnOffLoadDto> onOffLoadDtosTemp) {
        super();
        customProgressModel = MyApplication.getApplicationComponent().CustomProgressModel();
        customProgressModel.show(activity, false);
        this.sortType = sortType;
        this.onOffLoadDtos = new ArrayList<>(onOffLoadDtos);
        this.onOffLoadDtosTemp = new ArrayList<>(onOffLoadDtosTemp);
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        customProgressModel.getDialog().dismiss();
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        if (sortType) {
            Collections.sort(onOffLoadDtos, (o1, o2) -> o2.eshterak.compareTo(o1.eshterak));
            Collections.sort(onOffLoadDtosTemp, (o1, o2) -> o2.eshterak.compareTo(o1.eshterak));
        } else {
            Collections.sort(onOffLoadDtos, (o1, o2) -> o1.eshterak.compareTo(o2.eshterak));
            Collections.sort(onOffLoadDtosTemp, (o1, o2) -> o1.eshterak.compareTo(o2.eshterak));
        }
        ((ReadingActivity) (activities[0])).dataChanged(onOffLoadDtos, onOffLoadDtosTemp);
        return null;
    }
}
