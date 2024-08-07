package com.leon.counter_reading.utils.uploading;

import static com.leon.counter_reading.enums.ProgressType.SHOW_CANCELABLE;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getContext;
import static com.leon.counter_reading.utils.Converters.bitmapToFile;
import static com.leon.counter_reading.utils.CustomFile.loadImage;
import static com.leon.counter_reading.utils.CustomFile.prepareVoiceToSend;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.fragments.upload.UploadFragment;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.tables.ImageMultiple;
import com.leon.counter_reading.tables.MultimediaUploadResponse;
import com.leon.counter_reading.tables.Voice;
import com.leon.counter_reading.tables.VoiceMultiple;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomToast;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PrepareMultimedia extends AsyncTask<Activity, Activity, Activity> {
    private final CustomProgressModel progress;
    private final ArrayList<Image> images = new ArrayList<>();
    private final ArrayList<Voice> voice = new ArrayList<>();
    private final ImageMultiple imageMultiples = new ImageMultiple();
    private final VoiceMultiple voiceMultiples = new VoiceMultiple();
    private final UploadFragment uploadFragment;
    private final boolean justImages;

    public PrepareMultimedia(Activity activity, UploadFragment uploadFragment, boolean justImages) {
        super();
        progress = getApplicationComponent().CustomProgressModel();
        progress.show(activity, false);
        this.uploadFragment = uploadFragment;
        this.justImages = justImages;
    }

    @Override
    protected Activity doInBackground(Activity... activities) {
        images.clear();
        images.addAll(getApplicationComponent().MyDatabase().imageDao().getImagesByBySent(false));
        voice.clear();
        if (!justImages)
            voice.addAll(getApplicationComponent().MyDatabase().voiceDao().
                    getVoicesByBySent(false));
        for (int i = 0; i < images.size(); i++) {
            final Bitmap bitmap = loadImage(activities[0], images.get(i).address);
            if (images.get(i) != null && bitmap != null && images.get(i).OnOffLoadId != null) {
                try {
                    imageMultiples.Description.add(RequestBody.create(images.get(i).Description,
                            MediaType.parse("text/plain")));
                } catch (Exception e) {
                    e.printStackTrace();
                    imageMultiples.Description.add(RequestBody.create("",
                            MediaType.parse("text/plain")));
                }
                imageMultiples.IsGallery.add(RequestBody.create(String.valueOf(images.get(0).isGallery),
                        MediaType.parse("text/plain")));
                imageMultiples.OnOffLoadId.add(RequestBody.create(images.get(i).OnOffLoadId,
                        MediaType.parse("text/plain")));
                imageMultiples.File.add(bitmapToFile(bitmap, activities[0]));
            } else {
                getApplicationComponent().MyDatabase().imageDao().deleteImage(images.get(i).id);
            }
        }
        for (int i = 0; i < voice.size(); i++) {
            voice.get(i).File = prepareVoiceToSend(activities[0]
                    .getExternalFilesDir(null).getAbsolutePath() +
                    activities[0].getString(R.string.audio_folder) + voice.get(i).address);
            if (voice.get(i).File != null && voice.get(i).OnOffLoadId != null) {
                voiceMultiples.OnOffLoadId.add(RequestBody.create(voice.get(i).OnOffLoadId,
                        MediaType.parse("text/plain")));
                try {
                    voiceMultiples.Description.add(RequestBody.create(voice.get(i).Description,
                            MediaType.parse("text/plain")));
                } catch (Exception e) {
                    e.printStackTrace();
                    voiceMultiples.Description.add(RequestBody.create("",
                            MediaType.parse("text/plain")));
                }
                voiceMultiples.File.add(voice.get(i).File);
            } else {
                getApplicationComponent().MyDatabase().voiceDao().deleteVoice(voice.get(i).id);
            }
        }
        return activities[0];
    }

    @Override
    protected void onPostExecute(Activity activity) {
        super.onPostExecute(activity);
        progress.getDialog().dismiss();
        uploadImages(activity);
        if (!justImages)
            uploadVoice(activity);
    }

    private void uploadVoice(Activity activity) {
        if (!voice.isEmpty()) {
            final Retrofit retrofit = getApplicationComponent().Retrofit();
            final IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
            final Call<MultimediaUploadResponse> call = iAbfaService.voiceUploadMultiple(
                    voiceMultiples.File, voiceMultiples.OnOffLoadId, voiceMultiples.Description);
            HttpClientWrapper.callHttpAsync(call, SHOW_CANCELABLE.getValue(), activity,
                    new UploadVoices(voice), new UploadVoicesIncomplete(), new UploadMultimediaError());
        } else {
            activity.runOnUiThread(() ->
                    new CustomToast().info(activity.getString(R.string.there_is_no_voices), Toast.LENGTH_LONG));
        }
    }

    private void uploadImages(Activity activity) {
        if (!images.isEmpty()) {
            final Retrofit retrofit = getApplicationComponent().Retrofit();
            final IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
            final Call<MultimediaUploadResponse> call = iAbfaService.fileUploadMultiple(imageMultiples.File,
                    imageMultiples.OnOffLoadId, imageMultiples.Description, imageMultiples.IsGallery);
            HttpClientWrapper.callHttpAsync(call, SHOW_CANCELABLE.getValue(), activity,
                    new UploadImages(images, activity, uploadFragment), new UploadImagesIncomplete(),
                    new UploadMultimediaError());
        } else {
            activity.runOnUiThread(() ->
                    new CustomToast().info(activity.getString(R.string.there_is_no_images),
                            Toast.LENGTH_LONG));
        }
    }

}

