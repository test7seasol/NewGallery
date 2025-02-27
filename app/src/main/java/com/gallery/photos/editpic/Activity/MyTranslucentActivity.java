package com.gallery.photos.editpic.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.gallery.photos.editpic.databinding.TranslucentActivityMyBinding;


public class MyTranslucentActivity extends AppCompatActivity {

    TranslucentActivityMyBinding binding;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = TranslucentActivityMyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
