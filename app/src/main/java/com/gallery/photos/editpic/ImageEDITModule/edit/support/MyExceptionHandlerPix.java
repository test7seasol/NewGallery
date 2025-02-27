package com.gallery.photos.editpic.ImageEDITModule.edit.support;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.gallery.photos.editpic.Activity.MainActivity;
import com.gallery.photos.editpic.ImageEDITModule.edit.ArtRoom;

import java.lang.Thread;

/* loaded from: classes.dex */
public class MyExceptionHandlerPix implements Thread.UncaughtExceptionHandler {
    private Activity activity;

    public MyExceptionHandlerPix(Activity activity) {
        this.activity = activity;
    }

    @SuppressLint("WrongConstant")
    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread thread, Throwable th) {
        Intent intent;
        if (this.activity != null) {
            intent = new Intent(this.activity, (Class<?>) MainActivity.class);
        } else {
            intent = ArtRoom.getContext() != null ? new Intent(ArtRoom.getContext(), (Class<?>) MainActivity.class) : null;
        }
        intent.putExtra("crash", true);
        intent.addFlags(335577088);
        ((AlarmManager) ArtRoom.getContext().getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.RTC, System.currentTimeMillis() + 10, PendingIntent.getActivity(ArtRoom.getContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE));
        System.exit(2);
    }
}
