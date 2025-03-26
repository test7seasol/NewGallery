package com.gallery.photos.editpic.Activity

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
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
    private val handler = Handler(Looper.getMainLooper())
    private val splashDelay = 2000L // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            // Set content view before super.onCreate to prevent window leaks
            setContentView(binding.root)
            super.onCreate(savedInstanceState)

            // Initialize language settings
            setLanguageCode(this, MyApplicationClass.getString(PREF_LANGUAGE_CODE) ?: "en")

            // Initialize default language if not set
            if (MyApplicationClass.getString(PREF_LANGUAGE_CODE).isNullOrEmpty()) {
                MyApplicationClass.putString(PREF_LANGUAGE_CODE, "en")
            }

            // Initialize ads
            initializeMobileAdsSdk()

            // Start next activity with delay
            handler.postDelayed({
                if (!isFinishing && !isDestroyed) {
                    launchAppropriateActivity()
                }
            }, splashDelay)

        } catch (e: Exception) {
            Log.e("SplashActivity", "Error in onCreate", e)
            // Attempt to recover by immediately starting next activity
            launchAppropriateActivity()
        }
    }

    private fun initializeMobileAdsSdk() {
        try {
            ctx.getData(this)
            MobileAds.initialize(this) { initializationStatus ->
                Log.d("SplashActivity", "Mobile Ads SDK initialized: $initializationStatus")
            }
        } catch (e: Exception) {
            Log.e("SplashActivity", "Failed to initialize Mobile Ads SDK", e)
        }
    }

    private fun launchAppropriateActivity() {
        try {
            MyApplicationClass.putBoolean("ISAPPOPENONE", true)
            binding.progressid.beVisible()

            if (MyApplicationClass.getBoolean("ISAPPOPENONE") == false) {
                MyAppOpenManager.Strcheckad = "StrClosed"
                MySplashAppOpenAds.SplashAppOpenShow(this) {
                    proceedToNextActivity()
                }
            } else {
                proceedToNextActivity()
            }
        } catch (e: Exception) {
            Log.e("SplashActivity", "Error launching activity", e)
            proceedToNextActivity() // Fallback
        }
    }

    private fun proceedToNextActivity() {
        try {
            if (!Settings.canDrawOverlays(this)) {
                startActivitySafely(PermissionActivity::class.java)
            } else {
                when {
                    !MyApplicationClass.getBoolean(ISONETIME)!! -> {
                        startActivitySafely(LanguageAct::class.java)
                    }
                    hasAllRequiredPermissions() -> {
                        startActivitySafely(MainActivity::class.java)
                    }
                    else -> {
                        startActivitySafely(PermissionActivity::class.java)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SplashActivity", "Error in proceedToNextActivity", e)
            // Last resort recovery
            restartApplication()
        }
    }

    private fun startActivitySafely(activityClass: Class<*>) {
        try {
            val intent = Intent(this, activityClass)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } catch (e: Exception) {
            Log.e("SplashActivity", "Failed to start activity", e)
            restartApplication()
        }
    }

    private fun restartApplication() {
        try {
            val pm = packageManager
            val intent = pm.getLaunchIntentForPackage(packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finishAffinity()
        } catch (e: Exception) {
            Log.e("SplashActivity", "Failed to restart application", e)
        }
    }

    private fun hasAllRequiredPermissions(): Boolean {
        return canDrawOverlays(this) && hasMediaPermissions()
    }

    private fun hasAllFilesAccess(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun hasMediaPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            ) == PackageManager.PERMISSION_GRANTED
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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

    override fun onDestroy() {
        super.onDestroy()
        // Clean up handlers to prevent memory leaks
        handler.removeCallbacksAndMessages(null)
    }
}