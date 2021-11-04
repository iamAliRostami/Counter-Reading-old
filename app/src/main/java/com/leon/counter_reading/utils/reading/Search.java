package com.leon.counter_reading.utils.reading;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.enums.SearchTypeEnum;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.utils.CustomToast;

import java.util.ArrayList;

public class Search extends AsyncTask<Activity, Void, Void> {
    private final int type;
    private final String key;
    private final boolean goToPage;
    private final ArrayList<OnOffLoadDto> onOffLoadDtos;
    private final ArrayList<OnOffLoadDto> onOffLoadDtosTemp;

    public Search(ArrayList<OnOffLoadDto> onOffLoadDtos, ArrayList<OnOffLoadDto> onOffLoadDtosTemp,
                  int type, String key, boolean goToPage) {
        super();
        this.type = type;
        this.key = key;
        this.goToPage = goToPage;
        this.onOffLoadDtosTemp = new ArrayList<>(onOffLoadDtosTemp);
        this.onOffLoadDtos = new ArrayList<>(onOffLoadDtos);
//        this.onOffLoadDtos = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        if (type == SearchTypeEnum.NAME.getValue()) {
            onOffLoadDtos.clear();
            for (int i = 0; i < onOffLoadDtosTemp.size(); i++) {
                if (onOffLoadDtosTemp.get(i).firstName.toLowerCase().contains(key) ||
                        onOffLoadDtosTemp.get(i).sureName.toLowerCase().contains(key))
                    onOffLoadDtos.add(onOffLoadDtosTemp.get(i));
            }
            ((ReadingActivity) (activities[0])).dataChanged(onOffLoadDtos, onOffLoadDtosTemp);
        } else {
            boolean found = false;
            int i = 0;
            if (goToPage) {
                if (type == SearchTypeEnum.ESHTERAK.getValue()) {
                    while (i < onOffLoadDtos.size() && !found) {
                        found = onOffLoadDtos.get(i).eshterak.contains(key);
                        i++;
                    }
                } else if (type == SearchTypeEnum.RADIF.getValue()) {
                    while (i < onOffLoadDtos.size() && !found) {
                        found = String.valueOf(onOffLoadDtos.get(i).radif).contains(key);
                        i++;
                    }
                } else if (type == SearchTypeEnum.BODY_COUNTER.getValue()) {
                    while (i < onOffLoadDtos.size() && !found) {
                        found = onOffLoadDtos.get(i).counterSerial.contains(key);
                        i++;
                    }
                }
                if (found)
                    ((ReadingActivity) (activities[0])).changePage(i - 1);
                else
                    activities[0].runOnUiThread(() ->
                            new CustomToast().warning(activities[0].getString(R.string.data_not_found)));
            } else {
                onOffLoadDtos.clear();
                if (type == SearchTypeEnum.ESHTERAK.getValue()) {
                    for (int j = 0; j < onOffLoadDtosTemp.size(); j++) {
                        if (onOffLoadDtosTemp.get(j).eshterak.toLowerCase().contains(key))
                            onOffLoadDtos.add(onOffLoadDtosTemp.get(j));
                    }
                } else if (type == SearchTypeEnum.RADIF.getValue()) {
                    for (int j = 0; j < onOffLoadDtosTemp.size(); j++) {
                        if (String.valueOf(onOffLoadDtosTemp.get(j).radif).contains(key))
                            onOffLoadDtos.add(onOffLoadDtosTemp.get(j));
                    }
                } else if (type == SearchTypeEnum.BODY_COUNTER.getValue()) {
                    for (int j = 0; j < onOffLoadDtosTemp.size(); j++) {
                        if (onOffLoadDtosTemp.get(j).counterSerial.toLowerCase().contains(key))
                            onOffLoadDtos.add(onOffLoadDtosTemp.get(j));
                    }
                }
                ((ReadingActivity) (activities[0])).dataChanged(onOffLoadDtos, onOffLoadDtosTemp);
            }
        }
        return null;
    }
}
