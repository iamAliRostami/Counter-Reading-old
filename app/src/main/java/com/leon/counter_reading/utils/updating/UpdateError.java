package com.leon.counter_reading.utils.updating;

import static com.leon.counter_reading.enums.DialogType.Red;

import android.content.Context;

import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.utils.CustomErrorHandling;

public class UpdateError implements ICallbackError {
    final Context context;

    public UpdateError(Context context) {
        this.context = context;
    }

    @Override
    public void executeError(Throwable t) {
        if (!HttpClientWrapper.cancel) {
            CustomErrorHandling errorHandling = new CustomErrorHandling(context);
            String error = errorHandling.getErrorMessageTotal(t);
            new CustomDialogModel(Red, context, error, R.string.dear_user, R.string.update,
                    R.string.accepted);
        }
    }
}
