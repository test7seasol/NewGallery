package com.gallery.photos.editpic.callendservice.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gallery.photos.editpic.callendservice.model.Reminder;

import java.util.ArrayList;

public class MyDB {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public MyDB(Context context) {
        this.dbHelper = new DatabaseHelper(context);
        this.database = this.dbHelper.getWritableDatabase();
    }

    public long addReminder(Reminder reminder) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("_title", reminder.getTitle());
        contentValues.put("_time", Long.valueOf(reminder.getTime()));
        contentValues.put("_color", Integer.valueOf(reminder.getColor()));
        contentValues.put("_mobileNumber", reminder.getMobileNumber());
        return this.database.insert("Reminder", null, contentValues);
    }

    public ArrayList<Reminder> getReminderList() {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM Reminder", null);
        ArrayList<Reminder> arrayList = new ArrayList<>();
        if (rawQuery.moveToFirst()) {
            do {
                arrayList.add(new Reminder(rawQuery.getInt(0), rawQuery.getString(1), rawQuery.getLong(2), rawQuery.getInt(3), rawQuery.getString(4)));
            } while (rawQuery.moveToNext());
            rawQuery.close();
            return arrayList;
        }
        rawQuery.close();
        return arrayList;
    }

    public void deleteReminder(Reminder reminder) {
        this.database.delete("Reminder", "_id=?", new String[]{String.valueOf(reminder.getId())});
    }
}
