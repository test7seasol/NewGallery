package com.gallery.photos.editpic.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    lateinit var bind: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.apply {
            ivBack.onClick { finish() }
        }

    }
}