package com.gallery.photos.editpic.callendservice;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.gallery.photos.editpic.R;


public class ReminderReceiver extends BroadcastReceiver {
    String channelId = "CallStateService";
    Context mContext;
    int reminderId;
    String title;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        try {
            this.title = intent.getStringExtra("extra_reminder_name");
            this.reminderId = intent.getIntExtra("extra_reminder_id", 0);
            showNotification();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    public void showNotification() {
        NotificationCompat.Builder builder;
        if (TextUtils.isEmpty(this.title)) {
            this.title = "No Title";
        }
        Uri defaultUri = RingtoneManager.getDefaultUri(2);
        NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(createChannel(this.channelId));
            }
            builder = new NotificationCompat.Builder(this.mContext, this.channelId);
        } else {
            builder = new NotificationCompat.Builder(this.mContext, this.channelId);
        }
        builder.setPriority(1).setContentTitle(this.title).setDefaults(1).setSmallIcon(R.drawable.ic_notification).
            setStyle(new NotificationCompat.DecoratedCustomViewStyle()).setColor(ContextCompat.getColor(this.mContext, R.color.tools_theme))
            .setVibrate(new long[]{100, 200, 400, 600, 800, 1000}).setSound(defaultUri).setOngoing(false).setAutoCancel(true);
        notificationManager.notify(this.reminderId, builder.build());
    }

    @TargetApi(26)
    public NotificationChannel createChannel(String str) {
        Uri defaultUri = RingtoneManager.getDefaultUri(2);
        AudioAttributes build = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build();
        NotificationChannel m = new NotificationChannel(str, "notification", NotificationManager.IMPORTANCE_HIGH);
        m.setDescription("this private chanel");
        m.enableLights(true);
        m.setSound(defaultUri, build);
        m.setLightColor(-256);
        return m;
    }
}
