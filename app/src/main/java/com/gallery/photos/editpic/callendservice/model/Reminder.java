package com.gallery.photos.editpic.callendservice.model;


public class Reminder {
    int color;
    int id;
    String mobileNumber;
    long time;
    String title;

    public Reminder() {
    }

    public Reminder(int i, String str, long j, int i2, String str2) {
        this.id = i;
        this.title = str;
        this.time = j;
        this.color = i2;
        this.mobileNumber = str2;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int i) {
        this.id = i;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long j) {
        this.time = j;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int i) {
        this.color = i;
    }

    public String getMobileNumber() {
        return this.mobileNumber;
    }

    public void setMobileNumber(String str) {
        this.mobileNumber = str;
    }
}
