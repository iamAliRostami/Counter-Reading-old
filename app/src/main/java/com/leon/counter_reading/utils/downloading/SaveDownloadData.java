package com.leon.counter_reading.utils.downloading;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;

import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.QotrDictionary;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;
import com.leon.counter_reading.tables.ReadingData;
import com.leon.counter_reading.utils.MyDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class SaveDownloadData {
    public void savedData(Activity activity, ReadingData readingData, ReadingData readingDataTemp) {
        MyDatabase myDatabase = MyApplication.getApplicationComponent().MyDatabase();
//            long startTime = Calendar.getInstance().getTimeInMillis();
        for (int i = 0; i < readingData.trackingDtos.size(); i++) {
            if (myDatabase.trackingDao().getTrackingDtoArchiveCountByTrackNumber(readingData
                    .trackingDtos.get(i).trackNumber, true) > 0) {
                downloadArchive(activity, readingData, myDatabase, i);
                myDatabase.counterReportDao().deleteAllCounterReport(readingData.trackingDtos.get(i).zoneId);
            } else if (myDatabase.trackingDao().getTrackingDtoActivesCountByTracking(readingData
                    .trackingDtos.get(i).trackNumber) > 0) {
                String message = String.format(activity.getString(R.string.download_message_error),
                        readingData.trackingDtos.get(i).trackNumber);
                showMessage(activity, message, DialogType.Yellow);
                return;
            } else {
                myDatabase.counterReportDao().deleteAllCounterReport(readingData.counterReportDtos.get(0).zoneId);
            }
        }
        if (readingData.trackingDtos.size() > 0 &&
                myDatabase.trackingDao().getTrackingDtoActivesCount(true, false) == 0) {
            readingData.trackingDtos.get(0).isActive = true;
        }
        myDatabase.trackingDao().insertAllTrackingDtos(readingData.trackingDtos);
        myDatabase.onOffLoadDao().insertAllOnOffLoad(readingData.onOffLoadDtos);

        ArrayList<CounterStateDto> counterStateDtos = new ArrayList<>(myDatabase.counterStateDao()
                .getCounterStateDtos());
        for (int j = 0; j < counterStateDtos.size(); j++) {
            int i = 0;
            boolean found = false;
            while (readingData.counterStateDtos.size() > i && !found) {
                if (counterStateDtos.get(j).id == readingData.counterStateDtos.get(i).id) {
                    readingData.counterStateDtos.remove(i);
                    found = true;
                }
                i++;
            }
        }
        myDatabase.counterStateDao().insertAllCounterStateDto(readingData.counterStateDtos);

        myDatabase.karbariDao().deleteKarbariDto();
        myDatabase.karbariDao().insertAllKarbariDtos(readingData.karbariDtos);

        ArrayList<QotrDictionary> qotrDictionaries = new ArrayList<>(myDatabase.qotrDictionaryDao()
                .getAllQotrDictionaries());
        for (int j = 0; j < qotrDictionaries.size(); j++) {
            int i = 0;
            boolean found = false;
            while (readingData.qotrDictionary.size() > i && !found) {
                if (qotrDictionaries.get(j).id == readingData.qotrDictionary.get(i).id) {
                    readingData.qotrDictionary.remove(i);
                    found = true;
                }
                i++;
            }
        }
        myDatabase.qotrDictionaryDao().insertQotrDictionaries(readingData.qotrDictionary);

        ArrayList<ReadingConfigDefaultDto> readingConfigDefaultDtos = new ArrayList<>(
                myDatabase.readingConfigDefaultDao().getReadingConfigDefaultDtos());
        for (int j = 0; j < readingConfigDefaultDtos.size(); j++) {
            for (int i = 0; i < readingDataTemp.readingConfigDefaultDtos.size(); i++) {
                if (readingConfigDefaultDtos.get(j).id.equals(readingDataTemp
                        .readingConfigDefaultDtos.get(i).id))
                    readingData.readingConfigDefaultDtos.remove(readingDataTemp
                            .readingConfigDefaultDtos.get(i));
            }
        }
        myDatabase.readingConfigDefaultDao().insertAllReadingConfigDefault(
                readingData.readingConfigDefaultDtos);

        if (readingData.counterReportDtos.size() > 0) {
            myDatabase.counterReportDao().insertAllCounterStateReport(readingData.counterReportDtos);
        }
        String message = String.format(MyApplication.getContext().getString(R.string.download_message),
                readingData.trackingDtos.size(), readingData.onOffLoadDtos.size());
        showMessage(activity, message, DialogType.Green);
    }


    private void showMessage(Activity activity, String message, DialogType dialogType) {
        activity.runOnUiThread(() -> new CustomDialogModel(dialogType,
                activity, message,
                MyApplication.getContext().getString(R.string.dear_user),
                MyApplication.getContext().getString(R.string.download),
                MyApplication.getContext().getString(R.string.accepted)));
    }

    @SuppressLint({"DefaultLocale", "SimpleDateFormat"})
    private void downloadArchive(Activity activity, ReadingData readingData, MyDatabase myDatabase,
                                 int i) {
        String time = (new SimpleDateFormat(activity
                .getString(R.string.save_format_name))).format(new Date())
                .concat(String.valueOf(new Random().nextInt(1000)));
        String query = "CREATE TABLE %s AS %s;";
        String query1 = String.format(query, "TrackingDto_".concat(time),
                String.format("SELECT * FROM TrackingDto WHERE trackNumber = %d AND isArchive = 1",
                        readingData.trackingDtos.get(i).trackNumber));
        String query2 = String.format(query, "OnOffLoadDto_".concat(time),
                String.format("SELECT * FROM OnOffLoadDto WHERE trackNumber = %d",
                        readingData.trackingDtos.get(i).trackNumber));
        try {
            Cursor cursor = myDatabase.getOpenHelper().getWritableDatabase().query(query1);
            cursor.moveToFirst();
            cursor = myDatabase.getOpenHelper().getWritableDatabase().query(query2);
            cursor.moveToFirst();
            myDatabase.trackingDao().deleteTrackingDto(readingData.trackingDtos.get(i).trackNumber, true);
            myDatabase.onOffLoadDao().deleteOnOffLoad(readingData.trackingDtos.get(i).trackNumber);
        } catch (Exception ignored) {
        }
    }
}
