package com.leon.counter_reading.utils.login;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.text.InputType;
import android.view.inputmethod.InputMethodManager;

import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.utils.custom_dialog.LovelyTextInputDialog;

public class SetProxy {
    public static void insertProxy(Context context) {
        LovelyTextInputDialog lovelyTextInputDialog = new LovelyTextInputDialog(context);
        lovelyTextInputDialog.setTopColorRes(R.color.yellow)
                .setTopTitleColorRes(R.color.white)
                .setTopTitle(R.string.proxy)
                .setTitle(R.string.dear_user)
                .setMessage(context.getString(R.string.enter_proxy))
                .setCancelable(false)
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .setHint("http:// یا https://")
                .setInitialInput(MyApplication.getApplicationComponent().SharedPreferenceModel().getStringData(SharedReferenceKeys.PROXY.getValue()))
                .setInputFilter(R.string.error_format, text -> {
                    String proxy = lovelyTextInputDialog.getEditTextNumber().getText().toString();
                    return proxy.length() == 0 || proxy.startsWith("https://") || proxy.startsWith("http://")/**/;
//                    return true;
                })
                .setConfirmButton(R.string.confirm, text -> {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    String personalCode = lovelyTextInputDialog.getEditTextNumber().getText().toString();
                    MyApplication.getApplicationComponent().SharedPreferenceModel()
                            .putData(SharedReferenceKeys.PROXY.getValue(), personalCode);
                })
                .setNegativeButton(R.string.close, v -> {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//                        new CustomToast().warning(context.getString(R.string.canceled), Toast.LENGTH_LONG);
                });
        lovelyTextInputDialog.show();
    }

    public static void showProxy(Activity activity) {
        if (MyApplication.getApplicationComponent().SharedPreferenceModel()
                .getIntData(SharedReferenceKeys.PROXY.getValue()) > 0) {
            CustomDialogModel customDialogModel = new CustomDialogModel(DialogType.Green, activity,
                    MyApplication.getApplicationComponent().SharedPreferenceModel()
                            .getStringData(SharedReferenceKeys.PROXY.getValue()),
                    MyApplication.getContext().getString(R.string.proxy),
                    MyApplication.getContext().getString(R.string.dear_user),
                    MyApplication.getContext().getString(R.string.accepted));
            customDialogModel.getLovelyStandardDialog().getMessageView()
                    .setTextSize(activity.getResources().getDimension(R.dimen.text_size_huge));
        } else insertProxy(activity);
    }
}
