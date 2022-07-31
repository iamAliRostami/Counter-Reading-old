package com.leon.counter_reading.utils.downloading;

import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.fragments.ExplorerFragment;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.uploading.CopyTaskParam;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class CopyTask extends AsyncTask<CopyTaskParam, Integer, Void> {
    private final CustomProgressModel progress;
    private final ExplorerFragment parent;
    private CopyTaskParam param;

    public CopyTask(Activity activity, ExplorerFragment fragment) {
        super();
        progress = getApplicationComponent().CustomProgressModel();
        progress.show(activity, false);
        this.parent = fragment;
    }

    @Override
    protected Void doInBackground(CopyTaskParam... params) {
        final ByteBuffer buffer = ByteBuffer.allocate(4096);
        param = params[0];
        long length = params[0].from.getLength();
        try {
            if (!param.to.exists()) if (!param.to.createNewFile()) return null;
            final FileOutputStream out = new FileOutputStream(param.to);
            for (long i = 0; i < length; i += buffer.limit()) {
                if (!isCancelled()) {
                    buffer.limit((int) Math.min(buffer.capacity(), length - i));
                    params[0].from.read(i, buffer);
                    out.write(buffer.array(), 0, buffer.limit());
                    publishProgress((int) i);
                    buffer.clear();
                }
            }
            out.close();
        } catch (IOException e) {
            new CustomToast().warning(e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        progress.getDialog().dismiss();
        parent.launchIntent(param.to);
        super.onPostExecute(result);
    }
}
