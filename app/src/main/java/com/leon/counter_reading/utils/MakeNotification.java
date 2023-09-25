package com.leon.counter_reading.utils;

import static com.leon.counter_reading.helpers.MyApplication.getContext;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.leon.counter_reading.enums.NotificationType;

public class MakeNotification {
    public static void makeVibrate(Context context, int... milliSeconds) {
        final Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(milliSeconds.length > 0 ? milliSeconds[0] : 500);
    }

    public static void makeRing(Context context, NotificationType type) {
//        makeVibrate(context);
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

    @SuppressLint("LaunchActivityFromNotification")
    public static void makeAboveNotification(Context context, Class<?> aClass, String actionName,
                                             String title, String text, String actionTitle,
                                             int smallIcon, int actionIcon) {
        final Intent intent = new Intent(context, aClass);
        intent.setAction(actionName);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getService(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "2")
                .setSmallIcon(smallIcon)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(actionIcon, actionTitle, pendingIntent)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(2, mBuilder.build());
    }
}

