package com.gallery.photos.editpic.ImageEDITModule.edit.activities;

import static com.gallery.photos.editpic.Extensions.ConstKt.setLanguageCode;
import static com.gallery.photos.editpic.Extensions.ExtKt.PREF_LANGUAGE_CODE;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.gallery.photos.editpic.Activity.MyApplicationClass;
import com.gallery.photos.editpic.callendservice.utils.CDOUtiler;

/* loaded from: classes.dex */
public class BaseActivity extends AppCompatActivity {
    public void isPermissionGranted(boolean z, String str) {
    }

    public void makeFullScreen() {
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
    }

    @Override
    // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 52) {
            isPermissionGranted(iArr[0] == 0, strArr[0]);
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setLanguageCode(this, MyApplicationClass.Companion.getString(PREF_LANGUAGE_CODE));

        try {
            CDOUtiler.hideNavigationBar(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            CDOUtiler.hideNavigationBar(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            try {
                CDOUtiler.hideNavigationBar(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
