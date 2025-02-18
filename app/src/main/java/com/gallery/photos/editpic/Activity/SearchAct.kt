package com.gallery.photos.editpic.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gallery.photos.editpic.databinding.ActivitySearchBinding

class SearchAct : AppCompatActivity() {
    lateinit var bind: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.apply {
        }
    }
}