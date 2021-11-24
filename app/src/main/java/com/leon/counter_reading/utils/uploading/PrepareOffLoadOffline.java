package com.leon.counter_reading.utils.uploading;

import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.utils.OfflineUtils.writeOnSdCard;
import static com.leon.counter_reading.utils.OfflineUtils.zipFileAtPath;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.enums.OffloadStateEnum;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.tables.ForbiddenDto;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.tables.OffLoadReport;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.Voice;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.utils.CustomToast;

import java.util.ArrayList;

public class PrepareOffLoadOffline extends AsyncTask<Activity, Activity, Activity> {
    private final CustomProgressModel customProgressModel;
    private final ArrayList<OnOffLoadDto> onOffLoadDtos = new ArrayList<>();
    private final ArrayList<OffLoadReport> offLoadReports = new ArrayList<>();
    private final ArrayList<ForbiddenDto> forbiddenDtos = new ArrayList<>();
    private final int trackNumber;
    private final String id;

    public PrepareOffLoadOffline(Activity activity, int trackNumber, String id) {
        super();
        this.trackNumber = trackNumber;
        this.id = id;
        customProgressModel = MyApplication.getApplicationComponent().CustomProgressModel();
        customProgressModel.show(activity, false);
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected Activity doInBackground(Activity... activities) {
        forbiddenDtos.clear();
        forbiddenDtos.addAll(MyApplication.getApplicationComponent().MyDatabase().
                forbiddenDao().getAllForbiddenDto(false));
        if (forbiddenDtos.size() > 0) {
            uploadForbid(activities[0]);
        }
        onOffLoadDtos.clear();
        onOffLoadDtos.addAll(MyApplication.getApplicationComponent().MyDatabase().
                onOffLoadDao().getOnOffLoadReadByTrackingAndOffLoad(trackNumber,
                OffloadStateEnum.INSERTED.getValue()));
        offLoadReports.clear();
        offLoadReports.addAll(MyApplication.getApplicationComponent().MyDatabase().
                offLoadReportDao().getAllOffLoadReport(false));
        if (uploadOffLoad(activities[0])) {
            uploadImages(activities[0]);
            uploadVoices(activities[0]);
            if (zipFileAtPath(trackNumber)) {
                //TODO after zip and sent
                for (int i = 0; i < onOffLoadDtos.size(); i++)
                    onOffLoadDtos.get(i).offLoadStateId = OffloadStateEnum.SENT.getValue();
//                getApplicationComponent().MyDatabase().onOffLoadDao().updateOnOffLoads(onOffLoadDtos);
                getApplicationComponent().MyDatabase().trackingDao().updateTrackingDtoByArchive(id, true, false);
                for (int i = 0; i < offLoadReports.size(); i++) offLoadReports.get(i).isSent = true;
                getApplicationComponent().MyDatabase().offLoadReportDao().updateOffLoadReport(offLoadReports);

                activities[0].runOnUiThread(() -> {
                    String message = "تعداد %d اشتراک با موفقیت بارگذاری شد.";
                    message = String.format(message, onOffLoadDtos.size());
                    new CustomToast().success(message, Toast.LENGTH_LONG);
                });
            }
        }
        return activities[0];
    }

    @Override
    protected void onPostExecute(Activity activity) {
        super.onPostExecute(activity);
        customProgressModel.getDialog().dismiss();
    }

    private void uploadImages(Activity activity) {
        final ArrayList<Image> images = new ArrayList<>(getApplicationComponent().MyDatabase().imageDao()
                .getImagesByBySentAndTrack(trackNumber, false));
        if (images.size() > 0) {
            final Gson gson = new Gson();
            final String onOffLoadDtoString = gson.toJson(images);
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

    private boolean uploadOffLoad(Activity activity) {
        if (onOffLoadDtos.size() == 0) {
            thankYou(activity);
            onOffLoadDtos.clear();
            onOffLoadDtos.add(MyApplication.getApplicationComponent().MyDatabase().
                    onOffLoadDao().getOnOffLoadReadByTrackingAndOffLoad(trackNumber));
        }
        if (onOffLoadDtos.size() == 0 || onOffLoadDtos.get(0) == null) {
            MyApplication.getApplicationComponent().MyDatabase().
                    trackingDao().updateTrackingDtoByArchive(id, true, false);
            return false;
        }
        final OnOffLoadDto.OffLoadData offLoadData = new OnOffLoadDto.OffLoadData();
        offLoadData.isFinal = true;
        offLoadData.finalTrackNumber = trackNumber;
        for (int i = 0; i < onOffLoadDtos.size(); i++)
            offLoadData.offLoads.add(new OnOffLoadDto.OffLoad(onOffLoadDtos.get(i)));
        offLoadData.offLoadReports.addAll(offLoadReports);
        final Gson gson = new Gson();
        final String onOffLoadDtoString = gson.toJson(offLoadData);
        writeOnSdCard(onOffLoadDtoString, "offLoadData", trackNumber);
        return true;
    }


    private void thankYou(Activity activity) {
        activity.runOnUiThread(() ->
                new CustomToast().info(activity.getString(R.string.thank_you), Toast.LENGTH_LONG));
    }
}

