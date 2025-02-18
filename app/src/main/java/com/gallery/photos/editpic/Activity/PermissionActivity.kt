package com.gallery.photos.editpic.Activity

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.gallery.photos.editpic.Extensions.viewBinding
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.databinding.ActivityPermissionBinding


class PermissionActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityPermissionBinding::inflate)
    private var mPermissionForResult: ActivityResultLauncher<Intent>? = null
    private val handler = Handler(Looper.getMainLooper())
    private val checkOverlayPermissionRunnable = object : Runnable {
        override fun run() {
            if (isOverlayPermissionGranted()) {
                goToNextActivity()
            } else {
                handler.postDelayed(this, 1000)  // Check again after 1 second
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        shineAnimation()

        mPermissionForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                // Handle the result of the activity here
            }

        binding.btnGrant.setOnClickListener {
            askOverlayPermission()
        }
    }

    private fun askOverlayPermission() {
        try {
            mPermissionForResult?.launch(
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            )
        } catch (e: Exception) {
            Log.d("TAG", "askOverlayPermission: ${e.message}")
            e.printStackTrace()
        }

        // Start checking for permission every second
        handler.postDelayed(checkOverlayPermissionRunnable, 1000)
    }

    private fun isOverlayPermissionGranted(): Boolean {
        val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        return appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, android.os.Process.myUid(), packageName
        ) == AppOpsManager.MODE_ALLOWED
    }

    private fun goToNextActivity() {
        handler.removeCallbacks(checkOverlayPermissionRunnable)  // Stop checking
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            startActivity(Intent(this, AllFilePermissionActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } else {
            startActivity(Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
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
        handler.removeCallbacks(checkOverlayPermissionRunnable)  // Stop checking when activity is destroyed
    }
}
