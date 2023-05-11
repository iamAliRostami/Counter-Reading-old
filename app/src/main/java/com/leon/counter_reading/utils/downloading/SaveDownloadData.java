package com.leon.counter_reading.utils.downloading;

import static com.leon.counter_reading.enums.DialogType.Green;
import static com.leon.counter_reading.enums.DialogType.Yellow;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getExpireDate;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.utils.Converters.replaceNonstandardDigits;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;

import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.DynamicTraverse;
import com.leon.counter_reading.tables.QotrDictionary;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;
import com.leon.counter_reading.tables.ReadingData;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.MyDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class SaveDownloadData {
    public void savedData(Activity activity, ReadingData readingData, ReadingData readingDataTemp) {
        deleteExpiredData(activity);
        final MyDatabase myDatabase = getApplicationComponent().MyDatabase();
//            long startTime = Calendar.getInstance().getTimeInMillis();
        for (int i = 0; i < readingData.trackingDtos.size(); i++) {
            if (myDatabase.trackingDao().getTrackingDtoArchiveCountByTrackNumber(readingData
                    .trackingDtos.get(i).trackNumber, true) > 0) {
                if (!downloadArchive(activity, readingData, myDatabase, i)) return;
                myDatabase.counterReportDao().deleteAllCounterReport(readingData.trackingDtos.get(i).zoneId);
            } else if (myDatabase.trackingDao().getTrackingDtoActivesCountByTracking(readingData
                    .trackingDtos.get(i).trackNumber) > 0) {
                final String message = String.format(activity.getString(R.string.download_message_error),
                        readingData.trackingDtos.get(i).trackNumber);
                showMessage(activity, message, Yellow);
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

        final ArrayList<CounterStateDto> counterStateDtos = new ArrayList<>(myDatabase.counterStateDao()
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

        final ArrayList<QotrDictionary> qotrDictionaries = new ArrayList<>(myDatabase.qotrDictionaryDao()
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

        final ArrayList<ReadingConfigDefaultDto> readingConfigDefaultDtos = new ArrayList<>(
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

        myDatabase.guildDao().deleteGuildCompletely();
        if (readingData.guilds.size() > 0) {
            myDatabase.guildDao().insertGuild(readingData.guilds);
        }

        //TODO
        myDatabase.dynamicTraverseDao().deleteDynamicTraverse();
        myDatabase.dynamicTraverseDao().insertDynamicTraverse(readingData.dynamicTraverses);
        for (DynamicTraverse dynamicTraverse : readingData.dynamicTraverses) {
            getApplicationComponent().SharedPreferenceModel().putData(dynamicTraverse.storageTitle, dynamicTraverse.defaultValue);
        }

        final String message = String.format(MyApplication.getContext().getString(R.string.download_message),
                readingData.trackingDtos.size(), readingData.onOffLoadDtos.size());
        showMessage(activity, message, Green);
    }


    private void showMessage(Activity activity, String message, DialogType dialogType) {
        activity.runOnUiThread(() -> new CustomDialogModel(dialogType, activity, message,
                MyApplication.getContext().getString(R.string.dear_user),
                MyApplication.getContext().getString(R.string.download),
                MyApplication.getContext().getString(R.string.accepted)));
    }

    @SuppressLint({"DefaultLocale", "SimpleDateFormat"})
    private boolean downloadArchive(Activity activity, ReadingData readingData, MyDatabase myDatabase,
                                    int i) {
        try {
            final String time = replaceNonstandardDigits(new SimpleDateFormat(activity
                    .getString(R.string.save_format_name))
                    .format(new Date()).concat(String.valueOf(new Random().nextInt(1000))));
            int trackNumber = Integer.parseInt(replaceNonstandardDigits(String.valueOf(readingData.trackingDtos.get(i).trackNumber)));
            final String query = "CREATE TABLE `%s` AS %s;";
            final String queryTrackDto = String.format(query, "TrackingDto_".concat(time),
                    String.format("SELECT * FROM TrackingDto WHERE trackNumber = %d AND isArchive = 1",
                            trackNumber));

            final String queryOnOffLoad = String.format(query, "OnOffLoadDto_".concat(time),
                    String.format("SELECT * FROM OnOffLoadDto WHERE trackNumber = %d", trackNumber));

            try {
                Cursor cursor = myDatabase.getOpenHelper().getWritableDatabase().query(queryTrackDto);
                cursor.moveToFirst();
                cursor = myDatabase.getOpenHelper().getWritableDatabase().query(queryOnOffLoad);
                cursor.moveToFirst();

                myDatabase.trackingDao().deleteTrackingDto(readingData.trackingDtos.get(i).trackNumber, true);
                myDatabase.onOffLoadDao().deleteOnOffLoads(readingData.trackingDtos.get(i).trackNumber);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                new CustomToast().error(e.getMessage());
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            new CustomToast().error(e.getMessage());
            return false;
        }
    }

    private void deleteExpiredData(Activity activity) {
        final List<Integer> trackNumbers = getApplicationComponent().MyDatabase().trackingDao()
                .getTrackingDtosExpired(true, getExpireDate(activity));
        getApplicationComponent().MyDatabase().trackingDao().deleteTrackingDtos(trackNumbers);
        getApplicationComponent().MyDatabase().onOffLoadDao().deleteOnOffLoads(trackNumbers);
    }
}
