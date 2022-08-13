package com.leon.counter_reading.di.view_model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leon.counter_reading.R;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.utils.CustomToast;

public final class CustomProgressModel {
    private Dialog dialog;

    public static CustomProgressModel getInstance() {
        return new CustomProgressModel();
    }

    public Dialog show(Context context) {
        return show(context, "");
    }

    public Dialog show(Context context, CharSequence title) {
        return show(context, title, false);
    }

    public Dialog show(Context context, CharSequence title, boolean cancelable) {
        return show(context, title, cancelable, dialog ->
                new CustomToast().warning(MyApplication.getContext().getString(R.string.canceled),
                        Toast.LENGTH_LONG));
    }

    public void show(Context context, boolean cancelable) {
        show(context, context.getString(R.string.waiting), cancelable, dialog ->
                new CustomToast().warning(MyApplication.getContext().getString(R.string.canceled),
                        Toast.LENGTH_LONG));
    }

    public Dialog show(Context context, CharSequence title, DialogInterface.OnCancelListener cancelListener) {
        return show(context, title, true, cancelListener);
    }

    public Dialog show(Context context, DialogInterface.OnCancelListener cancelListener) {
        return show(context, context.getString(R.string.waiting), true, cancelListener);
    }

    @SuppressLint("InflateParams")
    public Dialog show(Context context, CharSequence title, boolean cancelable,
                       DialogInterface.OnCancelListener cancelListener) {
        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.progress_bar, null);
        dialog = new Dialog(context, R.style.NewDialog);
        setCancelable(cancelable, view, cancelListener);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        final TextView textView = view.findViewById(R.id.text_view_title);
        textView.setText(title);
        if (!((Activity) context).isFinishing()) {
            //show dialog
            try {
                dialog.show();
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
        }
        return dialog;
    }

    void setCancelable(boolean cancelable, View view, DialogInterface.OnCancelListener cancelListener) {
        dialog.setCancelable(cancelable);
        if (cancelable) {
            dialog.setOnCancelListener(cancelListener);
            RelativeLayout relativeLayout = view.findViewById(R.id.relative_layout);
            relativeLayout.setOnClickListener(v -> {
                HttpClientWrapper.cancel = true;
                if (HttpClientWrapper.call != null) {
                    HttpClientWrapper.call.cancel();
                    HttpClientWrapper.call = null;
                }
                cancelDialog();
            });
        }
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void cancelDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog.cancel();
            dialog = null;
        }
    }
}