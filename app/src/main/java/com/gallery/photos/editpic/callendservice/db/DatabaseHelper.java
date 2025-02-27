package com.gallery.photos.editpic.callendservice.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, "cdo_custom.DB", (SQLiteDatabase.CursorFactory) null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table Reminder(_id INTEGER PRIMARY KEY AUTOINCREMENT, _title TEXT NOT NULL, _time LONG, _color Int, _mobileNumber TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS Reminder");
        onCreate(sQLiteDatabase);
    }
}
