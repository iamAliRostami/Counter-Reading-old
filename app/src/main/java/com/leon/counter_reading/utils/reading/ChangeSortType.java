package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.helpers.Constants.readingData;
import static com.leon.counter_reading.helpers.Constants.readingDataTemp;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.di.view_model.CustomProgressModel;

import java.util.Collections;
import java.util.Comparator;

public class ChangeSortType extends AsyncTask<Activity, Void, Void> {
    private final boolean sortType;
    private final CustomProgressModel progress;

    public ChangeSortType(Activity activity, boolean sortType) {
        super();
        progress = getApplicationComponent().CustomProgressModel();
        progress.show(activity, false);
        this.sortType = sortType;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        progress.getDialog().dismiss();
    }

    @Override
    protected Void doInBackground(Activity... activities) {

        if (readingData.readingConfigDefaultDtos.get(0).isOnQeraatCode) {
            if (sortType) {
                Collections.sort(readingData.onOffLoadDtos, (o1, o2) -> o2.qeraatCode.compareTo(o1.qeraatCode));
                Collections.sort(readingDataTemp.onOffLoadDtos, (o1, o2) -> o2.qeraatCode.compareTo(o1.qeraatCode));
            } else {
//                Collections.sort(readingData.onOffLoadDtos, (o2, o1) -> o1.qeraatCode.compareTo(o2.qeraatCode));
//                Collections.sort(readingDataTemp.onOffLoadDtos, (o2, o1) -> o1.qeraatCode.compareTo(o2.qeraatCode));

                Collections.sort(readingData.onOffLoadDtos, Comparator.comparing(o -> o.qeraatCode));
                Collections.sort(readingDataTemp.onOffLoadDtos, Comparator.comparing(o -> o.qeraatCode));
            }

        } else {
            if (sortType) {
                Collections.sort(readingData.onOffLoadDtos, (o1, o2) -> o2.eshterak.compareTo(o1.eshterak));
                Collections.sort(readingDataTemp.onOffLoadDtos, (o1, o2) -> o2.eshterak.compareTo(o1.eshterak));

            } else {
                Collections.sort(readingData.onOffLoadDtos, Comparator.comparing(o -> o.eshterak));
                Collections.sort(readingDataTemp.onOffLoadDtos, Comparator.comparing(o -> o.eshterak));
/*
            Collections.sort(readingData.onOffLoadDtos, (o1, o2) -> o1.eshterak.compareTo(o2.eshterak));
            Collections.sort(readingDataTemp.onOffLoadDtos, (o1, o2) -> o1.eshterak.compareTo(o2.eshterak));
*/
            }
        }
        ((ReadingActivity) (activities[0])).setupViewPager(false);
        return null;
    }
}