class UploadImages implements ICallback<MultimediaUploadResponse> {
    private final ArrayList<Image> images;
    private final Activity activity;
    private final UploadFragment uploadFragment;

    public UploadImages(ArrayList<Image> images, Activity activity, UploadFragment uploadFragment) {
        this.images = new ArrayList<>(images);
        this.activity = activity;
        this.uploadFragment = uploadFragment;
    }

    @Override
    public void execute(Response<MultimediaUploadResponse> response) {
        if (response.body() != null && response.body().status == 200) {
            new CustomToast().success(response.body().message, Toast.LENGTH_LONG);
            updateImages();
            uploadFragment.setMultimediaInfo();
            new PrepareMultimedia(activity, uploadFragment, true).execute(activity);
        }
    }

    private void updateImages() {
        for (int i = 0; i < images.size(); i++) {
            images.get(i).isSent = true;
            getApplicationComponent().MyDatabase().imageDao().updateImage(images.get(i));
        }
    }
}

class UploadVoices implements ICallback<MultimediaUploadResponse> {
    private final ArrayList<Voice> voice;

    public UploadVoices(ArrayList<Voice> voice) {
        this.voice = new ArrayList<>(voice);
    }

    @Override
    public void execute(Response<MultimediaUploadResponse> response) {
        if (response.body() != null && response.body().status == 200) {
            new CustomToast().success(response.body().message);
            updateVoice();
        }
    }

    void updateVoice() {
        for (int i = 0; i < voice.size(); i++) {
            voice.get(i).isSent = true;
            getApplicationComponent().MyDatabase().voiceDao().updateVoice(voice.get(i));
        }
    }
}

class UploadImagesIncomplete implements ICallbackIncomplete<MultimediaUploadResponse> {
    @Override
    public void executeIncomplete(Response<MultimediaUploadResponse> response) {
        final CustomErrorHandling errorHandling = new CustomErrorHandling(getContext());
        final String error = errorHandling.getErrorMessageDefault(response);
        new CustomToast().warning(error, Toast.LENGTH_LONG);
    }
}

class UploadVoicesIncomplete implements ICallbackIncomplete<MultimediaUploadResponse> {
    @Override
    public void executeIncomplete(Response<MultimediaUploadResponse> response) {
        final CustomErrorHandling errorHandling = new CustomErrorHandling(getContext());
        final String error = errorHandling.getErrorMessageDefault(response);
        new CustomToast().warning(error, Toast.LENGTH_LONG);
    }
}

class UploadMultimediaError implements ICallbackError {
    @Override
    public void executeError(Throwable t) {
        if (!HttpClientWrapper.cancel) {
            final CustomErrorHandling errorHandling = new CustomErrorHandling(getContext());
            new CustomToast().error(errorHandling.getErrorMessageTotal(t), Toast.LENGTH_LONG);
        }
    }
}