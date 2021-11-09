package com.leon.counter_reading.utils.backup_restore;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.MyDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackUp extends AsyncTask<Activity, Integer, Void> {
    private final CustomProgressModel customProgressModel;

    public BackUp(Activity activity) {
        super();
        customProgressModel = MyApplication.getApplicationComponent().CustomProgressModel();
        customProgressModel.show(activity, false);
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
    @SuppressLint("SimpleDateFormat")
    protected Void doInBackground(Activity... activities) {
        MyDatabase myDatabase = MyApplication.getApplicationComponent().MyDatabase();
        Cursor cursor = myDatabase.getOpenHelper().getWritableDatabase().query("SELECT name FROM sqlite_master WHERE type='table';");

        String timeStamp = "/_" + (new SimpleDateFormat(
                activities[0].getString(R.string.save_format_name_melli))).format(new Date());
        MyApplication.getApplicationComponent().SharedPreferenceModel().putData(SharedReferenceKeys.LAST_BACK_UP.getValue(), timeStamp);
        while (cursor.moveToNext()) {
            if (!exportDatabaseToCSVFile(cursor.getString(0), timeStamp, activities[0]))
                break;
        }
//        exportDatabaseToCSVFile("ReadingConfigDefaultDto", activities[0]);
//        exportDatabaseToCSVFile("TrackingDto", activities[0]);
//        exportDatabaseToCSVFile("OnOffLoadDto", activities[0]);
//        exportDatabaseToCSVFile("CounterStateDto", activities[0]);
//        exportDatabaseToCSVFile("QotrDictionary", activities[0]);
//        exportDatabaseToCSVFile("KarbariDto", activities[0]);
//        exportDatabaseToCSVFile("CounterReportDto", activities[0]);
//        exportDatabaseToCSVFile("OffLoadReport", activities[0]);
//        exportDatabaseToCSVFile("ForbiddenDto", activities[0]);
        return null;
    }

    @SuppressLint("SimpleDateFormat")
    public static boolean exportDatabaseToCSVFile(String tableName, String child, Activity activity) {
        File exportDir = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + child);
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File file = new File(exportDir, tableName + "_" + BuildConfig.BUILD_TYPE + "_"
                + BuildConfig.VERSION_CODE + ".csv");

        try {
            if (file.exists()) file.delete();
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            Cursor curCSV = MyApplication.getApplicationComponent().MyDatabase()
                    .query("SELECT * FROM " + tableName, null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                //Which column you want to export
                String[] arrStr = new String[curCSV.getColumnCount()];
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
}
