package com.leon.counter_reading.utils.uploading;

import static com.leon.counter_reading.enums.OffloadStateEnum.INSERTED;
import static com.leon.counter_reading.enums.OffloadStateEnum.SENT;
import static com.leon.counter_reading.helpers.Constants.ZIP_ROOT;
import static com.leon.counter_reading.helpers.Constants.zipAddress;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.utils.CalendarTool.getDate;
import static com.leon.counter_reading.utils.OfflineUtils.writeOnSdCard;
import static com.leon.counter_reading.utils.OfflineUtils.zipFileAtPath;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.tables.ForbiddenDto;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.tables.OffLoadReport;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.Voice;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.utils.CustomToast;

import java.io.File;
import java.util.ArrayList;

public class PrepareOffLoadOffline extends AsyncTask<Activity, Activity, Activity> {
    private final ArrayList<OnOffLoadDto> onOffLoadDtos = new ArrayList<>();
    private final ArrayList<OffLoadReport> offLoadReports = new ArrayList<>();
    private final ArrayList<ForbiddenDto> forbiddenDtos = new ArrayList<>();
    private final CustomProgressModel progress;
    private final int trackNumber;
    private final String id;

    public PrepareOffLoadOffline(Activity activity, int trackNumber, String id) {
        super();
        progress = getApplicationComponent().CustomProgressModel();
        progress.show(activity, false);
        this.trackNumber = trackNumber;
        this.id = id;
    }

    @SuppressLint({"DefaultLocale", "SimpleDateFormat"})
    @Override
    protected Activity doInBackground(Activity... activities) {
        forbiddenDtos.clear();
        forbiddenDtos.addAll(getApplicationComponent().MyDatabase().forbiddenDao()
                .getAllForbiddenDto(false));
        if (forbiddenDtos.size() > 0) uploadForbid(activities[0]);
        onOffLoadDtos.clear();
        onOffLoadDtos.addAll(getApplicationComponent().MyDatabase().onOffLoadDao()
                .getOnOffLoadReadByTrackingAndOffLoad(id, INSERTED.getValue()));
        offLoadReports.clear();
        offLoadReports.addAll(getApplicationComponent().MyDatabase().offLoadReportDao()
                .getAllOffLoadReport(false));
        if (uploadOffLoad(activities[0])) {
            uploadImages(activities[0]);
            uploadVoices(activities[0]);
            if (zipFileAtPath(trackNumber)) {
                final Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.setType("application/zip");
                final File file = new File(zipAddress);
                intent.putExtra(Intent.EXTRA_TITLE, file.getName());
                activities[0].startActivityForResult(intent, ZIP_ROOT);
                //TODO after zip and sent
                for (int i = 0; i < onOffLoadDtos.size(); i++)
                    onOffLoadDtos.get(i).offLoadStateId = SENT.getValue();
                getApplicationComponent().MyDatabase().onOffLoadDao().updateOnOffLoads(onOffLoadDtos);
                getApplicationComponent().MyDatabase().trackingDao().updateTrackingDtoByArchive(id,
                        true, false, getDate(activities[0]));
                for (int i = 0; i < offLoadReports.size(); i++) offLoadReports.get(i).isSent = true;
                getApplicationComponent().MyDatabase().offLoadReportDao().updateOffLoadReport(offLoadReports);
                activities[0].runOnUiThread(() -> {
                    final String message = String.format("تعداد %d اشتراک با موفقیت بارگذاری شد.", onOffLoadDtos.size());
                    new CustomToast().success(message, Toast.LENGTH_LONG);
                });
            }
        }
        return activities[0];
    }

    @Override
    protected void onPostExecute(Activity activity) {
        super.onPostExecute(activity);
        progress.getDialog().dismiss();
    }

    private void uploadImages(Activity activity) {
        final ArrayList<Image> images = new ArrayList<>(getApplicationComponent().MyDatabase().
                imageDao().getImagesByBySentAndTrack(trackNumber, false));
        if (images.size() > 0) {
            final String onOffLoadDtoString = new Gson().toJson(images);
            writeOnSdCard(onOffLoadDtoString, "images", trackNumber);
            if (CustomFile.copyImages(images, trackNumber, activity)) {
                for (int i = 0; i < images.size(); i++) {
                    images.get(i).isSent = true;
                }
                getApplicationComponent().MyDatabase().imageDao().updateImage(images);
            }
        }
    }

    private void uploadVoices(Activity activity) {
        final ArrayList<Voice> voices = new ArrayList<>(getApplicationComponent().MyDatabase().voiceDao().
                getVoicesByBySentAndTrackNumber(trackNumber, false));
        if (voices.size() > 0) {
            final Gson gson = new Gson();
            final String onOffLoadDtoString = gson.toJson(voices);
            writeOnSdCard(onOffLoadDtoString, "voices", trackNumber);
            if (CustomFile.copyAudios(voices, trackNumber, activity)) {
                for (Voice voice : voices) {
                    voice.isSent = true;
                }
                getApplicationComponent().MyDatabase().voiceDao().updateVoice(voices);
            }
        }
    }

    private void uploadForbid(Activity activity) {
        if (forbiddenDtos.size() > 0) {
            final Gson gson = new Gson();
            final String forbiddenString = gson.toJson(forbiddenDtos);
            writeOnSdCard(forbiddenString, "forbiddenDtos", trackNumber);
            getApplicationComponent().MyDatabase().forbiddenDao().
                    updateAllForbiddenDtoBySent(true);
            for (ForbiddenDto forbiddenDto : forbiddenDtos) {
                if (forbiddenDto.address != null) {
                    final Image image = new Image();
                    image.address = forbiddenDto.address;
                    CustomFile.copyImages(image, trackNumber, activity);
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private boolean uploadOffLoad(Activity activity) {
        if (onOffLoadDtos.size() == 0) {
            thankYou(activity);
            onOffLoadDtos.clear();
            onOffLoadDtos.add(getApplicationComponent().MyDatabase().
                    onOffLoadDao().getOnOffLoadReadByTrackingAndOffLoad(id));
        }
        if (onOffLoadDtos.size() == 0 || onOffLoadDtos.get(0) == null) {
            getApplicationComponent().MyDatabase().trackingDao().updateTrackingDtoByArchive(id,
                    true, false, getDate(activity));
            return false;
        }
        final OnOffLoadDto.OffLoadData offLoadData = new OnOffLoadDto.OffLoadData();
        offLoadData.isFinal = true;
        offLoadData.finalTrackNumber = trackNumber;
        for (int i = 0; i < onOffLoadDtos.size(); i++)
            offLoadData.offLoads.add(new OnOffLoadDto.OffLoad(onOffLoadDtos.get(i)));
        offLoadData.offLoadReports.addAll(offLoadReports);
        final String onOffLoadDtoString = new Gson().toJson(offLoadData);
        writeOnSdCard(onOffLoadDtoString, "offLoadData", trackNumber);
        return true;
    }

    private void thankYou(Activity activity) {
        activity.runOnUiThread(() ->
                new CustomToast().info(activity.getString(R.string.thank_you), Toast.LENGTH_LONG));
    }
}

