package com.gallery.photos.editpic.Activity

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.gallery.photos.editpic.Extensions.viewBinding
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.databinding.ActivityAllfilepermissionBinding
import java.util.Timer
import java.util.TimerTask

class AllFilePermissionActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityAllfilepermissionBinding::inflate)
    private var mPermissionForResult: ActivityResultLauncher<Intent>? = null
    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null
    private var permissionCheckTimer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        shineAnimation()

        mPermissionForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            checkAllFilesPermission()
        }

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Media permission granted, now request All Files Access
                checkAllFilesPermission()
            } else {
                // Handle permission denial
                onPermissionDenied()
            }
        }

        binding.btnGrant.setOnClickListener {
            requestMediaPermission()
        }
    }

    private fun requestMediaPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+ use READ_MEDIA_IMAGES and READ_MEDIA_VIDEO
            requestPermissionLauncher?.launch(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            // For older Android versions
            requestPermissionLauncher?.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun checkAllFilesPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (isAllFilesAccessGranted()) {
                onPermissionGranted()
            } else {
                requestAllFilesPermission()
            }
        } else {
            onPermissionGranted()
        }
    }

    private fun shineAnimation() {
        val anim = AnimationUtils.loadAnimation(this,
            R.anim.left_right)
        binding.shine.startAnimation(anim)

        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
                binding.shine.startAnimation(anim)
            }

            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    private fun requestAllFilesPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!isAllFilesAccessGranted()) {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:$packageName")
                    mPermissionForResult?.launch(intent)
                    startPermissionCheckTimer()
                } catch (e: Exception) {
                    Log.d("TAG", "requestAllFilesPermission: ${e.message}")
                    e.printStackTrace()
                }
            } else {
                onPermissionGranted()
            }
        } else {
            onPermissionGranted()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun isAllFilesAccessGranted(): Boolean {
        return Environment.isExternalStorageManager()
    }

    private fun onPermissionGranted() {
        permissionCheckTimer?.cancel()
        startActivity(Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        finishAffinity()
    }

    private fun onPermissionDenied() {
        // Handle what happens if media permissions are denied
        Log.d("TAG", "Media permissions denied")
    }

    private fun startPermissionCheckTimer() {
        permissionCheckTimer = Timer()
        permissionCheckTimer?.schedule(object : TimerTask() {
            override fun run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && isAllFilesAccessGranted()) {
                    runOnUiThread { onPermissionGranted() }
                }
            }
        }, 500, 500)
    }

    override fun onDestroy() {
        super.onDestroy()
        permissionCheckTimer?.cancel()
    }
}
