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
import com.leon.counter_reading.utils.CustomToast;

public class Search extends AsyncTask<Activity, Void, Activity> {
    private final int type;
    private final String key;
    private final boolean goToPage;
    private boolean result;

    public Search(int type, String key, boolean goToPage) {
        super();
        this.type = type;
        this.key = key;
        this.goToPage = goToPage;
    }

    @Override
    protected Activity doInBackground(Activity... activities) {
        if (type == NAME.getValue()) {
            result = searchByName(activities[0]);
        } else {
            if (goToPage) {
                result = filter(activities[0]);
            } else {
                result = searchOther(activities[0]);
            }
        }
        return activities[0];
    }

    @Override
    protected void onPostExecute(Activity activity) {
        super.onPostExecute(activity);
        ((ReadingActivity) activity).searchResult = result;
    }

    private boolean searchOther(Activity activity) {
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
        if (!readingData.onOffLoadDtos.isEmpty())
            ((ReadingActivity) (activity)).setupViewPager(false);
        else {
            activity.runOnUiThread(() ->
                    new CustomToast().warning(activity.getString(R.string.data_not_found)));
            readingData.onOffLoadDtos.addAll(readingDataTemp.onOffLoadDtos);
            return false;
        }
        return true;
    }

    private boolean searchByName(Activity activity) {
        readingData.onOffLoadDtos.clear();
        for (int i = 0; i < readingDataTemp.onOffLoadDtos.size(); i++) {
            if (readingDataTemp.onOffLoadDtos.get(i).firstName.toLowerCase().contains(key) ||
                    readingDataTemp.onOffLoadDtos.get(i).sureName.toLowerCase().contains(key))
                readingData.onOffLoadDtos.add(readingDataTemp.onOffLoadDtos.get(i));
        }
        if (!readingData.onOffLoadDtos.isEmpty())
            ((ReadingActivity) (activity)).setupViewPager(false);
        else {
            activity.runOnUiThread(() ->
                    new CustomToast().warning(activity.getString(R.string.data_not_found)));
            readingData.onOffLoadDtos.addAll(readingDataTemp.onOffLoadDtos);
            return false;
        }
        return true;
    }

    private boolean filter(Activity activity) {
        boolean found = false;
        int i = 0;
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
            ((ReadingActivity) (activity)).changePage(i - 1);
        else
            activity.runOnUiThread(() ->
                    new CustomToast().warning(activity.getString(R.string.data_not_found)));
        return found;
    }
}
