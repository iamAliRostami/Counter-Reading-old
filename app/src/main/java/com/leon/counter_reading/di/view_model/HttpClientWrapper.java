package com.leon.counter_reading.di.view_model;

import static com.leon.counter_reading.enums.ProgressType.SHOW;
import static com.leon.counter_reading.enums.ProgressType.SHOW_CANCELABLE;
import static com.leon.counter_reading.enums.ProgressType.SHOW_CANCELABLE_REDIRECT;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.utils.PermissionManager.isNetworkAvailable;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.leon.counter_reading.R;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.utils.CustomToast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HttpClientWrapper {
    public static Call call;
    public static boolean cancel;
    public static CustomProgressModel progress;

    public static <T> void callHttpAsync(Call<T> call, final int progressType, final Context context,
                                         final ICallback<T> callback, final ICallbackIncomplete<T> incomplete,
                                         final ICallbackError error) {
        cancel = false;
        final CustomProgressModel progress = getApplicationComponent().CustomProgressModel();
        try {
            if (progressType == SHOW.getValue()) {
                progress.show(context, context.getString(R.string.waiting));
            } else if (progressType == SHOW_CANCELABLE.getValue()) {
                progress.show(context, context.getString(R.string.waiting), true);
            } else if (progressType == SHOW_CANCELABLE_REDIRECT.getValue()) {
                progress.show(context, context.getString(R.string.waiting), true);
            }
        } catch (Exception e) {
            new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
        }

        if (isNetworkAvailable(context)) {
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                    if (!cancel) {
                        if (progress.getDialog() != null)
                            try {
                                progress.getDialog().dismiss();
                            } catch (Exception e) {
                                new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
                            }
                        if (response.isSuccessful()) {
                            callback.execute(response);
                        } else {
                            ((Activity) context).runOnUiThread(() -> incomplete.executeIncomplete(response));
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {

                    ((Activity) context).runOnUiThread(() -> error.executeError(t));
                    if (progress.getDialog() != null)
                        try {
                            progress.getDialog().dismiss();
                        } catch (Exception e) {
                            new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
                        }
                }
            });
            HttpClientWrapper.call = call;
        } else {
            if (progress.getDialog() != null)
                try {
                    progress.getDialog().dismiss();
                } catch (Exception e) {
                    new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
                }
            new CustomToast().warning(context.getString(R.string.turn_internet_on));
        }
    }

    public static <T> void callHttpAsyncProgressDismiss(Call<T> call, int progressType,
                                                        final Context context,
                                                        final ICallback<T> callback,
                                                        final ICallbackIncomplete<T> callbackIncomplete,
                                                        final ICallbackError callbackError) {
        progress = getApplicationComponent().CustomProgressModel();
        try {
            if (progressType == SHOW.getValue()) {
                progress.show(context, context.getString(R.string.waiting));
            } else if (progressType == SHOW_CANCELABLE.getValue()) {
                progress.show(context, context.getString(R.string.waiting), true);
            } else if (progressType == SHOW_CANCELABLE_REDIRECT.getValue()) {
                progress.show(context, context.getString(R.string.waiting), true);
            }
        } catch (Exception e) {
            new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
        }
        if (isNetworkAvailable(context)) {
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                    if (progress.getDialog() != null)
                        progress.getDialog().dismiss();
                    if (response.isSuccessful()) {
                        callback.execute(response);
                    } else {
                        ((Activity) context).runOnUiThread(() -> callbackIncomplete.executeIncomplete(response));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                    if (progress.getDialog() != null)
                        progress.getDialog().dismiss();
                    ((Activity) context).runOnUiThread(() -> callbackError.executeError(t));
                }
            });
            HttpClientWrapper.call = call;
        } else {
            if (progress.getDialog() != null)
                progress.getDialog().dismiss();
            new CustomToast().warning(context.getString(R.string.turn_internet_on));
        }
    }
}