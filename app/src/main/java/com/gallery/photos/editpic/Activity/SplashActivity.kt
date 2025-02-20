package com.gallery.photos.editpic.Activity

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gallery.photos.editpic.Activity.MyApplicationClass.Companion.ctx
import com.gallery.photos.editpic.Extensions.ISONETIME
import com.gallery.photos.editpic.Extensions.PREF_LANGUAGE_CODE
import com.gallery.photos.editpic.Extensions.beGone
import com.gallery.photos.editpic.Extensions.beVisible
import com.gallery.photos.editpic.Extensions.delayTime
import com.gallery.photos.editpic.Extensions.setLanguageCode
import com.gallery.photos.editpic.Extensions.viewBinding
import com.gallery.photos.editpic.databinding.ActivitySplashBinding
import com.gallery.photos.editpic.myadsworld.MyAppOpenManager
import com.gallery.photos.editpic.myadsworld.MySplashAppOpenAds
import com.google.android.gms.ads.MobileAds

class SplashActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivitySplashBinding::inflate)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguageCode(this, MyApplicationClass.getString(PREF_LANGUAGE_CODE)!!)

        setContentView(binding.root)

        initializeMobileAdsSdk()

        if (MyApplicationClass.getString(PREF_LANGUAGE_CODE)!!.isEmpty()) {
            MyApplicationClass.putString(PREF_LANGUAGE_CODE, "en")
        }

        delayTime(1000) {
//        launchActivity()

            onNextActivity()
        }
    }

    private fun initializeMobileAdsSdk() {
//        if (isMobileAdsInitializeCalled.getAndSet(true)) {
//            return
//        }
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(
            this
        ) {
            ctx.getData(
                this@SplashActivity
            )
        }
    }

    private fun launchActivity() {
        binding.progressid.beVisible()
        if (MyApplicationClass.getBoolean("ISAPPOPENONE") == false) {
            MyAppOpenManager.Strcheckad = "StrClosed"
            MySplashAppOpenAds.SplashAppOpenShow(
                this@SplashActivity
            ) {
                onNextActivity()
            }
        } else {
            binding.progressid.beGone()
            delayTime(500) {
                onNextActivity()
            }
        }
    }

    private fun hasAllFilesAccess(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                this, READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun onNextActivity() {
        MyApplicationClass.putBoolean("ISAPPOPENONE", true)
        if (MyApplicationClass.getBoolean(ISONETIME) == false) {
            startActivity(Intent(this, LanguageAct::class.java))
            finish()
        } else {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(this, PermissionActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                if (hasAllFilesAccess()) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, AllFilePermissionActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}