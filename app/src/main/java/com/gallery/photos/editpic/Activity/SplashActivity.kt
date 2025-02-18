package com.gallery.photos.editpic.Activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.gallery.photos.editpic.Extensions.ISONETIME
import com.gallery.photos.editpic.Extensions.viewBinding
import com.gallery.photos.editpic.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivitySplashBinding::inflate)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        delayTime(1000) {
        launchActivity()
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
    }

    private fun launchActivity() {
        if (MyApplicationClass.getBoolean(ISONETIME) == false) {
            startActivity(Intent(this, LanguageAct::class.java))
            finish()
        } else {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(this, PermissionActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}