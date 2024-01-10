package com.leon.counter_reading.utils;

import static com.leon.counter_reading.helpers.MyApplication.getContext;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import com.leon.counter_reading.enums.NotificationType;

public class MakeNotification {
    public static void makeVibrate(Context context, int... milliSeconds) {
        final Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(milliSeconds.length > 0 ? milliSeconds[0] : 500);
    }

    public static void makeRing(Context context, NotificationType type) {
        try {
            Uri notificationPath = switch (type) {
                case NOT_SAVE ->
                        Uri.parse("android.resource://" + context.getPackageName() + "/raw/not_save");
                case SAVE ->
                        Uri.parse("android.resource://" + context.getPackageName() + "/raw/save");
                case LIGHT_ON ->
                        Uri.parse("android.resource://" + context.getPackageName() + "/raw/light_switch_on");
                case LIGHT_OFF ->
                        Uri.parse("android.resource://" + context.getPackageName() + "/raw/light_switch_off");
                default -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            };
            Ringtone r = RingtoneManager.getRingtone(context, notificationPath);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ringNotification() {
        try {
            final AudioManager am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            am.playSoundEffect(AudioManager.FX_KEY_CLICK, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

