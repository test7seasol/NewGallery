package com.gallery.photos.editpic.Activity

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings.canDrawOverlays
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gallery.photos.editpic.Activity.MyApplicationClass.Companion.ctx
import com.gallery.photos.editpic.Extensions.ISONETIME
import com.gallery.photos.editpic.Extensions.PREF_LANGUAGE_CODE
import com.gallery.photos.editpic.Extensions.beGone
import com.gallery.photos.editpic.Extensions.beVisible
import com.gallery.photos.editpic.Extensions.setLanguageCode
import com.gallery.photos.editpic.Extensions.viewBinding
import com.gallery.photos.editpic.databinding.ActivitySplashBinding
import com.gallery.photos.editpic.myadsworld.MyAppOpenManager
import com.gallery.photos.editpic.myadsworld.MySplashAppOpenAds
import com.google.android.gms.ads.MobileAds

@SuppressLint("CustomSplashScreen")
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

        if (MyApplicationClass.getBoolean("ISAPPOPENONE") == false) {
//        launchActivity()
                onNextActivity()
        } else {
            onNextActivity()
        }
    }

    private fun initializeMobileAdsSdk() {
//        if (isMobileAdsInitializeCalled.getAndSet(true)) {
//            return
//        }
        // Initialize the Mobile Ads SDK.
        try {
            ctx.getData(
                this@SplashActivity
            )

            MobileAds.initialize(this) { initializationStatus ->
                Log.d("SplashActivity", "Mobile Ads SDK initialized: $initializationStatus")
            }
        } catch (e: Exception) {
            Log.e("SplashActivity", "Failed to initialize Mobile Ads SDK", e)
            // Optionally, track this failure or notify the user
            // e.g., Toast.makeText(this, "Ad features unavailable", Toast.LENGTH_SHORT).show()
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
                onNextActivity()
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

    /* private fun onNextActivity() {
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
                    *//* val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()*//*
                    val intent = Intent(this, AllFilePermissionActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }*/

    private fun onNextActivity() {
        MyApplicationClass.putBoolean("ISAPPOPENONE", true)

        if (!canDrawOverlays(this)) {
            startActivity(Intent(this, PermissionActivity::class.java))
            finish()
        } else {
            if (canDrawOverlays(this) && MyApplicationClass.getBoolean(ISONETIME) == true) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else if (MyApplicationClass.getBoolean(ISONETIME) == false) {
                startActivity(Intent(this, LanguageAct::class.java))
                finish()
            } else {
                startActivity(Intent(this, PermissionActivity::class.java))
                finish()
            }
        }
    }

    private fun requestMediaPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO
                ), 101
            )
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 101
            )
        }
    }


    private fun hasMediaPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            ) == PackageManager.PERMISSION_GRANTED
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}