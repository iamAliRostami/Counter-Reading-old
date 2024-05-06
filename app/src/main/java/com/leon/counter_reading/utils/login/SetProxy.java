package com.leon.counter_reading.utils.login;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.leon.counter_reading.enums.DialogType.Green;
import static com.leon.counter_reading.enums.SharedReferenceKeys.PROXY;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.validate;

import android.app.Activity;
import android.content.Context;
import android.text.InputType;
import android.view.inputmethod.InputMethodManager;

import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.utils.custom_dialog.LovelyTextInputDialog;

public class SetProxy {
    public static void insertProxy(Context context) {
        LovelyTextInputDialog lovelyTextInputDialog = new LovelyTextInputDialog(context);
        lovelyTextInputDialog.setTopColorRes(R.color.yellow)
                .setTopTitleColorRes(android.R.color.white)
                .setTopTitle(R.string.proxy)
                .setTitle(R.string.dear_user)
                .setMessage(context.getString(R.string.enter_proxy))
                .setCancelable(false)
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .setHint("http:// یا https://")
                .setInitialInput(getApplicationComponent().SharedPreferenceModel().getStringData(PROXY.getValue()))
                .setInputFilter(R.string.error_format, text -> {
                    final String ip = lovelyTextInputDialog.getEditTextNumber().getText().toString();
                    return !(ip.isEmpty() || validate(ip));
                })
                .setConfirmButton(R.string.confirm, text -> {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    String personalCode = lovelyTextInputDialog.getEditTextNumber().getText().toString();
                    getApplicationComponent().SharedPreferenceModel()
                            .putData(PROXY.getValue(), personalCode);
                })
                .setNegativeButton(R.string.close, v -> {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//                        new CustomToast().warning(context.getString(R.string.canceled), Toast.LENGTH_LONG);
                });
        lovelyTextInputDialog.show();
    }

    public static void showProxy(Activity activity) {
        if (getApplicationComponent().SharedPreferenceModel()
                .getIntData(PROXY.getValue()) > 0) {
            CustomDialogModel customDialogModel = new CustomDialogModel(Green, activity,
                    getApplicationComponent().SharedPreferenceModel().getStringData(PROXY.getValue()),
                    R.string.proxy, R.string.dear_user, R.string.accepted);
            customDialogModel.getLovelyStandardDialog().getMessageView()
                    .setTextSize(activity.getResources().getDimension(R.dimen.text_size_huge));
        } else insertProxy(activity);
    }
}
