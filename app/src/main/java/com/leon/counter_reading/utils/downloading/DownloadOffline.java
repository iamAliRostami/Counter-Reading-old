package com.leon.counter_reading.utils.downloading;

import static com.leon.counter_reading.utils.OfflineUtils.getStorageDirectories;
import static com.leon.counter_reading.utils.OfflineUtils.readFromSdCard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.QotrDictionary;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;
import com.leon.counter_reading.tables.ReadingData;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.MyDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class DownloadOffline extends AsyncTask<Activity, Void, Void> {
    private final CustomProgressModel customProgressModel;

    public DownloadOffline(Activity activity) {
        super();
        customProgressModel = MyApplication.getApplicationComponent().CustomProgressModel();
        customProgressModel.show(activity, false);
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        final String[] retArray = getStorageDirectories();
        if (retArray.length == 0) {
            new CustomToast().error("کارت حافظه نصب نشده است.", Toast.LENGTH_LONG);
        } else {
            for (String s : retArray) {
                Log.e("path ", s);
//                readFromSdCard(s);
                final ReadingData readingData = readFromSdCard(s);
                Log.e("here","after reading");
                if (readingData == null) {
                    new CustomToast().warning("موردی برای بارگیری وجود ندارد.");
                    return null;
                }
                final ReadingData readingDataTemp = readFromSdCard(s);
                MyDatabase myDatabase = MyApplication.getApplicationComponent().MyDatabase();
                for (int i = 0; i < readingData.trackingDtos.size(); i++) {
                    if (myDatabase.trackingDao().getTrackingDtoArchiveCountByTrackNumber(readingData
                            .trackingDtos.get(i).trackNumber, true) > 0) {
                        downloadArchive(readingData, i, myDatabase, activities[0]);
                    } else if (myDatabase.trackingDao().getTrackingDtoActivesCountByTracking(readingData
                            .trackingDtos.get(i).trackNumber) > 0) {
                        String message = String.format(activities[0].getString(R.string.download_message_error),
                                readingData.trackingDtos.get(i).trackNumber);
                        showMessage(message, DialogType.Yellow, activities[0]);
                        return null;
                    }
                }
                if (readingData.trackingDtos.size() > 0 &&
                        myDatabase.trackingDao().getTrackingDtoActivesCount(true, false) == 0) {
                    readingData.trackingDtos.get(0).isActive = true;
                }
                myDatabase.trackingDao().insertAllTrackingDtos(readingData.trackingDtos);
                myDatabase.onOffLoadDao().insertAllOnOffLoad(readingData.onOffLoadDtos);

                ArrayList<CounterStateDto> counterStateDtos = new ArrayList<>(myDatabase
                        .counterStateDao().getCounterStateDtos());
                for (int j = 0; j < counterStateDtos.size(); j++) {
                    for (int i = 0; i < readingDataTemp.counterStateDtos.size(); i++) {
                        if (counterStateDtos.get(j).id == readingDataTemp.counterStateDtos.get(i).id)
                            readingData.counterStateDtos.remove(readingDataTemp.counterStateDtos.get(i));
                    }
                }
                myDatabase.counterStateDao().insertAllCounterStateDto(readingData.counterStateDtos);

                myDatabase.karbariDao().deleteKarbariDto();
                myDatabase.karbariDao().insertAllKarbariDtos(readingData.karbariDtos);

                ArrayList<QotrDictionary> qotrDictionaries = new ArrayList<>(myDatabase
                        .qotrDictionaryDao().getAllQotrDictionaries());
                for (int j = 0; j < qotrDictionaries.size(); j++) {
                    for (int i = 0; i < readingDataTemp.qotrDictionary.size(); i++) {
                        if (qotrDictionaries.get(j).id == readingDataTemp.qotrDictionary.get(i).id)
                            readingData.qotrDictionary.remove(readingDataTemp.qotrDictionary.get(i));
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
                    myDatabase.counterReportDao().deleteAllCounterReport();
                    myDatabase.counterReportDao().insertAllCounterStateReport(readingData.counterReportDtos);
                }
                String message = String.format(MyApplication.getContext().getString(R.string.download_message),
                        readingData.trackingDtos.size(), readingData.onOffLoadDtos.size());
                showMessage(message, DialogType.Green, activities[0]);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        customProgressModel.getDialog().dismiss();
        super.onPostExecute(unused);

    }

    private void showMessage(String message, DialogType dialogType, Activity activity) {
        activity.runOnUiThread(() -> new CustomDialogModel(dialogType, activity, message,
                MyApplication.getContext().getString(R.string.dear_user),
                MyApplication.getContext().getString(R.string.download),
                MyApplication.getContext().getString(R.string.accepted)));
    }

    @SuppressLint({"DefaultLocale", "SimpleDateFormat"})
    private void downloadArchive(ReadingData readingData, int i, MyDatabase myDatabase, Activity activity) {
        String time = (new SimpleDateFormat(activity
                .getString(R.string.save_format_name))).format(new Date())
                .concat(String.valueOf(new Random().nextInt(1000)));
        String query = "CREATE TABLE %s AS %s;";
        String query1 = String.format(query, "TrackingDto_".concat(time), String
                .format("SELECT * FROM TrackingDto WHERE trackNumber = %d AND isArchive = 1"
                        , readingData.trackingDtos.get(i).trackNumber));
        String query2 = String.format(query, "OnOffLoadDto_".concat(time), String
                .format("SELECT * FROM OnOffLoadDto WHERE trackNumber = %d"
                        , readingData.trackingDtos.get(i).trackNumber));
        Cursor cursor = myDatabase.getOpenHelper().getWritableDatabase().query(query1);
        cursor.moveToFirst();
        cursor = myDatabase.getOpenHelper().getWritableDatabase().query(query2);
        cursor.moveToFirst();
        myDatabase.trackingDao().deleteTrackingDto(readingData.trackingDtos.get(i).trackNumber, true);
        myDatabase.onOffLoadDao().deleteOnOffLoad(readingData.trackingDtos.get(i).trackNumber);
    }
}

