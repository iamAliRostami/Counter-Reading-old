package com.leon.counter_reading.utils.login;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.leon.counter_reading.enums.DialogType.Green;
import static com.leon.counter_reading.enums.SharedReferenceKeys.PERSONAL_CODE;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getDigits;

import android.app.Activity;
import android.content.Context;
import android.text.InputFilter;
import android.view.inputmethod.InputMethodManager;

import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.utils.CalendarTool;
import com.leon.counter_reading.utils.custom_dialog.LovelyTextInputDialog;

public class TwoStepVerification {
    public static void insertPersonalCode(Context context) {
        final LovelyTextInputDialog lovelyTextInputDialog = new LovelyTextInputDialog(context);
        lovelyTextInputDialog.getEditTextNumber().setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});
        lovelyTextInputDialog.setTopColorRes(R.color.yellow).setTopTitleColorRes(R.color.white)
                .setTopTitle(R.string.verification_code).setTitle(R.string.dear_user).setMessage(context.getString(R.string.enter_personal_code))
                .setCancelable(false).setInputFilter(R.string.error_empty, text ->
                        lovelyTextInputDialog.getEditTextNumber().getText().toString().isEmpty()).setConfirmButton(R.string.confirm, text -> {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    final int personalCode = getDigits(lovelyTextInputDialog.getEditTextNumber().getText().toString());
                    getApplicationComponent().SharedPreferenceModel().putData(PERSONAL_CODE.getValue(), personalCode);
                }).setNegativeButton(R.string.close, v -> {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                });
        lovelyTextInputDialog.show();
    }

    public static void showPersonalCode(Activity activity) {
        if (getApplicationComponent().SharedPreferenceModel().getIntNullData(PERSONAL_CODE.getValue()) > 0) {
            final CalendarTool calendarTool = new CalendarTool();
            final String verificationCode = String.valueOf(getApplicationComponent().SharedPreferenceModel().getIntData(PERSONAL_CODE.getValue()) + 1313 * calendarTool.getIranianMonth() * calendarTool.getIranianDay());
            final CustomDialogModel dialog = new CustomDialogModel(Green, activity, verificationCode, MyApplication.getContext().getString(R.string.verification_code), MyApplication.getContext().getString(R.string.dear_user), MyApplication.getContext().getString(R.string.accepted));
            dialog.getLovelyStandardDialog().getMessageView().setTextSize(activity.getResources().getDimension(R.dimen.text_size_huge));
        } else insertPersonalCode(activity);
    }
}
