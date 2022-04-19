package com.leon.counter_reading.utils.photo;

import static com.leon.counter_reading.enums.ProgressType.SHOW_CANCELABLE;
import static com.leon.counter_reading.enums.SharedReferenceKeys.TOKEN;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.utils.CustomFile.bitmapToFile;
import static com.leon.counter_reading.utils.CustomFile.loadImage;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.fragments.dialog.TakePhotoFragment;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.tables.ImageGrouped;
import com.leon.counter_reading.tables.MultimediaUploadResponse;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomToast;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PrepareMultimedia extends AsyncTask<Activity, Integer, Activity> {
    private final ImageGrouped imageGrouped = new ImageGrouped();
    private final CustomProgressModel customProgressModel;
    private final ArrayList<Image> images;
    private final String description;
    private final boolean result;

    public PrepareMultimedia(ArrayList<Image> images, String description, boolean result,
                             Activity activity) {
        super();
        customProgressModel = getApplicationComponent().CustomProgressModel();
        customProgressModel.show(activity, false);
        this.description = description;
        this.result = result;
        this.images = images;
    }

    @Override
    protected Activity doInBackground(Activity... activities) {
        imageGrouped.File.clear();
        for (int i = 0; i < images.size(); i++) {
            images.get(i).Description = description;
            if (!images.get(i).isSent && images.get(i).File == null) //TODO
//                imageGrouped.File.add(bitmapToFile(images.get(i).bitmap, activities[0]));
//                imageGrouped.File.add(bitmapToFile(BitmapFactory.decodeFile(images.get(i).address), activities[0]));
            imageGrouped.File.add(bitmapToFile(loadImage(activities[0], images.get(i).address), activities[0]));
        }
        return activities[0];
    }

    @Override
    protected void onPostExecute(Activity activity) {
        super.onPostExecute(activity);
        if (customProgressModel.getDialog() != null)
            customProgressModel.getDialog().dismiss();
        uploadImage(activity);
    }

    private void uploadImage(Activity activity) {
        if (imageGrouped.File.size() > 0 && images.size() > 0) {
            imageGrouped.OnOffLoadId = RequestBody.create(images.get(0).OnOffLoadId,
                    MediaType.parse("text/plain"));
            imageGrouped.Description = RequestBody.create(images.get(0).Description,
                    MediaType.parse("text/plain"));
            final Retrofit retrofit = getApplicationComponent().NetworkHelperModel()
                    .getInstance(true, getApplicationComponent().SharedPreferenceModel()
                            .getStringData(TOKEN.getValue()), 10, 5 * imageGrouped.File.size(), 5);
            final IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
            final Call<MultimediaUploadResponse> call = iAbfaService.fileUploadGrouped(imageGrouped.File,
                    imageGrouped.OnOffLoadId, imageGrouped.Description);
            HttpClientWrapper.callHttpAsync(call, SHOW_CANCELABLE.getValue(), activity,
                    new UploadImages(activity), new UploadImagesIncomplete(activity),
                    new UploadImagesError(activity));
        } else if (result) {
            setResult();
        } else {
            activity.runOnUiThread(() ->
                    new CustomToast().warning(activity.getString(R.string.there_is_no_images), Toast.LENGTH_LONG));
        }
    }

    private void setResult() {
        TakePhotoFragment.newInstance().setResult();
    }

    private void saveImages(boolean isSent, Activity activity) {
//        for (int j = 0; j < 200; j++)//TODO
        for (int i = 0; i < images.size(); i++) {
            if (!images.get(i).isSent) {
                images.get(i).isSent = isSent;
                if (getApplicationComponent().MyDatabase().imageDao().getImagesById(images.get(i).id).size() > 0)
                    getApplicationComponent().MyDatabase().imageDao().updateImage(images.get(i));
                else {
                    //TODO
//                    final String address = saveTempBitmap(images.get(i).bitmap, activity);
//                    if (!address.equals(activity.getString(R.string.error_external_storage_is_not_writable))) {
//                        images.get(i).address = address;
                    getApplicationComponent().MyDatabase().imageDao().insertImage(images.get(i));
//                    }
                }
            }
        }
    }

    class UploadImages implements ICallback<MultimediaUploadResponse> {
        private final Activity activity;

        public UploadImages(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void execute(Response<MultimediaUploadResponse> response) {
            if (response.body() != null && response.body().status == 200) {
                new CustomToast().success(response.body().message, Toast.LENGTH_LONG);
            } else {
                new CustomToast().warning(activity.getString(R.string.error_upload), Toast.LENGTH_LONG);
            }
            saveImages(response.body() != null && response.body().status == 200, activity);
            setResult();
        }
    }

    class UploadImagesIncomplete implements ICallbackIncomplete<MultimediaUploadResponse> {
        private final Activity activity;

        public UploadImagesIncomplete(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void executeIncomplete(Response<MultimediaUploadResponse> response) {
            final CustomErrorHandling errorHandling = new CustomErrorHandling(activity);
            final String error = errorHandling.getErrorMessageDefault(response);
            new CustomToast().warning(error, Toast.LENGTH_LONG);
            saveImages(false, activity);
            setResult();
        }
    }

    class UploadImagesError implements ICallbackError {
        private final Activity activity;

        public UploadImagesError(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void executeError(Throwable t) {
            if (!HttpClientWrapper.cancel) {
                final CustomErrorHandling errorHandling = new CustomErrorHandling(activity);
                final String error = errorHandling.getErrorMessageTotal(t);
                new CustomToast().error(error, Toast.LENGTH_LONG);
            }
            saveImages(false, activity);
            setResult();
        }
    }
}