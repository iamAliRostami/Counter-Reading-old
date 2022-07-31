package com.leon.counter_reading.utils.backup_restore;

import static com.leon.counter_reading.enums.SharedReferenceKeys.LAST_BACK_UP;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
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
import com.leon.counter_reading.utils.backup_restore.model.CounterReportDtoTemp;
import com.leon.counter_reading.utils.backup_restore.model.CounterStateDtoTemp;
import com.leon.counter_reading.utils.backup_restore.model.ForbiddenDtoTemp;
import com.leon.counter_reading.utils.backup_restore.model.KarbariDtoTemp;
import com.leon.counter_reading.utils.backup_restore.model.OffLoadReportTemp;
import com.leon.counter_reading.utils.backup_restore.model.OnOffLoadDtoTemp;
import com.leon.counter_reading.utils.backup_restore.model.QotrDictionaryTemp;
import com.leon.counter_reading.utils.backup_restore.model.ReadingConfigDefaultDtoTemp;
import com.leon.counter_reading.utils.backup_restore.model.TrackingDtoTemp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Restore extends AsyncTask<Activity, Integer, Void> {
    private final CustomProgressModel progress;
    private final ArrayList<String> trackIds = new ArrayList<>();
    private final ArrayList<Integer> trackNumbers = new ArrayList<>();

    public Restore(Activity activity) {
        super();
        progress = getApplicationComponent().CustomProgressModel();
        progress.show(activity, false);
    }

    public static ArrayList<String> importTableFromCSV(String tableName, Activity activity) {
        final File importDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                getApplicationComponent().SharedPreferenceModel().getStringData(LAST_BACK_UP.getValue()));
        CSVReader csvReader;
        final ArrayList<String> value = new ArrayList<>();
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
                    new CustomToast().success("بازیابی اطلاعات با موفقیت انجام شد.", Toast.LENGTH_SHORT));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            activity.runOnUiThread(() ->
                    new CustomToast().error("خطا بازیابی اطلاعات\n پوشه ی دانلود دستگاه خود را تخلیه کنید.", Toast.LENGTH_SHORT));
        } catch (IOException e) {
            e.printStackTrace();
            activity.runOnUiThread(() ->
                    new CustomToast().error("خطا در بازیابی اطلاعات.\n".concat("علت خطا: ")
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
                    new CustomToast().error("خطا در بازیابی اطلاعات.\n".concat("علت خطا: ")
                            .concat(e.toString()), Toast.LENGTH_LONG));

        }
        return value;
    }

    public static void importDatabaseFromCSVFileSample(String tableName, Activity activity) {
        final File importDir = new File(String.valueOf(Environment
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
            activity.runOnUiThread(() ->
                    new CustomToast().success("بازیابی اطلاعات با موفقیت انجام شد.", Toast.LENGTH_LONG));
        } catch (IOException e) {
            e.printStackTrace();
            activity.runOnUiThread(() ->
                    new CustomToast().error("خطا در بازیابی اطلاعات.\n".concat("علت خطا: ")
                            .concat(e.toString()), Toast.LENGTH_LONG));

        }
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        progress.getDialog().dismiss();
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
        final ArrayList<String> forbiddenDtoString = importTableFromCSV("OffLoadReport", activity);
        final ArrayList<ForbiddenDto> forbiddenDtos = new ArrayList<>();
        final Gson gson = new Gson();
        for (int i = 0; i < forbiddenDtoString.size(); i++) {
            final ForbiddenDtoTemp forbiddenDtoTemp = gson.fromJson(forbiddenDtoString.get(i),
                    ForbiddenDtoTemp.class);
            forbiddenDtos.add(forbiddenDtoTemp.getForbiddenDto());
        }
        getApplicationComponent().MyDatabase().forbiddenDao().insertForbiddenDto(forbiddenDtos);
    }

    private void restoreOffLoadReport(Activity activity) {
        final ArrayList<String> offLoadReportString = importTableFromCSV("OffLoadReport", activity);
        final ArrayList<OffLoadReport> offLoadReports = new ArrayList<>();
        final Gson gson = new Gson();
        for (int i = 0; i < offLoadReportString.size(); i++) {
            final OffLoadReportTemp offLoadReportTemp = gson.fromJson(offLoadReportString.get(i),
                    OffLoadReportTemp.class);
            offLoadReports.add(offLoadReportTemp.getOffLoadReport());
        }
        getApplicationComponent().MyDatabase().offLoadReportDao().insertOffLoadReport(offLoadReports);
    }

    private void restoreKarbariDto(Activity activity) {
        final ArrayList<String> karbariDtoString = importTableFromCSV("KarbariDto", activity);
        final ArrayList<KarbariDto> karbariDtos = new ArrayList<>();
        final Gson gson = new Gson();
        for (int i = 0; i < karbariDtoString.size(); i++) {
            final KarbariDtoTemp karbariDtoTemp = gson
                    .fromJson(karbariDtoString.get(i), KarbariDtoTemp.class);
            karbariDtos.add(karbariDtoTemp.getKarbariDto());
        }
        getApplicationComponent().MyDatabase().karbariDao().insertAllKarbariDtos(karbariDtos);
    }

    private void restoreCounterReportDto(Activity activity) {
        final ArrayList<String> counterReportDtoString = importTableFromCSV("CounterReportDto", activity);
        final ArrayList<CounterReportDto> counterReportDtos = new ArrayList<>();
        final Gson gson = new Gson();
        for (int i = 0; i < counterReportDtoString.size(); i++) {
            final CounterReportDtoTemp counterReportDtoTemp = gson
                    .fromJson(counterReportDtoString.get(i), CounterReportDtoTemp.class);
            counterReportDtos.add(counterReportDtoTemp.getCounterReportDto());
        }
        getApplicationComponent().MyDatabase().counterReportDao().insertAllCounterStateReport(counterReportDtos);
    }

    private void restoreReadingConfigDefaultDto(Activity activity) {
        final ArrayList<String> readingConfigDefaultDtoString = importTableFromCSV("ReadingConfigDefaultDto", activity);
        final ArrayList<ReadingConfigDefaultDto> readingConfigDefaultDtos = new ArrayList<>();
        final Gson gson = new Gson();
        for (int i = 0; i < readingConfigDefaultDtoString.size(); i++) {
            final ReadingConfigDefaultDtoTemp readingConfigDefaultDtoTemp = gson
                    .fromJson(readingConfigDefaultDtoString.get(i), ReadingConfigDefaultDtoTemp.class);
            readingConfigDefaultDtos.add(readingConfigDefaultDtoTemp.getReadingConfigDto());
        }
        getApplicationComponent().MyDatabase().readingConfigDefaultDao().insertAllReadingConfigDefault(readingConfigDefaultDtos);
    }

    private ArrayList<OnOffLoadDto> restoreOnOffLoadDto(Activity activity) {
        final ArrayList<String> onOffLoadDtoString = importTableFromCSV("OnOffLoadDto", activity);
        final ArrayList<OnOffLoadDto> onOffLoadDtos = new ArrayList<>();
        final Gson gson = new Gson();
        for (int i = 0; i < onOffLoadDtoString.size(); i++) {
            boolean found = false;
            final OnOffLoadDtoTemp onOffLoadDtoTemp = gson
                    .fromJson(onOffLoadDtoString.get(i), OnOffLoadDtoTemp.class);
            for (int j = 0; j < trackIds.size() && !found; j++) {
                if (trackIds.get(j).equals(onOffLoadDtoTemp.id) && trackNumbers.get(j) == onOffLoadDtoTemp.trackNumber) {
                    found = true;
                }
            }
            if (!found)
                onOffLoadDtos.add(onOffLoadDtoTemp.getOnOffLoadDto());
        }
        getApplicationComponent().MyDatabase().onOffLoadDao().insertAllOnOffLoad(onOffLoadDtos);
        return onOffLoadDtos;
    }

    private ArrayList<TrackingDto> restoreTrackingDto(Activity activity) {
        final ArrayList<String> trackingDtoString = importTableFromCSV("TrackingDto", activity);
        final ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
        final ArrayList<TrackingDto> trackingDtosTemp = new ArrayList<>(getApplicationComponent().MyDatabase().trackingDao().getTrackingDto());
        final Gson gson = new Gson();
        for (int i = 0; i < trackingDtoString.size(); i++) {
            boolean found = false;
            final TrackingDtoTemp trackingDtoTemp = gson.fromJson(trackingDtoString.get(i),
                    TrackingDtoTemp.class);
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
        final ArrayList<String> counterStateDtoString = importTableFromCSV("CounterStateDto", activity);
        final ArrayList<CounterStateDto> counterStateDtos = new ArrayList<>();
        final Gson gson = new Gson();
        for (int i = 0; i < counterStateDtoString.size(); i++) {
            final CounterStateDtoTemp counterStateDtoTemp = gson
                    .fromJson(counterStateDtoString.get(i), CounterStateDtoTemp.class);
            counterStateDtos.add(counterStateDtoTemp.getCounterStateDto());
        }
        getApplicationComponent().MyDatabase().counterStateDao().insertAllCounterStateDto(counterStateDtos);
    }

    private void restoreQotrDictionary(Activity activity) {
        final ArrayList<String> qotrDictionaryString = importTableFromCSV("QotrDictionary", activity);
        final ArrayList<QotrDictionary> qotrDictionaries = new ArrayList<>();
        final Gson gson = new Gson();
        for (int i = 0; i < qotrDictionaryString.size(); i++) {
            final QotrDictionaryTemp qotrDictionaryTemp = gson.fromJson(qotrDictionaryString.get(i),
                    QotrDictionaryTemp.class);
            qotrDictionaries.add(qotrDictionaryTemp.getQotrDictionary());
        }
        getApplicationComponent().MyDatabase().qotrDictionaryDao().insertQotrDictionaries(qotrDictionaries);
    }

}
