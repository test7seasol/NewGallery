package com.gallery.photos.editpic.Activity

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gallery.photos.editpic.Extensions.PREF_LANGUAGE_CODE
import com.gallery.photos.editpic.Extensions.delayTime
import com.gallery.photos.editpic.Extensions.setLanguageCode
import com.gallery.photos.editpic.Extensions.startActivityWithBundle
import com.gallery.photos.editpic.Extensions.viewBinding
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.databinding.ActivityPermissionBinding

class PermissionActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityPermissionBinding::inflate)
    private val handler = Handler(Looper.getMainLooper())
    private var overlayPermissionLauncher: ActivityResultLauncher<Intent>? = null
    private var readPhoneStatePermissionLauncher: ActivityResultLauncher<String>? = null

    private val checkOverlayPermissionRunnable = object : Runnable {
        override fun run() {
            if (isOverlayPermissionGranted()) {
                goToNextActivity()
            } else {
                handler.postDelayed(this, 1000)  // Keep checking every second
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguageCode(this, MyApplicationClass.getString(PREF_LANGUAGE_CODE)!!)
        setContentView(binding.root)

        shineAnimation()

        // Register for overlay permission result
        overlayPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                checkOverlayPermissionAfterDelay()
            }

        // Register for READ_PHONE_STATE permission result
        readPhoneStatePermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) {
                    askOverlayPermission()  // If granted, move to overlay permission
                } else {
                    Log.e(
                        "com.gallery.photos.editpic.Activity.PermissionActivity",
                        "READ_PHONE_STATE permission denied!"
                    )
                }
            }

        binding.btnGrant.setOnClickListener {

            checkReadPhoneStatePermission()
        }
    }

    private fun checkReadPhoneStatePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                readPhoneStatePermissionLauncher?.launch(Manifest.permission.READ_PHONE_STATE)
            } else {
                askOverlayPermission()  // If already granted, move to overlay permission
            }
        } else {
            askOverlayPermission()  // No need to ask on lower Android versions
        }
    }

    private fun askOverlayPermission() {
        delayTime(200) {
            startActivityWithBundle<MyTranslucentActivity>()
        }

        try {
            overlayPermissionLauncher?.launch(
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            )
        } catch (e: Exception) {
            Log.e("TAG", "askOverlayPermission: ${e.message}")
        }

        // Start checking for permission every second
        handler.postDelayed(checkOverlayPermissionRunnable, 1000)
    }

    private fun isOverlayPermissionGranted(): Boolean {
        val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        return appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, Process.myUid(), packageName
        ) == AppOpsManager.MODE_ALLOWED
    }

    private fun checkOverlayPermissionAfterDelay() {
        handler.postDelayed(checkOverlayPermissionRunnable, 1000)
    }

    private fun goToNextActivity() {
        /*  startActivity(Intent(this, MainActivity::class.java).apply {
              addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
              addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
              addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          })

          finishAffinity()*/

        val nextActivity = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            MainActivity::class.java
        } else {
            MainActivity::class.java
        }
        startActivity(Intent(this, nextActivity).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        finishAffinity()
    }

    private fun shineAnimation() {
        val anim = AnimationUtils.loadAnimation(this, R.anim.left_right)
        binding.shine.startAnimation(anim)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(p0: Animation?) {
                binding.shine.startAnimation(anim)
            }
            override fun onAnimationStart(p0: Animation?) {}
            override fun onAnimationRepeat(p0: Animation?) {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(checkOverlayPermissionRunnable)  // Stop checking
    }
}
