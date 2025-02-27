package com.gallery.photos.editpic.callendservice;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.gallery.photos.editpic.callendservice.utils.CDOUtiler;

public class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            CDOUtiler.hideNavigationBar(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            CDOUtiler.hideNavigationBar(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (z) {
            try {
                CDOUtiler.hideNavigationBar(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
