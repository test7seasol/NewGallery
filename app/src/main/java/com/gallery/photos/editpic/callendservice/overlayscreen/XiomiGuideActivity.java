package com.gallery.photos.editpic.callendservice.overlayscreen;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.gallery.photos.editpic.Activity.MyApplicationClass;
import com.gallery.photos.editpic.databinding.XiomiguideActivityBinding;


public class XiomiGuideActivity extends AppCompatActivity {

    XiomiguideActivityBinding binding;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = XiomiguideActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MyApplicationClass.Companion.setStatuaryPadding(binding.getRoot());

        binding.translayRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        String stringExtra = getIntent().getStringExtra("autostart");
        if (stringExtra != null) {
            binding.textTrans.setText(stringExtra);
        }

    }
}
