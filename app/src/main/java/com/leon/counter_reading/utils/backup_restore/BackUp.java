package com.leon.counter_reading.utils.backup_restore;

import static com.leon.counter_reading.enums.SharedReferenceKeys.LAST_BACK_UP;
import static com.leon.counter_reading.helpers.Constants.OnOffLoadDtoTableName;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.MyDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackUp extends AsyncTask<Activity, Integer, Void> {
    private final CustomProgressModel progress;

    public BackUp(Activity activity) {
        super();
        progress = getApplicationComponent().CustomProgressModel();
        progress.show(activity, false);
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    @SuppressLint("SimpleDateFormat")
    protected Void doInBackground(Activity... activities) {
        final MyDatabase myDatabase = getApplicationComponent().MyDatabase();
        final Cursor cursor = myDatabase.getOpenHelper().getWritableDatabase()
                .query("SELECT name FROM sqlite_master WHERE type='table';");
        final String timeStamp = "/_" + (new SimpleDateFormat(activities[0].getString(R.string.save_format_name_melli)))
                .format(new Date());
        getApplicationComponent().SharedPreferenceModel().putData(LAST_BACK_UP.getValue(), timeStamp);
        while (cursor.moveToNext()) {
            if (!exportDatabaseToCSV(cursor.getString(0), timeStamp, activities[0]))
                break;
            if (cursor.getString(0).equals(OnOffLoadDtoTableName)
                    && !exportOnOffloadSummaryToCSV(timeStamp, activities[0]))
                break;
        }
        return null;
    }

    @SuppressLint("SimpleDateFormat")
    public static boolean exportDatabaseToCSV(String tableName, String child, Activity activity) {
        final File exportDir = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + child);
        if (!exportDir.exists()) if (!exportDir.mkdirs()) return false;
        final File file = new File(exportDir, tableName + "_" + BuildConfig.BUILD_TYPE + "_"
                + BuildConfig.VERSION_CODE + ".csv");
        try {
            if (file.exists()) if (!file.delete()) return false;
            if (!file.createNewFile()) return false;
            final CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            final Cursor curCSV = getApplicationComponent().MyDatabase()
                    .query("SELECT * FROM " + tableName, null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                //Which column you want to export
                final String[] arrStr = new String[curCSV.getColumnCount()];
                for (int i = 0; i < curCSV.getColumnCount() - 1; i++) {
                    if (!curCSV.getColumnName(i).equals("preNumber") && !curCSV.getColumnName(i).equals("customId")) {
                        arrStr[i] = curCSV.getString(i);
                    }
                }
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            activity.runOnUiThread(() ->
                    new CustomToast().success("پشتیبان گیری با موفقیت انجام شد.\n".concat("محل ذخیره سازی: ")
                            .concat(exportDir.getAbsolutePath()), Toast.LENGTH_SHORT));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            activity.runOnUiThread(() ->
                    new CustomToast().error("خطا در پشتیبان گیری\n پوشه ی دانلود دستگاه خود را تخلیه کنید.", Toast.LENGTH_SHORT));
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            activity.runOnUiThread(() ->
                    new CustomToast().error("خطا در پشتیبان گیری.\n".concat("علت خطا: ")
                            .concat(e.toString()), Toast.LENGTH_SHORT));
            return false;
        }
        return true;
    }


    @SuppressLint("SimpleDateFormat")
    public static boolean exportOnOffloadSummaryToCSV(String child, Activity activity) {
        final File exportDir = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + child);
        if (!exportDir.exists()) if (!exportDir.mkdirs()) return false;
        final File file = new File(exportDir, OnOffLoadDtoTableName + "Summary_" + BuildConfig.BUILD_TYPE + "_"
                + BuildConfig.VERSION_CODE + ".csv");
        try {
            if (file.exists()) if (!file.delete()) return false;
            if (!file.createNewFile()) return false;

            final CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            final Cursor curCSV = getApplicationComponent().MyDatabase()
                    .query("SELECT * FROM " + OnOffLoadDtoTableName, null);

            csvWrite.writeNext(new String[]{"BillId", "Radif", "Eshterak", "QeraatCode",
                    "PreDate", "TrackNumber", "CounterNumber", "CounterStateId"});
            while (curCSV.moveToNext()) {
                //Which column you want to export
//                final String[] arrStr = new String[curCSV.getColumnCount()];
                final String[] arrStr = new String[8];
                int j = 0;
                for (int i = 0; i < curCSV.getColumnCount() - 1; i++) {
                    //TODO
                    if (curCSV.getColumnName(i).equals("billId") ||
                            curCSV.getColumnName(i).equals("radif") ||
                            curCSV.getColumnName(i).equals("eshterak") ||
                            curCSV.getColumnName(i).equals("qeraatCode") ||
                            curCSV.getColumnName(i).equals("preDate") ||
                            curCSV.getColumnName(i).equals("trackNumber") ||
                            curCSV.getColumnName(i).equals("counterNumber") ||
                            curCSV.getColumnName(i).equals("counterStateId")) {
                        arrStr[j] = curCSV.getString(i);
                        j++;
                    }
                }
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            activity.runOnUiThread(() ->
                    new CustomToast().success("پشتیبان گیری با موفقیت انجام شد.\n".concat("محل ذخیره سازی: ")
                            .concat(exportDir.getAbsolutePath()), Toast.LENGTH_SHORT));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            activity.runOnUiThread(() ->
                    new CustomToast().error("خطا در پشتیبان گیری\n پوشه ی دانلود دستگاه خود را تخلیه کنید.", Toast.LENGTH_SHORT));
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            activity.runOnUiThread(() ->
                    new CustomToast().error("خطا در پشتیبان گیری.\n".concat("علت خطا: ")
                            .concat(e.toString()), Toast.LENGTH_SHORT));
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        progress.getDialog().dismiss();
    }
}