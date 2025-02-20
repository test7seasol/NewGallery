package com.gallery.photos.editpic.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gallery.photos.editpic.Extensions.PREF_LANGUAGE_CODE
import com.gallery.photos.editpic.Extensions.setLanguageCode
import com.gallery.photos.editpic.databinding.ActivitySearchBinding

class SearchAct : AppCompatActivity() {
    lateinit var bind: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguageCode(this, MyApplicationClass.getString(PREF_LANGUAGE_CODE)!!)
        bind = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.apply {
        }
    }
}