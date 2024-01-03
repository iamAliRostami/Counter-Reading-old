package com.leon.counter_reading.di.view_model;

import static com.leon.counter_reading.enums.DialogType.Green;
import static com.leon.counter_reading.enums.DialogType.GreenRedirect;
import static com.leon.counter_reading.enums.DialogType.Red;
import static com.leon.counter_reading.enums.DialogType.RedRedirect;
import static com.leon.counter_reading.enums.DialogType.Yellow;
import static com.leon.counter_reading.enums.DialogType.YellowRedirect;
import static com.leon.counter_reading.helpers.MyApplication.getActivityComponent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.HomeActivity;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.utils.custom_dialog.LovelyStandardDialog;

import javax.inject.Inject;

public class CustomDialogModel {
    private final LovelyStandardDialog lovelyStandardDialog;
    private Context context;

    @Inject
    public CustomDialogModel(Context context) {
        lovelyStandardDialog = new LovelyStandardDialog(context);
    }

    public CustomDialogModel(DialogType choose, Context context, String message, String title,
                             String top, String buttonText, Inline... inline) {
        lovelyStandardDialog = getActivityComponent().LovelyStandardDialog();
        this.context = context;
        lovelyStandardDialog.setTitle(title).setMessage(message).setTopTitle(top);
        if (choose == Green)
            CustomGreenDialog(buttonText);
        else if (choose == Yellow)
            CustomYellowDialog(buttonText);
        else if (choose == Red)
            CustomRedDialog(buttonText);
        else if (choose == GreenRedirect)
            CustomGreenDialogRedirect(buttonText);
        else if (choose == YellowRedirect)
            CustomYellowDialogRedirect(buttonText, inline);
        else if (choose == RedRedirect)
            CustomRedDialogRedirect(buttonText);

        if (lovelyStandardDialog.getMessageView().isShown())
            lovelyStandardDialog.dismiss();
        if (!((Activity) context).isFinishing()) {
            try {
                lovelyStandardDialog.show();
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
        }
    }

    public CustomDialogModel(DialogType choose, Context context, int message, int title,
                             int top, int buttonText, Inline... inline) {
        lovelyStandardDialog = getActivityComponent().LovelyStandardDialog();
        this.context = context;
        lovelyStandardDialog.setTitle(title).setMessage(message).setTopTitle(top);
        if (choose == Green)
            CustomGreenDialog(context.getString(buttonText));
        else if (choose == Yellow)
            CustomYellowDialog(context.getString(buttonText));
        else if (choose == Red)
            CustomRedDialog(context.getString(buttonText));
        else if (choose == GreenRedirect)
            CustomGreenDialogRedirect(context.getString(buttonText));
        else if (choose == YellowRedirect)
            CustomYellowDialogRedirect(context.getString(buttonText), inline);
        else if (choose == RedRedirect)
            CustomRedDialogRedirect(context.getString(buttonText));

        if (lovelyStandardDialog.getMessageView().isShown())
            lovelyStandardDialog.dismiss();
        if (!((Activity) context).isFinishing()) {
            try {
                lovelyStandardDialog.show();
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
        }
    }

    public CustomDialogModel(DialogType choose, Context context, String message, int title,
                             int top, int buttonText, Inline... inline) {
        lovelyStandardDialog = getActivityComponent().LovelyStandardDialog();
        this.context = context;
        lovelyStandardDialog.setTitle(title).setMessage(message).setTopTitle(top);
        if (choose == Green)
            CustomGreenDialog(context.getString(buttonText));
        else if (choose == Yellow)
            CustomYellowDialog(context.getString(buttonText));
        else if (choose == Red)
            CustomRedDialog(context.getString(buttonText));
        else if (choose == GreenRedirect)
            CustomGreenDialogRedirect(context.getString(buttonText));
        else if (choose == YellowRedirect)
            CustomYellowDialogRedirect(context.getString(buttonText), inline);
        else if (choose == RedRedirect)
            CustomRedDialogRedirect(context.getString(buttonText));

        if (lovelyStandardDialog.getMessageView().isShown())
            lovelyStandardDialog.dismiss();
        if (!((Activity) context).isFinishing()) {
            try {
                lovelyStandardDialog.show();
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
        }
    }

    public LovelyStandardDialog getLovelyStandardDialog() {
        return lovelyStandardDialog;
    }

    public void CustomGreenDialogRedirect(String ButtonText) {
        lovelyStandardDialog.setTopColorRes(R.color.green)
                .setTopTitleColor(ContextCompat.getColor(context, R.color.text_color_light))
                .setButtonsBackground(R.drawable.border_green_1)
                .setPositiveButton(ButtonText, v -> {
                    Intent intent = new Intent(context, HomeActivity.class);
                    context.startActivity(intent);
                });
    }

    public void CustomYellowDialogRedirect(String buttonText, Inline... inlines) {
        lovelyStandardDialog.setTopTitleColor(ContextCompat.getColor(context, R.color.text_color_light))
                .setButtonsBackground(R.drawable.border_yellow_1)
                .setTopColorRes(R.color.yellow).setPositiveButton(buttonText, v -> inlines[0].inline());
    }

    public void CustomRedDialogRedirect(String buttonText) {
        lovelyStandardDialog.setTopColorRes(R.color.red)
                .setTopTitleColor(ContextCompat.getColor(context, R.color.text_color_light))
                .setButtonsBackground(R.drawable.border_red_1)
                .setPositiveButton(buttonText, v -> lovelyStandardDialog.dismiss());
    }

    public void CustomGreenDialog(String ButtonText) {
        lovelyStandardDialog.setTopColorRes(R.color.green)
                .setTopTitleColor(ContextCompat.getColor(context, R.color.text_color_light))
                .setButtonsBackground(R.drawable.border_green_1)
                .setPositiveButton(ButtonText, v -> lovelyStandardDialog.dismiss());
    }

    public void CustomYellowDialog(String buttonText) {
        lovelyStandardDialog.setTopTitleColor(ContextCompat.getColor(context, R.color.text_color_light))
                .setTopColorRes(R.color.yellow)
                .setButtonsBackground(R.drawable.border_yellow_1)
                .setPositiveButton(buttonText, v -> lovelyStandardDialog.dismiss());
    }

    public void CustomRedDialog(String buttonText) {
        lovelyStandardDialog.setTopColorRes(R.color.red)
                .setTopTitleColor(ContextCompat.getColor(context, R.color.text_color_light))
                .setButtonsBackground(R.drawable.border_red_1)
                .setPositiveButton(buttonText, v -> lovelyStandardDialog.dismiss());
    }

    public interface Inline {
        void inline();
    }
}
