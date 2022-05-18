package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.enums.SearchTypeEnum.BODY_COUNTER;
import static com.leon.counter_reading.enums.SearchTypeEnum.ESHTERAK;
import static com.leon.counter_reading.enums.SearchTypeEnum.NAME;
import static com.leon.counter_reading.enums.SearchTypeEnum.RADIF;
import static com.leon.counter_reading.helpers.Constants.readingData;
import static com.leon.counter_reading.helpers.Constants.readingDataTemp;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.enums.SearchTypeEnum;
import com.leon.counter_reading.utils.CustomToast;

public class Search extends AsyncTask<Activity, Void, Void> {
    private final int type;
    private final String key;
    private final boolean goToPage;

    public Search(int type, String key, boolean goToPage) {
        super();
        this.type = type;
        this.key = key;
        this.goToPage = goToPage;
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        if (type == NAME.getValue()) {
            readingData.onOffLoadDtos.clear();
            for (int i = 0; i < readingDataTemp.onOffLoadDtos.size(); i++) {
                if (readingDataTemp.onOffLoadDtos.get(i).firstName.toLowerCase().contains(key) ||
                        readingDataTemp.onOffLoadDtos.get(i).sureName.toLowerCase().contains(key))
                    readingData.onOffLoadDtos.add(readingDataTemp.onOffLoadDtos.get(i));
            }
            ((ReadingActivity) (activities[0])).setupViewPager();
        } else {
            boolean found = false;
            int i = 0;
            if (goToPage) {
                if (type == ESHTERAK.getValue()) {
                    while (i < readingData.onOffLoadDtos.size() && !found) {
                        found = readingData.onOffLoadDtos.get(i).eshterak.contains(key);
                        i++;
                    }
                } else if (type == RADIF.getValue()) {
                    while (i < readingData.onOffLoadDtos.size() && !found) {
                        found = String.valueOf(readingData.onOffLoadDtos.get(i).radif).contains(key);
                        i++;
                    }
                } else if (type == BODY_COUNTER.getValue()) {
                    while (i < readingData.onOffLoadDtos.size() && !found) {
                        found = readingData.onOffLoadDtos.get(i).counterSerial.contains(key);
                        i++;
                    }
                }
                if (found)
                    ((ReadingActivity) (activities[0])).changePage(i - 1);
                else
                    activities[0].runOnUiThread(() ->
                            new CustomToast().warning(activities[0].getString(R.string.data_not_found)));
            } else {
                readingData.onOffLoadDtos.clear();
                if (type == ESHTERAK.getValue()) {
                    for (int j = 0; j < readingDataTemp.onOffLoadDtos.size(); j++) {
                        if (readingDataTemp.onOffLoadDtos.get(j).eshterak.toLowerCase().contains(key))
                            readingData.onOffLoadDtos.add(readingDataTemp.onOffLoadDtos.get(j));
                    }
                } else if (type == RADIF.getValue()) {
                    for (int j = 0; j < readingDataTemp.onOffLoadDtos.size(); j++) {
                        if (String.valueOf(readingDataTemp.onOffLoadDtos.get(j).radif).contains(key))
                            readingData.onOffLoadDtos.add(readingDataTemp.onOffLoadDtos.get(j));
                    }
                } else if (type == BODY_COUNTER.getValue()) {
                    for (int j = 0; j < readingDataTemp.onOffLoadDtos.size(); j++) {
                        if (readingDataTemp.onOffLoadDtos.get(j).counterSerial.toLowerCase().contains(key))
                            readingData.onOffLoadDtos.add(readingDataTemp.onOffLoadDtos.get(j));
                    }
                }
                ((ReadingActivity) (activities[0])).setupViewPager();
            }
        }
        return null;
    }
}
