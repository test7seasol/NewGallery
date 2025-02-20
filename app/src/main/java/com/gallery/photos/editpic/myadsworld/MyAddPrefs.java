package com.gallery.photos.editpic.myadsworld;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MyAddPrefs {
    public SharedPreferences prefs;
    public Editor editor;

    public MyAddPrefs(Context context) {
        prefs = context.getSharedPreferences("USER PREFS", Context.MODE_PRIVATE);
        editor = this.prefs.edit();
    }

    public void setAdmAppOpenId(String sid) {
        editor.putString("admappid", sid).commit();
    }

    public String getAdmAppOpenId() {
        return prefs.getString("admappid", "ca");
    }

    public void setAdmBannerId(String sid) {
        editor.putString("admbannerid", sid).commit();
    }

    public String getAdmBannerId() {
        return prefs.getString("admbannerid", "ca");
    }

    public void setAdmInterId(String sid) {
        editor.putString("adminterid", sid).commit();
    }

    public String getAdmInterId() {
        return prefs.getString("adminterid", "ca");
    }


    public void setAdmNativeId(String sid) {
        editor.putString("admnativeid", sid).commit();
    }

    public String getAdmCallEndNativeId() {
//        return "ca-app-pub-2524225827032053/3613276134";
        return "ca-app-pub-3940256099942544/2247696110";
    }

    public String getAdmNativeId() {
        return prefs.getString("admnativeid", "ca");
    }

    public void setsecAdmNativeId(String sid) {
        editor.putString("secNativeId", sid).commit();
    }

    public String getsecAdmNativeId() {
        return prefs.getString("secNativeId", "ca");
    }

    public void setSecAdmInterId(String sid) {
        editor.putString("secInterstialId", sid).commit();
    }

    public String getSecAdmInterId() {
        return prefs.getString("secInterstialId", "ca");
    }

    public void setCustomInterstial(int sid) {
        editor.putInt("customInterstial", sid).commit();
    }

    public int getCustomInterstial() {
        return prefs.getInt("customInterstial", 0);
    }


    public void setAdmShowclick(int num) {
        editor.putInt("AdmShowclick", num).commit();
    }

    public int getAdmShowclick() {
        return prefs.getInt("AdmShowclick", 1);
    }

    public void setButtonColor(String sid) {
        editor.putString("addButtonColor", sid).commit();
    }

    public String getButtonColor() {
        return prefs.getString("addButtonColor", "#2F9E33");
    }

    public int getNativeAdmShowclick() {
        return prefs.getInt("NativeAdmShowclick", 1);
    }

    public void setTextColor(String sid) {
        editor.putString("addTextColor", sid).commit();
    }

    public String getTextColor() {
        return prefs.getString("addTextColor", "#007AF5");
    }

    public void setbgColor(String sid) {
        editor.putString("addbgColor", sid).commit();
    }

    public String getbgColor() {
        return prefs.getString("addbgColor", "#007AF5");
    }


    public void setNativeAdmShowclick(int num) {
        editor.putInt("NativeAdmShowclick", num).commit();
    }


    public void setUploadContact(String sid) {
        editor.putString("upload_api", sid).commit();
    }

    public String getUploadContact() {
        return prefs.getString("upload_api", "");
    }


    public void setIncomingAppOpen(int num) {
        editor.putInt("InComingScreenAppOpen", num).commit();
    }

    public int getIncomingAppOpen() {
        return prefs.getInt("InComingScreenAppOpen", 1);
    }


    public void setSplashInterAppOpen(int sid) {
        editor.putInt("splash_inter_appopen", sid).commit();
    }

    public int getSplashInterAppOpen() {
        return prefs.getInt("splash_inter_appopen", 1);
    }

}
