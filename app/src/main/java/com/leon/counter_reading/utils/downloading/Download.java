package com.leon.counter_reading.utils.downloading;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.fragments.DownloadFragment;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.tables.ReadingData;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomToast;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Download extends AsyncTask<Activity, Void, Void> {
    private final DownloadFragment fragment;

    public Download(DownloadFragment fragment) {
        super();
        this.fragment = fragment;
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        Retrofit retrofit = MyApplication.getApplicationComponent().Retrofit();
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        //TODO
        Call<ReadingData> call = iAbfaService.loadData(BuildConfig.VERSION_CODE);
        activities[0].runOnUiThread(() ->
                HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), activities[0],
                        new DownloadCompleted(activities[0]), new DownloadIncomplete(), new DownloadError()));
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        fragment.setButtonState();
//        throw new RuntimeException("Test Force Crash"); // Force a crash

    }
}

class DownloadCompleted implements ICallback<ReadingData> {
    Activity activity;

    public DownloadCompleted(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void execute(Response<ReadingData> response) {
        if (response != null && response.body() != null) {
            ReadingData readingData = response.body();
            ReadingData readingDataTemp = response.body();
            new SaveDownloadData().savedData(activity, readingData, readingDataTemp);
//            MyDatabase myDatabase = MyApplication.getApplicationComponent().MyDatabase();
////            insert TrackingDto and OnOffLoadDto
////            long startTime = Calendar.getInstance().getTimeInMillis();
//            for (int i = 0; i < readingData.trackingDtos.size(); i++) {
//                if (myDatabase.trackingDao().getTrackingDtoArchiveCountByTrackNumber(readingData
//                        .trackingDtos.get(i).trackNumber, true) > 0) {
//                    downloadArchive(readingData, i, myDatabase);
//                    myDatabase.counterReportDao().deleteAllCounterReport(readingData.trackingDtos.get(i).zoneId);
//                } else if (myDatabase.trackingDao().getTrackingDtoActivesCountByTracking(readingData
//                        .trackingDtos.get(i).trackNumber) > 0) {
//                    String message = String.format(activity.getString(R.string.download_message_error),
//                            readingData.trackingDtos.get(i).trackNumber);
//                    showMessage(message, DialogType.Yellow);
//                    return;
//                } else {
//                    myDatabase.counterReportDao().deleteAllCounterReport(readingData.counterReportDtos.get(0).zoneId);
//                }
//            }
//            if (readingData.trackingDtos.size() > 0 &&
//                    myDatabase.trackingDao().getTrackingDtoActivesCount(true, false) == 0) {
//                readingData.trackingDtos.get(0).isActive = true;
//            }
//            myDatabase.trackingDao().insertAllTrackingDtos(readingData.trackingDtos);
//            myDatabase.onOffLoadDao().insertAllOnOffLoad(readingData.onOffLoadDtos);
//
//            ArrayList<CounterStateDto> counterStateDtos = new ArrayList<>(myDatabase
//                    .counterStateDao().getCounterStateDtos());
//            for (int j = 0; j < counterStateDtos.size(); j++) {
//                for (int i = 0; i < readingDataTemp.counterStateDtos.size(); i++) {
//                    if (counterStateDtos.get(j).id == readingDataTemp.counterStateDtos.get(i).id)
//                        readingData.counterStateDtos.remove(readingDataTemp.counterStateDtos.get(i));
//                }
//            }
//            myDatabase.counterStateDao().insertAllCounterStateDto(readingData.counterStateDtos);
//
//            myDatabase.karbariDao().deleteKarbariDto();
//            myDatabase.karbariDao().insertAllKarbariDtos(readingData.karbariDtos);
//
//            ArrayList<QotrDictionary> qotrDictionaries = new ArrayList<>(myDatabase
//                    .qotrDictionaryDao().getAllQotrDictionaries());
//            for (int j = 0; j < qotrDictionaries.size(); j++) {
//                for (int i = 0; i < readingDataTemp.qotrDictionary.size(); i++) {
//                    if (qotrDictionaries.get(j).id == readingDataTemp.qotrDictionary.get(i).id)
//                        readingData.qotrDictionary.remove(readingDataTemp.qotrDictionary.get(i));
//                }
//            }
//            myDatabase.qotrDictionaryDao().insertQotrDictionaries(readingData.qotrDictionary);
//
//            ArrayList<ReadingConfigDefaultDto> readingConfigDefaultDtos = new ArrayList<>(
//                    myDatabase.readingConfigDefaultDao().getReadingConfigDefaultDtos());
//            for (int j = 0; j < readingConfigDefaultDtos.size(); j++) {
//                for (int i = 0; i < readingDataTemp.readingConfigDefaultDtos.size(); i++) {
//                    if (readingConfigDefaultDtos.get(j).id.equals(readingDataTemp
//                            .readingConfigDefaultDtos.get(i).id))
//                        readingData.readingConfigDefaultDtos.remove(readingDataTemp
//                                .readingConfigDefaultDtos.get(i));
//                }
//            }
//            myDatabase.readingConfigDefaultDao().insertAllReadingConfigDefault(
//                    readingData.readingConfigDefaultDtos);
//
//            if (readingData.counterReportDtos.size() > 0) {
////                myDatabase.counterReportDao().deleteAllCounterReport(readingData.counterReportDtos.get(0).zoneId);
//                myDatabase.counterReportDao().insertAllCounterStateReport(readingData.counterReportDtos);
//            }
//            String message = String.format(MyApplication.getContext().getString(R.string.download_message),
//                    readingData.trackingDtos.size(), readingData.onOffLoadDtos.size());
//            showMessage(message, DialogType.Green);
        }
    }

//    private void showMessage(String message, DialogType dialogType) {
//        activity.runOnUiThread(() -> new CustomDialogModel(dialogType,
//                activity, message,
//                MyApplication.getContext().getString(R.string.dear_user),
//                MyApplication.getContext().getString(R.string.download),
//                MyApplication.getContext().getString(R.string.accepted)));
//    }
//
//    @SuppressLint({"DefaultLocale", "SimpleDateFormat"})
//    private void downloadArchive(ReadingData readingData, int i, MyDatabase myDatabase) {
//        String time = (new SimpleDateFormat(activity
//                .getString(R.string.save_format_name))).format(new Date())
//                .concat(String.valueOf(new Random().nextInt(1000)));
//        String query = "CREATE TABLE %s AS %s;";
//        String query1 = String.format(query, "TrackingDto_".concat(time), String
//                .format("SELECT * FROM TrackingDto WHERE trackNumber = %d AND isArchive = 1"
//                        , readingData.trackingDtos.get(i).trackNumber));
//        String query2 = String.format(query, "OnOffLoadDto_".concat(time), String
//                .format("SELECT * FROM OnOffLoadDto WHERE trackNumber = %d"
//                        , readingData.trackingDtos.get(i).trackNumber));
//        Cursor cursor = myDatabase.getOpenHelper().getWritableDatabase().query(query1);
//        cursor.moveToFirst();
//        cursor = myDatabase.getOpenHelper().getWritableDatabase().query(query2);
//        cursor.moveToFirst();
//        myDatabase.trackingDao().deleteTrackingDto(readingData.trackingDtos.get(i).trackNumber, true);
//        myDatabase.onOffLoadDao().deleteOnOffLoad(readingData.trackingDtos.get(i).trackNumber);
//    }
}

class DownloadIncomplete implements ICallbackIncomplete<ReadingData> {
    @Override
    public void executeIncomplete(Response<ReadingData> response) {
        CustomErrorHandling customErrorHandling = new CustomErrorHandling(MyApplication.getContext());
        String error = customErrorHandling.getErrorMessageDefault(response);
        if (response.code() == 400) {
            CustomErrorHandling.APIError apiError = customErrorHandling.parseError(response);
            error = apiError.message();
        }
        new CustomToast().warning(error, Toast.LENGTH_LONG);
    }
}

class DownloadError implements ICallbackError {
    @Override
    public void executeError(Throwable t) {
        CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(MyApplication.getContext());
        String error = customErrorHandlingNew.getErrorMessageTotal(t);
        new CustomToast().error(error, Toast.LENGTH_LONG);
    }
}