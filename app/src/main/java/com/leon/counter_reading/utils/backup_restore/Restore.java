package com.leon.counter_reading.utils.backup_restore;

import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.tables.CounterReportDto;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.ForbiddenDto;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OffLoadReport;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.QotrDictionary;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.CustomToast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Restore extends AsyncTask<Activity, Integer, Void> {
    private final CustomProgressModel customProgressModel;
    private final ArrayList<String> trackIds = new ArrayList<>();
    private final ArrayList<Integer> trackNumbers = new ArrayList<>();

    public Restore(Activity activity) {
        super();
        customProgressModel = getApplicationComponent().CustomProgressModel();
        customProgressModel.show(activity, false);
    }

    public static ArrayList<String> importTableFromCSVFile(String tableName, Activity activity) {
        File importDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                getApplicationComponent().SharedPreferenceModel()
                        .getStringData(SharedReferenceKeys.LAST_BACK_UP.getValue()));
        CSVReader csvReader;
        ArrayList<String> value = new ArrayList<>();
        try {
            csvReader = new CSVReader(new FileReader(importDir + "/" + tableName + "_"
                    + BuildConfig.BUILD_TYPE + "_" + BuildConfig.VERSION_CODE + ".csv"));
            String[] nextLine;
            String[] headerLine = null;
            while ((nextLine = csvReader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                if (headerLine == null) {
                    headerLine = nextLine;
                } else {
                    StringBuilder columns = new StringBuilder();
                    columns.append("{");
                    for (int i = 0; i < nextLine.length - 1; i++) {
                        columns.append("\"").append(headerLine[i]).append("\":\"");
                        if (i == nextLine.length - 2)
                            columns.append(nextLine[i]);
                        else {
                            columns.append(nextLine[i]).append("\",");
                        }
                    }
                    columns.append("\"}");
                    value.add(String.valueOf(columns));
                }
            }
            activity.runOnUiThread(() ->
                    new CustomToast().success("?????????????? ?????????????? ???? ???????????? ?????????? ????.", Toast.LENGTH_SHORT));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            activity.runOnUiThread(() ->
                    new CustomToast().error("?????? ?????????????? ??????????????\n ???????? ?? ???????????? ???????????? ?????? ???? ?????????? ????????.", Toast.LENGTH_SHORT));
        } catch (IOException e) {
            e.printStackTrace();
            activity.runOnUiThread(() ->
                    new CustomToast().error("?????? ???? ?????????????? ??????????????.\n".concat("?????? ??????: ")
                            .concat(e.toString()), Toast.LENGTH_LONG));

        }
        return value;
    }

    public static int getIntFromString(String intString) {
        if (intString == null || intString.isEmpty())
            return 0;
        else return Integer.parseInt(intString);
    }

    public static double getDoubleFromString(String doubleString) {
        if (doubleString == null || doubleString.isEmpty())
            return 0;
        else return Double.parseDouble(doubleString);
    }

    public static ArrayList<String> importDatabaseFromCSVFile(String tableName, Activity activity) {
        File importDir = new File(String.valueOf(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
        CSVReader csvReader;
        ArrayList<String> value = new ArrayList<>();
        try {
            csvReader = new CSVReader(new FileReader(importDir + "/" + tableName + "_" + BuildConfig.BUILD_TYPE + ".csv"));
            String[] nextLine;
            String[] headerLine = null;
            while ((nextLine = csvReader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                if (headerLine == null) {
                    headerLine = nextLine;
                } else {
                    StringBuilder columns = new StringBuilder();
                    columns.append("{");
                    for (int i = 0; i < nextLine.length - 1; i++) {
                        columns.append("\"").append(headerLine[i]).append("\":");
                        if (i == nextLine.length - 2)
                            columns.append(nextLine[i]);
                        else {
                            columns.append(nextLine[i]).append(",");
                        }
                    }
                    columns.append("}");
                    value.add(String.valueOf(columns));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            activity.runOnUiThread(() ->
                    new CustomToast().error("?????? ???? ?????????????? ??????????????.\n".concat("?????? ??????: ")
                            .concat(e.toString()), Toast.LENGTH_LONG));

        }
        return value;
    }

    public static void importDatabaseFromCSVFileSample(String tableName, Activity activity) {
        File importDir = new File(String.valueOf(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
        CSVReader csvReader;
        try {
            csvReader = new CSVReader(new FileReader(importDir + "/" + tableName + "_" + BuildConfig.BUILD_TYPE + ".csv"));
            String[] nextLine;
            String[] headerLine = null;
            StringBuilder value = new StringBuilder();
            value.append("[");
            while ((nextLine = csvReader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                if (headerLine == null) {
                    headerLine = nextLine;
                } else {
                    StringBuilder columns = new StringBuilder();
                    columns.append("{");
                    for (int i = 0; i < nextLine.length - 1; i++) {
                        columns.append("\"").append(headerLine[i]).append("\":\"");
                        if (i == nextLine.length - 2)
                            columns.append(nextLine[i]);
                        else {
                            columns.append(nextLine[i]).append("\",");
                        }
                    }
                    columns.append("\"}");
                    Log.e("columns", String.valueOf(columns));
                    Gson gson = new Gson();
                    OnOffLoadDtoTemp temp = gson.fromJson(String.valueOf(columns), OnOffLoadDtoTemp.class);
                    OnOffLoadDto table = temp.getOnOffLoadDto();
                    value.append(columns).append(",");
                }
            }
            try {
                value.replace(value.lastIndexOf(","), value.lastIndexOf(",") + 1, "]");
            } catch (Exception e) {
                value.append("]");
                e.printStackTrace();
            }
            Log.e("value", String.valueOf(value));
            activity.runOnUiThread(() ->
                    new CustomToast().success("?????????????? ?????????????? ???? ???????????? ?????????? ????.", Toast.LENGTH_LONG));
        } catch (IOException e) {
            e.printStackTrace();
            activity.runOnUiThread(() ->
                    new CustomToast().error("?????? ???? ?????????????? ??????????????.\n".concat("?????? ??????: ")
                            .concat(e.toString()), Toast.LENGTH_LONG));

        }
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        customProgressModel.getDialog().dismiss();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        if (restoreTrackingDto(activities[0]).size() > 0) {
            if (restoreOnOffLoadDto(activities[0]).size() > 0) {
                restoreReadingConfigDefaultDto(activities[0]);
                restoreCounterStateDto(activities[0]);
                restoreQotrDictionary(activities[0]);
                restoreKarbariDto(activities[0]);
                restoreCounterReportDto(activities[0]);
                restoreOffLoadReport(activities[0]);
                restoreForbiddenDto(activities[0]);
            }
        }
        return null;
    }

    private void restoreForbiddenDto(Activity activity) {
        ArrayList<String> forbiddenDtoString = importTableFromCSVFile("OffLoadReport", activity);
        ArrayList<ForbiddenDto> forbiddenDtos = new ArrayList<>();
        Gson gson = new Gson();
        for (int i = 0; i < forbiddenDtoString.size(); i++) {
            ForbiddenDtoTemp forbiddenDtoTemp = gson
                    .fromJson(forbiddenDtoString.get(i), ForbiddenDtoTemp.class);
            forbiddenDtos.add(forbiddenDtoTemp.getForbiddenDto());
        }
        getApplicationComponent().MyDatabase().forbiddenDao().insertForbiddenDto(forbiddenDtos);
    }

    private void restoreOffLoadReport(Activity activity) {
        ArrayList<String> offLoadReportString = importTableFromCSVFile("OffLoadReport", activity);
        ArrayList<OffLoadReport> offLoadReports = new ArrayList<>();
        Gson gson = new Gson();
        for (int i = 0; i < offLoadReportString.size(); i++) {
            OffLoadReportTemp offLoadReportTemp = gson
                    .fromJson(offLoadReportString.get(i), OffLoadReportTemp.class);
            offLoadReports.add(offLoadReportTemp.getOffLoadReport());
        }
        getApplicationComponent().MyDatabase().offLoadReportDao().insertOffLoadReport(offLoadReports);
    }

    private void restoreKarbariDto(Activity activity) {
        ArrayList<String> karbariDtoString = importTableFromCSVFile("KarbariDto", activity);
        ArrayList<KarbariDto> karbariDtos = new ArrayList<>();
        Gson gson = new Gson();
        for (int i = 0; i < karbariDtoString.size(); i++) {
            KarbariDtoTemp karbariDtoTemp = gson
                    .fromJson(karbariDtoString.get(i), KarbariDtoTemp.class);
            karbariDtos.add(karbariDtoTemp.getKarbariDto());
        }
        Log.e("size", String.valueOf(karbariDtos.size()));
        getApplicationComponent().MyDatabase().karbariDao().insertAllKarbariDtos(karbariDtos);
    }

    private void restoreCounterReportDto(Activity activity) {
        ArrayList<String> counterReportDtoString = importTableFromCSVFile("CounterReportDto", activity);
        ArrayList<CounterReportDto> counterReportDtos = new ArrayList<>();
        Gson gson = new Gson();
        for (int i = 0; i < counterReportDtoString.size(); i++) {
            CounterReportDtoTemp counterReportDtoTemp = gson
                    .fromJson(counterReportDtoString.get(i), CounterReportDtoTemp.class);
            counterReportDtos.add(counterReportDtoTemp.getCounterReportDto());
        }
        Log.e("size", String.valueOf(counterReportDtos.size()));
        getApplicationComponent().MyDatabase().counterReportDao().insertAllCounterStateReport(counterReportDtos);
    }

    private void restoreReadingConfigDefaultDto(Activity activity) {
        ArrayList<String> readingConfigDefaultDtoString = importTableFromCSVFile("ReadingConfigDefaultDto", activity);
        ArrayList<ReadingConfigDefaultDto> readingConfigDefaultDtos = new ArrayList<>();
        Gson gson = new Gson();
        for (int i = 0; i < readingConfigDefaultDtoString.size(); i++) {
            ReadingConfigDefaultDtoTemp readingConfigDefaultDtoTemp = gson
                    .fromJson(readingConfigDefaultDtoString.get(i), ReadingConfigDefaultDtoTemp.class);
            readingConfigDefaultDtos.add(readingConfigDefaultDtoTemp.getReadingConfigDto());
        }
        Log.e("size", String.valueOf(readingConfigDefaultDtos.size()));
        getApplicationComponent().MyDatabase().readingConfigDefaultDao().insertAllReadingConfigDefault(readingConfigDefaultDtos);
    }

    private ArrayList<OnOffLoadDto> restoreOnOffLoadDto(Activity activity) {
        ArrayList<String> onOffLoadDtoString = importTableFromCSVFile("OnOffLoadDto", activity);
        ArrayList<OnOffLoadDto> onOffLoadDtos = new ArrayList<>();
//        ArrayList<OnOffLoadDto> onOffLoadDtosTemp = new ArrayList<>(MyApplication.getApplicationComponent().MyDatabase().onOffLoadDao().getAllOnOffLoad());
        Gson gson = new Gson();
        for (int i = 0; i < onOffLoadDtoString.size(); i++) {
            boolean found = false;
            OnOffLoadDtoTemp onOffLoadDtoTemp = gson
                    .fromJson(onOffLoadDtoString.get(i), OnOffLoadDtoTemp.class);
            for (int j = 0; j < trackIds.size() && !found; j++) {
                if (trackIds.get(j).equals(onOffLoadDtoTemp.id) && trackNumbers.get(j) == onOffLoadDtoTemp.trackNumber) {
                    found = true;
                }
            }
            if (!found)
                onOffLoadDtos.add(onOffLoadDtoTemp.getOnOffLoadDto());
        }
        Log.e("size", String.valueOf(onOffLoadDtos.size()));
        getApplicationComponent().MyDatabase().onOffLoadDao().insertAllOnOffLoad(onOffLoadDtos);
        return onOffLoadDtos;
    }

    private ArrayList<TrackingDto> restoreTrackingDto(Activity activity) {
        ArrayList<String> trackingDtoString = importTableFromCSVFile("TrackingDto", activity);
        ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
        ArrayList<TrackingDto> trackingDtosTemp = new ArrayList<>(getApplicationComponent().MyDatabase().trackingDao().getTrackingDto());
        Gson gson = new Gson();
        for (int i = 0; i < trackingDtoString.size(); i++) {
            boolean found = false;
            TrackingDtoTemp trackingDtoTemp = gson
                    .fromJson(trackingDtoString.get(i), TrackingDtoTemp.class);
            for (int j = 0; j < trackingDtosTemp.size() && !found; j++) {
                if (trackingDtoTemp.trackNumber == trackingDtosTemp.get(j).trackNumber &&
                        trackingDtoTemp.id.equals(trackingDtosTemp.get(j).id)) {
                    found = true;
                    trackIds.add(trackingDtoTemp.id);
                    trackNumbers.add(trackingDtoTemp.trackNumber);
                }
            }
            if (!found) {
                trackingDtos.add(trackingDtoTemp.getTrackingDto());
            }
        }
        getApplicationComponent().MyDatabase().trackingDao().insertAllTrackingDtos(trackingDtos);
        return trackingDtos;
    }

    private void restoreCounterStateDto(Activity activity) {
        ArrayList<String> counterStateDtoString = importTableFromCSVFile("CounterStateDto", activity);
        ArrayList<CounterStateDto> counterStateDtos = new ArrayList<>();
        Gson gson = new Gson();
        for (int i = 0; i < counterStateDtoString.size(); i++) {
            CounterStateDtoTemp counterStateDtoTemp = gson
                    .fromJson(counterStateDtoString.get(i), CounterStateDtoTemp.class);
            counterStateDtos.add(counterStateDtoTemp.getCounterStateDto());
        }
        Log.e("size", String.valueOf(counterStateDtos.size()));
        getApplicationComponent().MyDatabase().counterStateDao().insertAllCounterStateDto(counterStateDtos);
    }

    private void restoreQotrDictionary(Activity activity) {
        ArrayList<String> qotrDictionaryString = importTableFromCSVFile("QotrDictionary", activity);
        ArrayList<QotrDictionary> qotrDictionaries = new ArrayList<>();
        Gson gson = new Gson();
        for (int i = 0; i < qotrDictionaryString.size(); i++) {
            QotrDictionaryTemp qotrDictionaryTemp = gson
                    .fromJson(qotrDictionaryString.get(i), QotrDictionaryTemp.class);
            qotrDictionaries.add(qotrDictionaryTemp.getQotrDictionary());
        }
        Log.e("size", String.valueOf(qotrDictionaries.size()));
        getApplicationComponent().MyDatabase().qotrDictionaryDao().insertQotrDictionaries(qotrDictionaries);
    }

}
