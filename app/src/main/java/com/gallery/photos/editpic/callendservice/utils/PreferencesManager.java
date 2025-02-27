package com.gallery.photos.editpic.callendservice.utils;

import android.content.Context;
import android.content.SharedPreferences;

import kotlin.jvm.internal.DefaultConstructorMarker;

public final class PreferencesManager {
    public static final Companion Companion = new Companion();
    private static volatile PreferencesManager sInstance;
    private SharedPreferences.Editor myEdit;
    private SharedPreferences sharedPreferences;

    public PreferencesManager(Context context, DefaultConstructorMarker defaultConstructorMarker) {
        this(context);
    }

    public static final PreferencesManager getInstance(Context context) {
        return Companion.getInstance(context);
    }

    private PreferencesManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences("smart_messages", 0);
        this.myEdit = sharedPreferences.edit();
        this.myEdit.apply();
    }

    public final String getLanguage() {
        return this.sharedPreferences.getString("LANGUAGE", "en");
    }

    public final Long getMuteNotificationTime() {
        return Long.valueOf(this.sharedPreferences.getLong("NOTIFICATION_TIME", 0L));
    }

    public final Boolean getMuteNotificationAlways() {
        return Boolean.valueOf(this.sharedPreferences.getBoolean("NOTIFICATION_ALWAYS", false));
    }

    public final Long getAppUpdateDate() {
        return Long.valueOf(this.sharedPreferences.getLong("app_update_date", 0L));
    }

    public final void setMuteNotification(Context context, int i) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("smart_messages", 0);
        this.sharedPreferences = sharedPreferences;
        SharedPreferences.Editor edit = sharedPreferences.edit();
        this.myEdit = edit;
        edit.putInt("MUTE_NOTIFICATION", i);
        this.myEdit.apply();
    }

    public static final class Companion {

        public final PreferencesManager getInstance(Context context) {
            if (PreferencesManager.sInstance == null) {
                synchronized (this) {
                    PreferencesManager.sInstance = new PreferencesManager(context, null);
                }
            }
            return PreferencesManager.sInstance;
        }
    }
}
